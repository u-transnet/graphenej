package com.github.utransnet.graphenej.operations;

import com.github.utransnet.graphenej.AssetAmount;
import com.github.utransnet.graphenej.BaseOperation;
import com.github.utransnet.graphenej.UserAccount;
import com.github.utransnet.graphenej.errors.MalformedOperationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 24.03.2018.
 */
public class ProposalCreateOperationBuilder extends BaseOperationBuilder {
    private AssetAmount fee = null;
    private UserAccount feePayingAccount;
    private List<BaseOperation> proposedOps = new ArrayList<>(1);
    private int expirationTime = 1000;
    private Integer reviewPeriodSeconds = null;


    public ProposalCreateOperationBuilder setFee(AssetAmount fee) {
        this.fee = fee;
        return this;
    }

    public ProposalCreateOperationBuilder setFeePayingAccount(UserAccount feePayingAccount) {
        this.feePayingAccount = feePayingAccount;
        return this;
    }

    public ProposalCreateOperationBuilder setProposedOps(List<BaseOperation> proposedOps) {
        this.proposedOps = proposedOps;
        return this;
    }

    public ProposalCreateOperationBuilder addProposedOp(BaseOperation operation) {
        this.proposedOps.add(operation);
        return this;
    }

    public ProposalCreateOperationBuilder setExpirationTime(int expirationTimeInSeconds) {
        this.expirationTime = expirationTimeInSeconds;
        return this;
    }

    public ProposalCreateOperationBuilder setReviewPeriodSeconds(int reviewPeriodSeconds) {
        this.reviewPeriodSeconds = reviewPeriodSeconds;
        return this;
    }

    public ProposalCreateOperation build() {
        if(feePayingAccount == null ){
            throw new MalformedOperationException("Missing fee paying account information");
        }
        return new ProposalCreateOperation(fee, feePayingAccount, expirationTime, reviewPeriodSeconds, proposedOps);
    }

    public String toString() {
        return "ProposalCreateOperation.ProposalCreateOperationBuilder(fee=" + this.fee + ", feePayingAccount=" + this.feePayingAccount + ", proposedOps=" + this.proposedOps + ", expirationTime=" + this.expirationTime + ", reviewPeriodSeconds=" + this.reviewPeriodSeconds + ")";
    }
}
