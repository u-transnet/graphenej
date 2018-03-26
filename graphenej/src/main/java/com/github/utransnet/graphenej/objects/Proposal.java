package com.github.utransnet.graphenej.objects;

import com.github.utransnet.graphenej.*;
import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Artem on 25.03.2018.
 */
public class Proposal extends GrapheneObject implements GrapheneSerializable {

    public static final String KEY_AVAILABLE_ACTIVE_APPROVALS = "available_active_approvals";
    public static final String KEY_AVAILABLE_OWNER_APPROVALS = "available_owner_approvals";
    public static final String KEY_AVAILABLE_KEY_APPROVALS = "available_key_approvals";

    public static final String KEY_EXPIRATION_TIME = "expirationTime";
    public static final String KEY_REVIEW_PERIOD_TIME = "review_period_time";

    public static final String KEY_REQUIRED_ACTIVE_APPROVALS = "required_active_approvals";
    public static final String KEY_REQUIRED_OWNER_APPROVALS = "required_owner_approvals";

    public static final String KEY_PROPOSED_TRANSACTION = "proposed_transaction";

    private Array<UserAccount> availableActiveApprovals = new Array<>(1);
    private Array<UserAccount> availableOwnerApprovals = new Array<>(1);

    //TODO: need PubKeyDeserializer
//    private Array<PublicKey> availableKeyApprovals = new Array<>(1);

    private PointInTime expirationTime;
    private PointInTime reviewPeriodTime;
    private ProposedTransaction proposedTransaction;

    private Array<UserAccount> requiredActiveApprovals = new Array<>(1);
    private Array<UserAccount> requiredOwnerApprovals = new Array<>(1);

    public Proposal(String id) {
        super(id);
    }

    public Proposal(
            String id,
            Array<UserAccount> availableActiveApprovals,
            Array<UserAccount> availableOwnerApprovals,
            PointInTime expirationTime,
            PointInTime reviewPeriodTime,
            ProposedTransaction proposedTransaction,
            Array<UserAccount> requiredActiveApprovals,
            Array<UserAccount> requiredOwnerApprovals
    ) {
        super(id);
        this.availableActiveApprovals = availableActiveApprovals;
        this.availableOwnerApprovals = availableOwnerApprovals;
        this.expirationTime = expirationTime;
        this.reviewPeriodTime = reviewPeriodTime;
        this.proposedTransaction = proposedTransaction;
        this.requiredActiveApprovals = requiredActiveApprovals;
        this.requiredOwnerApprovals = requiredOwnerApprovals;
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteArrayOutputStream);
        try {
            Varint.writeUnsignedVarLong(this.instance, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String toJsonString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        return null;
    }

    public Array<UserAccount> getAvailableActiveApprovals() {
        return this.availableActiveApprovals;
    }

    public Array<UserAccount> getAvailableOwnerApprovals() {
        return this.availableOwnerApprovals;
    }

    public PointInTime getExpirationTime() {
        return this.expirationTime;
    }

    public PointInTime getReviewPeriodTime() {
        return this.reviewPeriodTime;
    }

    public ProposedTransaction getProposedTransaction() {
        return this.proposedTransaction;
    }

    public Array<UserAccount> getRequiredActiveApprovals() {
        return this.requiredActiveApprovals;
    }

    public Array<UserAccount> getRequiredOwnerApprovals() {
        return this.requiredOwnerApprovals;
    }

    /**
     * Custom deserializer used while parsing the 'get_proposed_transactions' API call response.
     */
    public static class ProposalDeserializer implements JsonDeserializer<Proposal> {

        @Override
        public Proposal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject baseObject = json.getAsJsonObject();

            String id = baseObject.getAsJsonPrimitive(KEY_ID).getAsString();

            Array<UserAccount> availableActiveApprovals = Array.fromJsonObject(baseObject.get(KEY_AVAILABLE_ACTIVE_APPROVALS), UserAccount.class, context);
            Array<UserAccount> availableOwnerApprovals = Array.fromJsonObject(baseObject.get(KEY_AVAILABLE_OWNER_APPROVALS), UserAccount.class, context);

            PointInTime expirationTime = context.deserialize(baseObject.get(KEY_EXPIRATION_TIME), TypeToken.get(PointInTime.class).getType());
            PointInTime reviewPeriodTime = context.deserialize(baseObject.get(KEY_REVIEW_PERIOD_TIME), TypeToken.get(PointInTime.class).getType());

            Array<UserAccount> requiredActiveApprovals = Array.fromJsonObject(baseObject.get(KEY_REQUIRED_ACTIVE_APPROVALS), UserAccount.class, context);
            Array<UserAccount> requiredOwnerApprovals = Array.fromJsonObject(baseObject.get(KEY_REQUIRED_OWNER_APPROVALS), UserAccount.class, context);


            ProposedTransaction proposedTransaction = context.deserialize(baseObject.get(KEY_PROPOSED_TRANSACTION), TypeToken.get(ProposedTransaction.class).getType());


            return new Proposal(
                    id,
                    availableActiveApprovals,
                    availableOwnerApprovals,
                    expirationTime,
                    reviewPeriodTime,
                    proposedTransaction,
                    requiredActiveApprovals,
                    requiredOwnerApprovals
            );
        }
    }
}
