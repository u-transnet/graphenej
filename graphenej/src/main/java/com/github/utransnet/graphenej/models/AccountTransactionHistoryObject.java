package com.github.utransnet.graphenej.models;

import java.io.Serializable;

/**
 * Created by Artem on 06.04.2018.
 */
public class AccountTransactionHistoryObject implements Serializable {
    String id;
    String account;
    String operation_id;
    int sequence;
    String next;

    public String getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getOperation_id() {
        return operation_id;
    }

    public int getSequence() {
        return sequence;
    }

    public String getNext() {
        return next;
    }
}
