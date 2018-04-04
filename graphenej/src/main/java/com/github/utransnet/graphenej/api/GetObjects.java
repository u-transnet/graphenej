package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.*;
import com.github.utransnet.graphenej.interfaces.WitnessResponseListener;
import com.github.utransnet.graphenej.models.ApiCall;
import com.github.utransnet.graphenej.models.BitAssetData;
import com.github.utransnet.graphenej.models.WitnessResponse;
import com.github.utransnet.graphenej.objects.Proposal;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that implements get_objects request handler.
 * <p>
 * Get the objects corresponding to the provided IDs.
 * <p>
 * The response returns a list of objects retrieved, in the order they are mentioned in ids
 *
 * @see <a href="https://goo.gl/isRfeg">get_objects API doc</a>
 */
public class GetObjects extends BaseGrapheneHandler {
    private List<String> ids;

    private boolean mOneTime;

    /**
     * Default Constructor
     *
     * @param ids      list of IDs of the objects to retrieve
     * @param oneTime  boolean value indicating if WebSocket must be closed (true) or not
     *                 (false) after the response
     * @param listener A class implementing the WitnessResponseListener interface. This should
     *                 be implemented by the party interested in being notified about the
     *                 success/failure of the operation.
     */
    public GetObjects(List<String> ids, boolean oneTime, WitnessResponseListener listener) {
        super(listener);
        this.ids = ids;
        this.mOneTime = oneTime;
    }

    /**
     * Using this constructor the WebSocket connection closes after the response.
     *
     * @param ids      list of IDs of the objects to retrieve
     * @param listener A class implementing the WitnessResponseListener interface. This should
     *                 be implemented by the party interested in being notified about the
     *                 success/failure of the operation.
     */
    public GetObjects(List<String> ids, WitnessResponseListener listener) {
        this(ids, true, listener);
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        ArrayList<Serializable> params = new ArrayList<>();
        ArrayList<Serializable> subParams = new ArrayList<>();
        for (String id : this.ids) {
            subParams.add(id);
        }
        params.add(subParams);
        ApiCall apiCall = new ApiCall(0, RPC.GET_OBJECTS, params, RPC.VERSION, 0);
        websocket.sendText(apiCall.toJsonString());
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        if (frame.isTextFrame()) {
            System.out.println("<< " + frame.getPayloadText());
        }
        String response = frame.getPayloadText();
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(AssetAmount.class, new AssetAmount.AssetAmountDeserializer());
        gsonBuilder.registerTypeAdapter(Asset.class, new Asset.AssetDeserializer());
        gsonBuilder.registerTypeAdapter(Authority.class, new Authority.AuthorityDeserializer());
        gsonBuilder.registerTypeAdapter(AccountOptions.class, new AccountOptions.AccountOptionsDeserializer());

//        gsonBuilder.registerTypeAdapter(BaseOperation.class, new BaseOperation.BaseOperationDeserializer());
//        gsonBuilder.registerTypeAdapter(TransferOperation.class, new TransferOperation.TransferDeserializer());
//        gsonBuilder.registerTypeAdapter(ProposalCreateOperation.class, new ProposalCreateOperation.ProposalCreateDeserializer());
//        gsonBuilder.registerTypeAdapter(Memo.class, new Memo.MemoDeserializer());
        gsonBuilder.registerTypeAdapter(Proposal.class, new Proposal.ProposalDeserializer());
        gsonBuilder.registerTypeAdapter(PointInTime.class, new PointInTime.PointInTimeDeserializer());
        gsonBuilder.registerTypeAdapter(OperationWrapper.class, new OperationWrapper.OperationWrapperDeserializer());
        gsonBuilder.registerTypeAdapter(UInt32.class, new UInt32.UInt32Deserializer());
        Gson gson;

        List<GrapheneObject> parsedResult = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonArray resultArray = parser.parse(response).getAsJsonObject().get(WitnessResponse.KEY_RESULT).getAsJsonArray();
        for (JsonElement element : resultArray) {
            if (element.isJsonNull()) {
                continue;
            }
            String id = element.getAsJsonObject().get(GrapheneObject.KEY_ID).getAsString();
            GrapheneObject grapheneObject = new GrapheneObject(id);
            switch (grapheneObject.getObjectType()) {
                case ASSET_OBJECT:
                    gson = gsonBuilder.create();
                    Asset asset = gson.fromJson(element, Asset.class);
                    parsedResult.add(asset);
                    break;
                case ACCOUNT_OBJECT:
                    gsonBuilder.registerTypeAdapter(UserAccount.class, new UserAccount.UserAccountFullDeserializer());
                    gson = gsonBuilder.create();
                    UserAccount account = gson.fromJson(element, UserAccount.class);
                    parsedResult.add(account);
                    break;
                case ASSET_BITASSET_DATA:
                    gson = gsonBuilder.create();
                    Type BitAssetDataType = new TypeToken<WitnessResponse<List<BitAssetData>>>() {
                    }.getType();
                    WitnessResponse<List<BitAssetData>> witnessResponse = gson.fromJson(response, BitAssetDataType);
                    BitAssetData bitAssetData = witnessResponse.result.get(0);
                    parsedResult.add(bitAssetData);
                    break;
                case PROPOSAL_OBJECT:
                    gsonBuilder.registerTypeAdapter(UserAccount.class, new UserAccount.UserAccountSimpleDeserializer());
                    gson = gsonBuilder.create();
                    Proposal proposal = gson.fromJson(element, Proposal.class);
                    parsedResult.add(proposal);
                    break;
            }
        }

        WitnessResponse<List<GrapheneObject>> output = new WitnessResponse<>();
        output.result = parsedResult;
        mListener.onSuccess(output);
        if (mOneTime) {
            websocket.disconnect();
        }
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        if (frame.isTextFrame()) {
            System.out.println(">> " + frame.getPayloadText());
        }
    }
}
