package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.*;
import com.github.utransnet.graphenej.errors.RepeatedRequestIdException;
import com.github.utransnet.graphenej.interfaces.NodeErrorListener;
import com.github.utransnet.graphenej.interfaces.SubscriptionHub;
import com.github.utransnet.graphenej.interfaces.SubscriptionListener;
import com.github.utransnet.graphenej.models.ApiCall;
import com.github.utransnet.graphenej.models.DynamicGlobalProperties;
import com.github.utransnet.graphenej.models.SubscriptionResponse;
import com.github.utransnet.graphenej.models.WitnessResponse;
import com.github.utransnet.graphenej.objects.Memo;
import com.github.utransnet.graphenej.operations.CustomOperation;
import com.github.utransnet.graphenej.operations.LimitOrderCreateOperation;
import com.github.utransnet.graphenej.operations.TransferOperation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A WebSocket adapter prepared to be used as a basic dispatch hub for subscription messages.
 */
public class SubscriptionMessagesHub extends BaseGrapheneHandler implements SubscriptionHub {

    // Sequence of message ids
    public final static int LOGIN_ID = 1;
    public final static int GET_DATABASE_ID = 2;
    public final static int SUBSCRIPTION_REQUEST = 3;
    // ID of subscription notifications
    public final static int SUBSCRIPTION_NOTIFICATION = 4;
    /**
     * Id attributed to the indivitual 'get_objects' API call required for a fine-grained
     * subscription request.
     */
    public final static int MANUAL_SUBSCRIPTION_ID = 5;
    private WebSocket mWebsocket;
    private SubscriptionResponse.SubscriptionResponseDeserializer mSubscriptionDeserializer;
    private Gson gson;
    private String user;
    private String password;
    private boolean clearFilter;
    private int currentId;
    private int databaseApiId = -1;
    private int subscriptionCounter = 0;
    private HashMap<Long, BaseGrapheneHandler> mHandlerMap = new HashMap<>();
    private List<BaseGrapheneHandler> pendingHandlerList = new ArrayList<>();

    // State variables
    private boolean isUnsubscribing;
    private boolean isSubscribed;
    private boolean isAccountsSubscribed = false;

