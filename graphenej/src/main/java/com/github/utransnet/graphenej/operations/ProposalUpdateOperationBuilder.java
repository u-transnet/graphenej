package com.github.utransnet.graphenej.operations;

import com.github.utransnet.graphenej.Array;
import com.github.utransnet.graphenej.AssetAmount;
import com.github.utransnet.graphenej.PublicKey;
import com.github.utransnet.graphenej.UserAccount;
import com.github.utransnet.graphenej.errors.MalformedOperationException;
import com.github.utransnet.graphenej.objects.Proposal;

import java.util.List;

/**
 * Created by Artem on 25.03.2018.
 */
public class ProposalUpdateOperationBuilder {
    private AssetAmount fee = null;
    private UserAccount feePayingAccount = null;
    private Proposal proposal = null;
    private Array<UserAccount> activeApprovalsToAdd = new Array<>(1);
    private Array<UserAccount> activeApprovalsToRemove = new Array<>(1);;
    private Array<UserAccount> ownerApprovalsToAdd = new Array<>(1);;
    private Array<UserAccount> ownerApprovalsToRemove = new Array<>(1);;
    private Array<PublicKey> keyApprovalsToAdd = new Array<>(1);;
    private Array<PublicKey> keyApprovalsToRemove = new Array<>(1);;

    public ProposalUpdateOperationBuilder setFee(AssetAmount fee) {
        this.fee = fee;
        return this;
    }

    public ProposalUpdateOperationBuilder setFeePayingAccount(UserAccount feePayingAccount) {
        this.feePayingAccount = feePayingAccount;
        return this;
    }

    public ProposalUpdateOperationBuilder setProposal(Proposal proposal) {
        this.proposal = proposal;
        return this;
    }

    public ProposalUpdateOperationBuilder setProposalId(String proposalId) {
        this.proposal = new Proposal(proposalId);
        return this;
    }

    public ProposalUpdateOperationBuilder setActiveApprovalsToAdd(List<UserAccount> activeApprovalsToAdd) {
        this.activeApprovalsToAdd = new Array<>(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder addActiveApprovalsToAdd(UserAccount activeApprovalsToAdd) {
        this.activeApprovalsToAdd.add(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder setActiveApprovalsToRemove(List<UserAccount> activeApprovalsToRemove) {
        this.activeApprovalsToRemove = new Array<>(activeApprovalsToRemove);
        return this;
    }

    public ProposalUpdateOperationBuilder addActiveApprovalsToRemove(UserAccount activeApprovalsToAdd) {
        this.activeApprovalsToRemove.add(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder setOwnerApprovalsToAdd(List<UserAccount> ownerApprovalsToAdd) {
        this.ownerApprovalsToAdd = new Array<>(ownerApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder addOwnerApprovalsToAdd(UserAccount activeApprovalsToAdd) {
        this.ownerApprovalsToAdd.add(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder setOwnerApprovalsToRemove(List<UserAccount> ownerApprovalsToRemove) {
        this.ownerApprovalsToRemove = new Array<>(ownerApprovalsToRemove);
        return this;
    }

    public ProposalUpdateOperationBuilder addOwnerApprovalsToRemove(UserAccount activeApprovalsToAdd) {
        this.ownerApprovalsToRemove.add(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder setKeyApprovalsToAdd(List<PublicKey> keyApprovalsToAdd) {
        this.keyApprovalsToAdd = new Array<>(keyApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder addKeyApprovalsToAdd(PublicKey activeApprovalsToAdd) {
        this.keyApprovalsToAdd.add(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperationBuilder setKeyApprovalsToRemove(List<PublicKey> keyApprovalsToRemove) {
        this.keyApprovalsToRemove = new Array<>(keyApprovalsToRemove);
        return this;
    }

    public ProposalUpdateOperationBuilder addKeyApprovalsToRemove(PublicKey activeApprovalsToAdd) {
        this.keyApprovalsToRemove.add(activeApprovalsToAdd);
        return this;
    }

    public ProposalUpdateOperation build() {
        if(feePayingAccount == null ){
            throw new MalformedOperationException("Missing fee paying account information");
        } else if(proposal == null) {
            throw new MalformedOperationException("Missing proposal to approve");
        }
        return new ProposalUpdateOperation(
                fee,
                feePayingAccount,
                proposal,
                activeApprovalsToAdd,
                activeApprovalsToRemove,
                ownerApprovalsToAdd,
                ownerApprovalsToRemove,
                keyApprovalsToAdd,
                keyApprovalsToRemove
        );
    }

    public String toString() {
        return "ProposalUpdateOperation.ProposalUpdateOperationBuilder(fee=" + this.fee + ", feePayingAccount=" + this.feePayingAccount + ", activeApprovalsToAdd=" + this.activeApprovalsToAdd + ", activeApprovalsToRemove=" + this.activeApprovalsToRemove + ", ownerApprovalsToAdd=" + this.ownerApprovalsToAdd + ", ownerApprovalsToRemove=" + this.ownerApprovalsToRemove + ", keyApprovalsToAdd=" + this.keyApprovalsToAdd + ", keyApprovalsToRemove=" + this.keyApprovalsToRemove + ")";
    }
}
