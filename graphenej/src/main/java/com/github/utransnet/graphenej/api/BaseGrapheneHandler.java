package com.github.utransnet.graphenej.api;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import com.github.utransnet.graphenej.interfaces.NodeErrorListener;
import com.github.utransnet.graphenej.interfaces.WitnessResponseListener;
import com.github.utransnet.graphenej.models.BaseResponse;
import org.slf4j.Logger;

/**
 * Base class that should be extended by any implementation of a specific request to the full node.
 */
public abstract class BaseGrapheneHandler extends WebSocketAdapter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BaseGrapheneHandler.class);

    protected WitnessResponseListener mListener;
    protected NodeErrorListener mErrorListener;

    /**
     * The 'id' field of a message to the node. This is used in order to multiplex different messages
     * using the same websocket connection.
     *
     * For example:
     *
     * {"id":5,"method":"call","params":[0,"get_accounts",[["1.2.100"]]],"jsonrpc":"2.0"}
     *
     * The 'requestId' here is 5.
     */
    protected long requestId;

    /**
     * Constructor (The original constructor, should be replaced with the one that receives
     * NodeErrorListener instead of WitnessResponseListener)
     *
     * @param listener listener to be notified in if an error occurs
     */
    @Deprecated
    public BaseGrapheneHandler(WitnessResponseListener listener){
        this.mListener = listener;
    }

    /**
     * Constructor
     *
     * @param listener listener to be notified if an error occurs
     */
    public BaseGrapheneHandler(NodeErrorListener listener){
        this.mErrorListener = listener;
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        log.error("onError. cause: "+cause.getMessage());
        mErrorListener.onError(new BaseResponse.Error(cause.getMessage()));
        websocket.disconnect();
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        log.error("handleCallbackError. message: "+cause.getMessage()+", error: "+cause.getClass(), cause);
        /*for (StackTraceElement element : cause.getStackTrace()){
            System.out.println(element.getFileName()+"#"+element.getClassName()+":"+element.getLineNumber());
        }*/
        // Should be replaced for mErrorListener (NodeErrorListener type) only in the future
        if(mErrorListener != null){
            mErrorListener.onError(new BaseResponse.Error(cause.getMessage()));
        }
        else{
            mListener.onError(new BaseResponse.Error(cause.getMessage()));
        }

        websocket.disconnect();
    }

    public void setRequestId(long id){
        this.requestId = id;
    }

    public long getRequestId(){
        return this.requestId;
    }
}
