package com.github.utransnet.graphenej.operations;

import com.github.utransnet.graphenej.*;
import com.google.common.primitives.Bytes;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Artem on 22.03.2018.
 */
public class ProposalCreateOperation extends BaseOperation {

    public static final String KEY_FEE_PAYING_ACCOUNT = "fee_paying_account";
    public static final String KEY_EXPIRATION_TIME = "expiration_time";
    public static final String KEY_PROPOSED_OPS = "proposed_ops";
    public static final String KEY_REVIEW_PERIOD_SECONDS = "review_period_seconds";

    private AssetAmount fee;
    private UserAccount feePayingAccount;
    private Array<OperationWrapper> proposedOps;
    private PointInTime expirationTime;
    private Optional<UInt32> reviewPeriodSeconds;

    public ProposalCreateOperation(
            AssetAmount fee,
            UserAccount feePayingAccount,
            int expirationTimeInSeconds,
            Integer reviewPeriodSeconds,
            List<BaseOperation> proposedOps
    ) {
        super(OperationType.PROPOSAL_CREATE_OPERATION);
        this.fee = fee;
        this.feePayingAccount = feePayingAccount;
        this.proposedOps = new Array<>();
        for (BaseOperation op : proposedOps) {
            this.proposedOps.add(new OperationWrapper(op));
        }
        this.reviewPeriodSeconds = new Optional<>((reviewPeriodSeconds != null) ? new UInt32(reviewPeriodSeconds) : null);
        expirationTime = PointInTime.fromNow(expirationTimeInSeconds);
    }

    public static ProposalCreateOperationBuilder builder() {
        return new ProposalCreateOperationBuilder();
    }

    public AssetAmount getFee() {
        return this.fee;
    }

    @Override
    public void setFee(AssetAmount assetAmount) {
        this.fee = assetAmount;
    }

    public Array<OperationWrapper> getProposedOps() {
        return proposedOps;
    }

    public void setProposedOps(Array<OperationWrapper> proposedOps) {
        this.proposedOps = proposedOps;
    }

    @Override
    public byte[] toBytes() {
        return Bytes.concat(
                fee.toBytes(),
                feePayingAccount.toBytes(),
                expirationTime.toBytes(),
                proposedOps.toBytes(), //op_count: 01 + op_type: 00 + op: ...
                reviewPeriodSeconds.toBytes(),
                this.extensions.toBytes()
        );
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ProposalCreateOperation.class, new ProposalCreateOperation.ProposalCreateSerializer());
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
        jsonObject.add(KEY_EXPIRATION_TIME, expirationTime.toJsonObject());

        jsonObject.add(KEY_PROPOSED_OPS, proposedOps.toJsonObject());
        if (reviewPeriodSeconds.isSet()) {
            jsonObject.add(KEY_REVIEW_PERIOD_SECONDS, reviewPeriodSeconds.toJsonObject());
        }

        jsonObject.add(KEY_EXTENSIONS, new JsonArray());
        array.add(jsonObject);
        return array;
    }

    public static class ProposalCreateSerializer implements JsonSerializer<ProposalCreateOperation> {

        @Override
        public JsonElement serialize(ProposalCreateOperation proposalCreate, Type type, JsonSerializationContext jsonSerializationContext) {
            return proposalCreate.toJsonObject();
        }
    }


}
