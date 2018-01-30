package com.github.utransnet.graphenej.errors;

import com.github.utransnet.graphenej.api.BaseGrapheneHandler;
import com.github.utransnet.graphenej.api.SubscriptionMessagesHub;

/**
 * Thrown by the {@link SubscriptionMessagesHub#addRequestHandler(BaseGrapheneHandler)}
 * whenever the user tries to register a new handler with a previously registered id
 */

public class RepeatedRequestIdException extends Exception {
    public RepeatedRequestIdException(String message){
        super(message);
    }
}
