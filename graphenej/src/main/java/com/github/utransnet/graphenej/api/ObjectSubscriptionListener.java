package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.interfaces.SubscriptionListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by Artem on 09.04.2018.
 */
public abstract class ObjectSubscriptionListener implements SubscriptionListener {
    @Override
    public List<String> getInterestedAccountNames() {
        return Collections.emptyList();
    }
}