    /**
     * Constructor used to create a subscription message hub that will call the set_subscribe_callback
     * API with the clear_filter parameter set to false, meaning that it will only receive automatic updates
     * from objects we register.
     * <p>
     * A list of ObjectTypes must be provided, otherwise we won't get any update.
     *
     * @param user          User name, in case the node to which we're going to connect to requires
     *                      authentication
     * @param password      Password, same as above
     * @param clearFilter   Whether to automatically subscribe of not to the notification feed.
     * @param errorListener Callback that will be fired in case there is an error.
     */
    public SubscriptionMessagesHub(String user, String password, boolean clearFilter, NodeErrorListener errorListener) {
        super(errorListener);
        this.user = user;
        this.password = password;
        this.clearFilter = clearFilter;
        this.mSubscriptionDeserializer = new SubscriptionResponse.SubscriptionResponseDeserializer();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SubscriptionResponse.class, mSubscriptionDeserializer);
        builder.registerTypeAdapter(Transaction.class, new Transaction.TransactionDeserializer());
        builder.registerTypeAdapter(TransferOperation.class, new TransferOperation.TransferDeserializer());
        builder.registerTypeAdapter(LimitOrderCreateOperation.class, new LimitOrderCreateOperation.LimitOrderCreateDeserializer());
        builder.registerTypeAdapter(CustomOperation.class, new CustomOperation.CustomOperationDeserializer());
        builder.registerTypeAdapter(AssetAmount.class, new AssetAmount.AssetAmountDeserializer());
        builder.registerTypeAdapter(UserAccount.class, new UserAccount.UserAccountSimpleDeserializer());
        builder.registerTypeAdapter(DynamicGlobalProperties.class, new DynamicGlobalProperties.DynamicGlobalPropertiesDeserializer());
        builder.registerTypeAdapter(Memo.class, new Memo.MemoDeserializer());
        this.gson = builder.create();
    }

    /**
     * Constructor used to create a subscription message hub that will call the
     * set_subscribe_callback API with the clear_filter parameter set to false, meaning that it will
     * only receive updates from objects we register.
     *
     * @param user          User name, in case the node to which we're going to connect to requires
     *                      authentication
     * @param password      Password, same as above
     * @param errorListener Callback that will be fired in case there is an error.
     */
    public SubscriptionMessagesHub(String user, String password, NodeErrorListener errorListener) {
        this(user, password, false, errorListener);
    }

    @Override
    public void addSubscriptionListener(SubscriptionListener listener) {
        this.mSubscriptionDeserializer.addSubscriptionListener(listener);
    }

    @Override
    public void removeSubscriptionListener(SubscriptionListener listener) {
        this.mSubscriptionDeserializer.removeSubscriptionListener(listener);
    }

    @Override
    public List<SubscriptionListener> getSubscriptionListeners() {
        return this.mSubscriptionDeserializer.getSubscriptionListeners();
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        this.mWebsocket = websocket;
        ArrayList<Serializable> loginParams = new ArrayList<>();
        currentId = LOGIN_ID;
        loginParams.add(user);
        loginParams.add(password);
        ApiCall loginCall = new ApiCall(1, RPC.CALL_LOGIN, loginParams, RPC.VERSION, currentId);
        websocket.sendText(loginCall.toJsonString());
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        String message = frame.getPayloadText();
        System.out.println("<< " + message);
        if (currentId == LOGIN_ID) {
            requestApiId(websocket);
        } else if (currentId == GET_DATABASE_ID) {
            Type ApiIdResponse = new TypeToken<WitnessResponse<Integer>>() {
            }.getType();
            WitnessResponse<Integer> witnessResponse = gson.fromJson(message, ApiIdResponse);
            databaseApiId = witnessResponse.result;

            // Subscribing only if the clearFilter parameter is true
            /*if (clearFilter) {
                subscribe();
            }*/
            subscribe();

            // Dispatching the onConnected event to every pending handler
            if (pendingHandlerList.size() > 0) {
                for (BaseGrapheneHandler handler : pendingHandlerList) {
                    handler.setRequestId(++currentId);
                    dispatchConnectionEvent(handler);
                }
                pendingHandlerList.clear();
            }
        } else if (currentId >= SUBSCRIPTION_REQUEST) {
            List<SubscriptionListener> subscriptionListeners = mSubscriptionDeserializer.getSubscriptionListeners();

            if (!isUnsubscribing) {
                isSubscribed = true;
            }

            // If we haven't subscribed to all requested subscription channels yet,
            // just send one more subscription
            if (subscriptionListeners != null &&
                    subscriptionListeners.size() > 0 &&
                    subscriptionCounter < subscriptionListeners.size()) {

                if (!isAccountsSubscribed) {
                    subscribeForAccounts(websocket, subscriptionListeners);
                }
                subscribeForObjects(websocket, subscriptionListeners);

            } else {
                receiveNotice(websocket, frame, message);
            }
        }
    }

    private void receiveNotice(WebSocket websocket, WebSocketFrame frame, String message) throws Exception {
        WitnessResponse witnessResponse = gson.fromJson(message, WitnessResponse.class);
        if (witnessResponse.result != null &&
                mHandlerMap.get(witnessResponse.id) != null) {
            // This is the response to a request that was submitted to the message hub
            // and whose handler was stored in the "request id" -> "handler" map
            BaseGrapheneHandler handler = mHandlerMap.get(witnessResponse.id);
            handler.onTextFrame(websocket, frame);
            mHandlerMap.remove(witnessResponse.id);
        } else {
            // If we've already subscribed to all requested subscription channels, we
            // just proceed to deserialize content.
            // The deserialization is handled by all those TypeAdapters registered in the class
            // constructor while building the gson instance.
            SubscriptionResponse response = gson.fromJson(message, SubscriptionResponse.class);
        }
    }

    private void subscribeForAccounts(WebSocket websocket, List<SubscriptionListener> subscriptionListeners) {
        ArrayList<Serializable> accountNames = new ArrayList<>();
        for (SubscriptionListener listener : subscriptionListeners) {
            accountNames.addAll(listener.getInterestedAccountNames());
        }
        if (!accountNames.isEmpty()) {
            ArrayList<Serializable> payload = new ArrayList<>();
            payload.add(accountNames);
            payload.add(true);
            ApiCall subscribe = new ApiCall(databaseApiId, RPC.CALL_GET_FULL_ACCOUNTS, payload, RPC.VERSION, ++currentId);
            websocket.sendText(subscribe.toJsonString());
        }
        isAccountsSubscribed = true;
    }

    private void subscribeForObjects(WebSocket websocket, List<SubscriptionListener> subscriptionListeners) {
        ArrayList<Serializable> payload = new ArrayList<>();
        ArrayList<Serializable> objects = new ArrayList<>();
        for (SubscriptionListener listener : subscriptionListeners) {
            ObjectType interestObjectType = listener.getInterestObjectType();
            if (interestObjectType != null) {
                objects.add(interestObjectType.getGenericObjectId());
            }
        }
        payload.add(objects);
        subscriptionCounter = subscriptionListeners.size();
        ApiCall subscribe = new ApiCall(databaseApiId, RPC.GET_OBJECTS, payload, RPC.VERSION, ++currentId);
        websocket.sendText(subscribe.toJsonString());
    }

    private void requestApiId(WebSocket websocket) {
        currentId = GET_DATABASE_ID;
        ArrayList<Serializable> emptyParams = new ArrayList<>();
        ApiCall getDatabaseId = new ApiCall(1, RPC.CALL_DATABASE, emptyParams, RPC.VERSION, currentId);
        websocket.sendText(getDatabaseId.toJsonString());
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        System.out.println(">> " + frame.getPayloadText());
    }

    /**
     * Private method that sends a subscription request to the full node
     */
    private void subscribe() {
        isUnsubscribing = false;

        currentId++;
        ArrayList<Serializable> subscriptionParams = new ArrayList<>();
        subscriptionParams.add(String.format("%d", SUBSCRIPTION_NOTIFICATION));
        subscriptionParams.add(clearFilter);
        ApiCall getDatabaseId = new ApiCall(databaseApiId, RPC.CALL_SET_SUBSCRIBE_CALLBACK, subscriptionParams, RPC.VERSION, currentId);
        mWebsocket.sendText(getDatabaseId.toJsonString());
    }

    /**
     * Public method used to re-establish a subscription after it was cancelled by a previous
     * call to the {@see #cancelSubscriptions()} method call.
     * <p>
     * Please note that you should repeat the registration step for every interested listener, since
     * those were probably lost after the previous {@see #cancelSubscriptions()} method call.
     */
    public void resubscribe() {
        if (mWebsocket.isOpen()) {
            subscribe();
        } else {
            throw new IllegalStateException("Websocket is not open, can't resubscribe");
        }
    }

    /**
     * Method that sends a subscription cancellation request to the full node, and also
     * de-registers all subscription and request listeners.
     */
    public void cancelSubscriptions() {
        isSubscribed = false;
        isUnsubscribing = true;

        currentId++;
        ApiCall unsubscribe = new ApiCall(databaseApiId, RPC.CALL_CANCEL_ALL_SUBSCRIPTIONS, new ArrayList<Serializable>(), RPC.VERSION, currentId);
        mWebsocket.sendText(unsubscribe.toJsonString());

        // Clearing all subscription listeners
        mSubscriptionDeserializer.clearAllSubscriptionListeners();

        // Clearing all request handler listners
        mHandlerMap.clear();
    }

    /**
     * Method used to check the current state of the connection.
     *
     * @return True if the websocket is open and there is an active subscription, false otherwise.
     */
    public boolean isSubscribed() {
        return this.mWebsocket.isOpen() && isSubscribed;
    }

    /**
     * Method used to reset all internal variables.
     */
    public void reset() {
        currentId = 0;
        databaseApiId = -1;
        subscriptionCounter = 0;
    }

    /**
     * Adds a handler either to the map of handlers or to a list of pending ones
     *
     * @param handler The handler of a given request
     */
    public void addRequestHandler(BaseGrapheneHandler handler) {
        if (mWebsocket != null && currentId > SUBSCRIPTION_REQUEST) {
            handler.setRequestId(++currentId);
            mHandlerMap.put(handler.getRequestId(), handler);
            dispatchConnectionEvent(handler);
        } else {
            pendingHandlerList.add(handler);
        }
    }

    /**
     * Informing a handler that we have a connection with the full node.
     *
     * @param handler Handler that should be notified.
     */
    private void dispatchConnectionEvent(BaseGrapheneHandler handler) {
        try {
            // Artificially calling the 'onConnected' method of the handler.
            // The underlying websocket was already connected, but from the WebSocketAdapter
            // point of view it doesn't make a difference.
            handler.onConnected(mWebsocket, null);
        } catch (Exception e) {
            System.out.println("Exception. Msg: " + e.getMessage());
            System.out.println("Exception type: " + e);
            for (StackTraceElement el : e.getStackTrace()) {
                System.out.println(String.format("at %s.%s(%s:%s)",
                        el.getClassName(),
                        el.getMethodName(),
                        el.getFileName(),
                        el.getLineNumber()));
            }
        }
    }
}
