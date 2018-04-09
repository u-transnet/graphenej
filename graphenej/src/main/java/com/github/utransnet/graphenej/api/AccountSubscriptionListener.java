package com.github.utransnet.graphenej.api;

import com.github.utransnet.graphenej.ObjectType;
import com.github.utransnet.graphenej.interfaces.SubscriptionListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by Artem on 09.04.2018.
 */
public abstract class AccountSubscriptionListener implements SubscriptionListener {
    @Override
    public ObjectType getInterestObjectType() {
        return ObjectType.ACCOUNT_STATISTICS_OBJECT;
    }
}
