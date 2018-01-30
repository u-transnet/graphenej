package com.github.utransnet.graphenej.interfaces;

import com.github.utransnet.graphenej.models.BaseResponse;
import com.github.utransnet.graphenej.models.WitnessResponse;

/**
 * Class used to represent any listener to network requests.
 */
public interface WitnessResponseListener {

    void onSuccess(WitnessResponse response);

    void onError(BaseResponse.Error error);
}
