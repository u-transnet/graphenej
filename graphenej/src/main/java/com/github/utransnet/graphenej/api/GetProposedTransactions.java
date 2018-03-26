package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.*;
import com.github.utransnet.graphenej.interfaces.WitnessResponseListener;
import com.github.utransnet.graphenej.models.AccountProperties;
import com.github.utransnet.graphenej.models.ApiCall;
import com.github.utransnet.graphenej.models.WitnessResponse;
import com.github.utransnet.graphenej.objects.Memo;
import com.github.utransnet.graphenej.objects.Proposal;
import com.github.utransnet.graphenej.objects.ProposedTransaction;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Class that implements get_accounts request handler.
 *
 *  Get a list of accounts by ID.
 *
 *  The response returns the accounts corresponding to the provided IDs.
 *
 *  @see <a href="https://goo.gl/r5RqKG">get_accounts API doc</a>
 */
public class GetProposedTransactions extends BaseGrapheneHandler {
    private String accountId;
    private UserAccount userAccount;
    private WitnessResponseListener mListener;
    private boolean mOneTime;

    public GetProposedTransactions(String accountId, WitnessResponseListener listener){
        this(accountId, true, listener);
    }

    public GetProposedTransactions(UserAccount account, WitnessResponseListener listener){
        this(account.getObjectId(), listener);
    }

    public GetProposedTransactions(String accountId, boolean mOneTime, WitnessResponseListener listener){
        super(listener);
        this.accountId = accountId;
        this.mListener = listener;
        this.mOneTime = mOneTime;
    }

    public GetProposedTransactions(UserAccount account, boolean mOneTime, WitnessResponseListener listener){
        this(account.getObjectId(), mOneTime, listener);
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        ArrayList<Serializable> params = new ArrayList<>();
        params.add(accountId);
        ApiCall getAccountByAddress = new ApiCall(0, RPC.CALL_GET_PROPOSED_TRANSACTIONS, params, RPC.VERSION, (int) requestId);
        websocket.sendText(getAccountByAddress.toJsonString());
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        System.out.println("<<< "+frame.getPayloadText());
        String response = frame.getPayloadText();
        GsonBuilder builder = new GsonBuilder();

        Type getPoposedTransactionsResponseType = new TypeToken<WitnessResponse<List<Proposal>>>() {}.getType();
        builder.registerTypeAdapter(Proposal.class, new Proposal.ProposalDeserializer());
        builder.registerTypeAdapter(PointInTime.class, new PointInTime.PointInTimeDeserializer());
        builder.registerTypeAdapter(UserAccount.class, new UserAccount.UserAccountSimpleDeserializer());
        builder.registerTypeAdapter(ProposedTransaction.class, new ProposedTransaction.ProposedTransactionDeserializer());
        builder.registerTypeAdapter(AssetAmount.class, new AssetAmount.AssetAmountDeserializer());
        builder.registerTypeAdapter(Memo.class, new Memo.MemoDeserializer());
        builder.registerTypeAdapter(Extensions.class, new Extensions.ExtensionsDeserializer());
        WitnessResponse<List<Proposal>> witnessResponse = builder.create().fromJson(response, getPoposedTransactionsResponseType);

        if (witnessResponse.error != null) {
            this.mListener.onError(witnessResponse.error);
        } else {
            this.mListener.onSuccess(witnessResponse);
        }
        if(mOneTime){
            websocket.disconnect();
        }
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        if(frame.isTextFrame())
            System.out.println(">>> "+frame.getPayloadText());
    }
}
