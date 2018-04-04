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

    public static class ProposalUpdateDeserializer implements JsonDeserializer<ProposalUpdateOperation> {

        @Override
        public ProposalUpdateOperation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                // This block is used just to check if we are in the first step of the deserialization
                // when we are dealing with an array.
                JsonArray serializedTransfer = json.getAsJsonArray();
                if (serializedTransfer.get(0).getAsInt() != OperationType.PROPOSAL_UPDATE_OPERATION.ordinal()) {
                    // If the operation type does not correspond to a transfer operation, we return null
                    return null;
                } else {
                    // Calling itself recursively, this is only done once, so there will be no problems.
                    return context.deserialize(serializedTransfer.get(1), ProposalUpdateOperation.class);
                }
            } else {
                // This block is called in the second recursion and takes care of deserializing the
                // transfer data itself.
                JsonObject jsonObject = json.getAsJsonObject();

                AssetAmount fee = context.deserialize(jsonObject.get(KEY_FEE), AssetAmount.class);
                UserAccount feePayingAccount = context.deserialize(jsonObject.get(KEY_FEE_PAYING_ACCOUNT), UserAccount.class);

                String proposalId = jsonObject.get(KEY_PROPOSAL).getAsString();

                Array<UserAccount> activeApprovalsToAdd =  Array.fromJsonObject(jsonObject.get(KEY_ACTIVE_APPROVALS_TO_ADD), UserAccount.class, context);
                Array<UserAccount> activeApprovalsToRemove =  Array.fromJsonObject(jsonObject.get(KEY_ACTIVE_APPROVALS_TO_REMOVE), UserAccount.class, context);
                Array<UserAccount> ownerApprovalsToAdd =  Array.fromJsonObject(jsonObject.get(KEY_OWNER_APPROVALS_TO_ADD), UserAccount.class, context);
                Array<UserAccount> ownerApprovalsToRemove =  Array.fromJsonObject(jsonObject.get(KEY_OWNER_APPROVALS_TO_REMOVE), UserAccount.class, context);


                //TODO: need PubKeyDeserializer
                /*Array<PublicKey> keyApprovalsToAdd =  Array.fromJsonObject(jsonObject.get(KEY_KEY_APPROVALS_TO_ADD), PublicKey.class, context);
                Array<PublicKey> keyApprovalsToRemove =  Array.fromJsonObject(jsonObject.get(KEY_KEY_APPROVALS_TO_REMOVE), PublicKey.class, context);*/
                Array<PublicKey> keyApprovalsToAdd = new Array<>(1);
                Array<PublicKey> keyApprovalsToRemove = new Array<>(1);



                return new ProposalUpdateOperation(
                        fee,
                        feePayingAccount,
                        new Proposal(proposalId),
                        activeApprovalsToAdd,
                        activeApprovalsToRemove,
                        ownerApprovalsToAdd,
                        ownerApprovalsToRemove,
                        keyApprovalsToAdd,
                        keyApprovalsToRemove
                );
            }
        }
    }

}
