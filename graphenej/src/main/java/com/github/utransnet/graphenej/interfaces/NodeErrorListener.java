package com.github.utransnet.graphenej.interfaces;

import com.github.utransnet.graphenej.models.BaseResponse;

/**
 * Interface to be implemented by any listener to network errors.
 */
public interface NodeErrorListener {
    void onError(BaseResponse.Error error);
}
