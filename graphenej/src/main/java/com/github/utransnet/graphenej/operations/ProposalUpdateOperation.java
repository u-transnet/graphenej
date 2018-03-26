package com.github.utransnet.graphenej.operations;

import com.github.utransnet.graphenej.*;
import com.github.utransnet.graphenej.objects.Proposal;
import com.google.common.primitives.Bytes;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Artem on 24.03.2018.
 */
public class ProposalUpdateOperation extends BaseOperation {

    public static final String KEY_FEE_PAYING_ACCOUNT = "fee_paying_account";
    public static final String KEY_PROPOSAL = "proposal";
    public static final String KEY_ACTIVE_APPROVALS_TO_ADD = "active_approvals_to_add";
    public static final String KEY_ACTIVE_APPROVALS_TO_REMOVE = "active_approvals_to_remove";
    public static final String KEY_OWNER_APPROVALS_TO_ADD = "owner_approvals_to_add";
    public static final String KEY_OWNER_APPROVALS_TO_REMOVE = "owner_approvals_to_remove";
    public static final String KEY_KEY_APPROVALS_TO_ADD = "key_approvals_to_add";
    public static final String KEY_KEY_APPROVALS_TO_REMOVE = "key_approvals_to_remove";

    private AssetAmount fee;
    private UserAccount feePayingAccount;
    private Proposal proposal;
    private Array<UserAccount> activeApprovalsToAdd;
    private Array<UserAccount> activeApprovalsToRemove;
    private Array<UserAccount> ownerApprovalsToAdd;
    private Array<UserAccount> ownerApprovalsToRemove;
    private Array<PublicKey> keyApprovalsToAdd;
    private Array<PublicKey> keyApprovalsToRemove;

    public ProposalUpdateOperation(AssetAmount fee, UserAccount feePayingAccount, Proposal proposal, Array<UserAccount> activeApprovalsToAdd, Array<UserAccount> activeApprovalsToRemove, Array<UserAccount> ownerApprovalsToAdd, Array<UserAccount> ownerApprovalsToRemove, Array<PublicKey> keyApprovalsToAdd, Array<PublicKey> keyApprovalsToRemove) {
        super(OperationType.PROPOSAL_UPDATE_OPERATION);
        this.fee = fee;
        this.feePayingAccount = feePayingAccount;
        this.proposal = proposal;
        this.activeApprovalsToAdd = activeApprovalsToAdd;
        this.activeApprovalsToRemove = activeApprovalsToRemove;
        this.ownerApprovalsToAdd = ownerApprovalsToAdd;
        this.ownerApprovalsToRemove = ownerApprovalsToRemove;
        this.keyApprovalsToAdd = keyApprovalsToAdd;
        this.keyApprovalsToRemove = keyApprovalsToRemove;
    }

    public ProposalUpdateOperation(OperationType type) {
        super(type);
    }

    public static ProposalUpdateOperationBuilder builder() {
        return new ProposalUpdateOperationBuilder();
    }

    @Override
    public void setFee(AssetAmount fee) {
        this.fee = fee;
    }

    @Override
    public byte[] toBytes() {
        return Bytes.concat(
                fee.toBytes(),
                feePayingAccount.toBytes(),
                proposal.toBytes(),
                activeApprovalsToAdd.toBytes(),
                activeApprovalsToRemove.toBytes(),
                ownerApprovalsToAdd.toBytes(),
                ownerApprovalsToRemove.toBytes(),
                keyApprovalsToAdd.toBytes(),
                keyApprovalsToRemove.toBytes(),
                extensions.toBytes()
        );
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ProposalUpdateOperation.class, new ProposalUpdateOperation.ProposalUpdateSerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        if (fee != null) {
            jsonObject.add(KEY_FEE, fee.toJsonObject());
        }
        jsonObject.addProperty(KEY_FEE_PAYING_ACCOUNT, feePayingAccount.getObjectId());
        jsonObject.addProperty(KEY_PROPOSAL, proposal.getObjectId());

        jsonObject.add(KEY_ACTIVE_APPROVALS_TO_ADD, activeApprovalsToAdd.toJsonObject());
        jsonObject.add(KEY_ACTIVE_APPROVALS_TO_REMOVE, activeApprovalsToRemove.toJsonObject());

        jsonObject.add(KEY_OWNER_APPROVALS_TO_ADD, ownerApprovalsToAdd.toJsonObject());
        jsonObject.add(KEY_OWNER_APPROVALS_TO_REMOVE, ownerApprovalsToRemove.toJsonObject());

        jsonObject.add(KEY_KEY_APPROVALS_TO_ADD, keyApprovalsToAdd.toJsonObject());
        jsonObject.add(KEY_KEY_APPROVALS_TO_REMOVE, keyApprovalsToRemove.toJsonObject());

        jsonObject.add(KEY_EXTENSIONS, new JsonArray());
        array.add(jsonObject);
        return array;
    }


    public static class ProposalUpdateSerializer implements JsonSerializer<ProposalUpdateOperation> {

        @Override
        public JsonElement serialize(ProposalUpdateOperation proposalUpdateOperation, Type type, JsonSerializationContext jsonSerializationContext) {
            return proposalUpdateOperation.toJsonObject();
        }
    }

}
