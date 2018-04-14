package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.RPC;
import com.github.utransnet.graphenej.interfaces.WitnessResponseListener;
import com.github.utransnet.graphenej.models.ApiCall;
import com.github.utransnet.graphenej.models.BaseResponse;
import com.github.utransnet.graphenej.models.DynamicGlobalProperties;
import com.github.utransnet.graphenej.models.WitnessResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Artem on 27.03.2018.
 */
public abstract class ApiIdRequestSequence extends BaseGrapheneHandler {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ApiIdRequestSequence.class);

    protected final static int LOGIN_ID_RESPONSE = 0;
    protected final static int API_ID_RESPONSE = 1;

    protected final int latestResponseId;
    protected final String apiName;

    private WitnessResponseListener mListener;

    private int apiId = -1;

    private boolean mOneTime;

    public ApiIdRequestSequence(int latestResponseId, String apiName, boolean oneTime, WitnessResponseListener listener){
        super(listener);
        this.latestResponseId = latestResponseId;
        this.apiName = apiName;
        this.mOneTime = oneTime;
        this.mListener = listener;
    }

    public ApiIdRequestSequence(int latestResponseId, String apiName, WitnessResponseListener listener){
        this(latestResponseId, apiName, true, listener);
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        ArrayList<Serializable> loginParams = new ArrayList<>();
        loginParams.add(null);
        loginParams.add(null);
        ApiCall loginCall = new ApiCall(1, RPC.CALL_LOGIN, loginParams, RPC.VERSION, requestId);
        websocket.sendText(loginCall.toJsonString());
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        if(frame.isTextFrame())
            log.debug("<<< "+frame.getPayloadText());
        String response = frame.getPayloadText();
        GsonBuilder builder = gsonBuilder();
        Gson gson = builder.create();
        BaseResponse baseResponse = gson.fromJson(response, BaseResponse.class);
        if(baseResponse.error != null){
            mListener.onError(baseResponse.error);
            if(mOneTime){
                websocket.disconnect();
            }
        }else{
            requestId++;
            processFrame(websocket, response, gson, baseResponse);
        }
    }

    protected GsonBuilder gsonBuilder() {
        return new GsonBuilder();
    }

    protected void processFrame(WebSocket websocket, String response, Gson gson, BaseResponse baseResponse) {
        if(baseResponse.id == LOGIN_ID_RESPONSE){
            ArrayList<Serializable> emptyParams = new ArrayList<>();
            ApiCall networkApiIdCall = new ApiCall(1, apiName, emptyParams, RPC.VERSION, requestId);
            websocket.sendText(networkApiIdCall.toJsonString());
        }  else if(baseResponse.id == API_ID_RESPONSE) {
            Type ApiIdResponse = new TypeToken<WitnessResponse<Integer>>() {}.getType();
            WitnessResponse<Integer> witnessResponse = gson.fromJson(response, ApiIdResponse);
            apiId = witnessResponse.result;
        } else if(baseResponse.id >= latestResponseId){
            if(mOneTime){
                websocket.disconnect();
            }
        }
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        if(frame.isTextFrame()){
            log.debug(">>> "+frame.getPayloadText());
        }
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        log.error("onError. cause: "+cause.getMessage());
        mListener.onError(new BaseResponse.Error(cause.getMessage()));
        if(mOneTime){
            websocket.disconnect();
        }
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        log.error("handleCallbackError. cause: "+cause.getMessage()+", error: "+cause.getClass(), cause);
        /*for (StackTraceElement element : cause.getStackTrace()){
            System.out.println(element.getFileName()+"#"+element.getClassName()+":"+element.getLineNumber());
        }*/
        mListener.onError(new BaseResponse.Error(cause.getMessage()));
        if(mOneTime){
            websocket.disconnect();
        }
    }

    protected int getApiId() {
        return apiId;
    }

    protected WitnessResponseListener getListener() {
        return mListener;
    }
}
