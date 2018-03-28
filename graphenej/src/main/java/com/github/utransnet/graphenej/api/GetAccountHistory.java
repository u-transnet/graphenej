package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.*;
import com.github.utransnet.graphenej.interfaces.WitnessResponseListener;
import com.github.utransnet.graphenej.models.ApiCall;
import com.github.utransnet.graphenej.models.BaseResponse;
import com.github.utransnet.graphenej.models.HistoricalTransfer;
import com.github.utransnet.graphenej.models.WitnessResponse;
import com.github.utransnet.graphenej.objects.Memo;
import com.github.utransnet.graphenej.objects.Proposal;
import com.github.utransnet.graphenej.objects.ProposedTransaction;
import com.github.utransnet.graphenej.operations.TransferOperation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nelson on 12/26/16.
 */
//TODO: Implement if needed:  http://docs.bitshares.eu/api/history.html?highlight=get_market_history#account-history-api
public class GetAccountHistory extends ApiIdRequestSequence {

    public static final long NEWEST_TRANSACTION_ID = Long.MAX_VALUE;
    public static final long OLDEST_TRANSACTION_ID = 0;

    private String accountId;
    private long last = NEWEST_TRANSACTION_ID;
    private long first = OLDEST_TRANSACTION_ID;
    private int limit = 100;

    public GetAccountHistory(UserAccount account, long newestId, long oldestId, int limit, WitnessResponseListener listener) {
        this(account.getObjectId(), newestId, oldestId, limit, listener);
    }

    public GetAccountHistory(UserAccount account, WitnessResponseListener listener) {
        this(account.getObjectId(), listener);
    }

    public GetAccountHistory(UserAccount account, int limit, WitnessResponseListener listener) {
        this(account.getObjectId(), limit, listener);
    }

    public GetAccountHistory(String accountId, long newestId, long oldestId, int limit, WitnessResponseListener listener) {
        super(API_ID_RESPONSE, RPC.CALL_HISTORY, listener);
        this.accountId = accountId;
        this.last = newestId;
        this.first = oldestId;
        this.limit = limit;
    }

    public GetAccountHistory(String accountId, WitnessResponseListener listener) {
        super(API_ID_RESPONSE, RPC.CALL_HISTORY, listener);
        this.accountId = accountId;
    }

    public GetAccountHistory(String accountId, int limit, WitnessResponseListener listener) {
        super(API_ID_RESPONSE, RPC.CALL_HISTORY, listener);
        this.accountId = accountId;
        this.limit = limit;
    }

    @Override
    protected void processFrame(WebSocket websocket, String response, Gson gson, BaseResponse baseResponse) {
        super.processFrame(websocket, response, gson, baseResponse);
        if(baseResponse.id == API_ID_RESPONSE) {
            ArrayList<Serializable> params = new ArrayList<>();
            params.add(accountId);
            params.add("1.11." + first);
            params.add(limit);
            params.add("1.11." + last);
            ApiCall getAccountByAddress = new ApiCall(getApiId(), RPC.CALL_GET_ACCOUNT_HISTORY, params, RPC.VERSION, requestId);
            websocket.sendText(getAccountByAddress.toJsonString());
        } else if(baseResponse.id >= API_ID_RESPONSE) {
            Type witnessResponseType = new TypeToken<WitnessResponse<List<HistoricalTransfer>>>() {}.getType();
            WitnessResponse<List<HistoricalTransfer>> witnessResponse = gson.fromJson(response, witnessResponseType);

            if (witnessResponse.error != null) {
                getListener().onError(witnessResponse.error);
            } else {
                getListener().onSuccess(witnessResponse);
            }
        }
    }

    @Override
    protected GsonBuilder gsonBuilder() {
        GsonBuilder builder = super.gsonBuilder();
        builder.registerTypeAdapter(TransferOperation.class, new TransferOperation.TransferDeserializer());
        builder.registerTypeAdapter(UserAccount.class, new UserAccount.UserAccountSimpleDeserializer());
        builder.registerTypeAdapter(AssetAmount.class, new AssetAmount.AssetAmountDeserializer());
        builder.registerTypeAdapter(Memo.class, new Memo.MemoDeserializer());
        return builder;
    }
}
