package com.github.utransnet.graphenej.models;

import java.io.Serializable;

/**
 * Created by Artem on 06.04.2018.
 */
public class AccountStatisticsObject implements Serializable {
    String id;
    long lifetime_fees_paid;
    long pending_fees;
    long pending_vested_fees;
    String most_recent_op;
    String owner;
    long total_ops;

    public String getId() {
        return id;
    }

    public long getLifetime_fees_paid() {
        return lifetime_fees_paid;
    }

    public long getPending_fees() {
        return pending_fees;
    }

    public long getPending_vested_fees() {
        return pending_vested_fees;
    }

    public String getMost_recent_op() {
        return most_recent_op;
    }

    public String getOwner() {
        return owner;
    }

    public long getTotal_ops() {
        return total_ops;
    }
}
