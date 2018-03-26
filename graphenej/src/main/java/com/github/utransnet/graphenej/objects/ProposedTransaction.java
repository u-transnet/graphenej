package com.github.utransnet.graphenej.objects;

import com.github.utransnet.graphenej.BaseOperation;
import com.github.utransnet.graphenej.Extensions;
import com.github.utransnet.graphenej.OperationType;
import com.github.utransnet.graphenej.PointInTime;
import com.github.utransnet.graphenej.operations.TransferOperation;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 26.03.2018.
 */
public class ProposedTransaction {

    public static final String KEY_EXTENSIONS = "extensions";
    public static final String KEY_EXPIRATION = "expiration";
    public static final String KEY_REF_BLOCK_NUM = "ref_block_num";
    public static final String KEY_REF_BLOCK_PREFIX = "ref_block_prefix";
    public static final String KEY_OPERATIONS = "operations";

    private PointInTime expiration;
    private Extensions extensions;

    private int refBlockNum;
    private int refBlockPrefix;

    private List<BaseOperation> operations;


    public ProposedTransaction(PointInTime expiration, Extensions extensions, int refBlockNum, int refBlockPrefix, List<BaseOperation> operations) {
        this.expiration = expiration;
        this.extensions = extensions;
        this.refBlockNum = refBlockNum;
        this.refBlockPrefix = refBlockPrefix;
        this.operations = operations;
    }

    public PointInTime getExpiration() {
        return this.expiration;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public int getRefBlockNum() {
        return this.refBlockNum;
    }

    public int getRefBlockPrefix() {
        return this.refBlockPrefix;
    }

    public List<BaseOperation> getOperations() {
        return this.operations;
    }


    public static class ProposedTransactionDeserializer implements JsonDeserializer<ProposedTransaction> {

        @Override
        public ProposedTransaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject baseObject = json.getAsJsonObject();
            List<BaseOperation> operations = new ArrayList<>();
            for(JsonElement element: baseObject.get(KEY_OPERATIONS).getAsJsonArray()) {
                JsonArray serializedTransfer = element.getAsJsonArray();
                if(serializedTransfer.get(0).getAsInt() == OperationType.TRANSFER_OPERATION.ordinal()){
                    TransferOperation op = context.deserialize(serializedTransfer.get(1), TransferOperation.class);
                    operations.add(op);
                }
                //TODO: add support for other operations
            }
            return new ProposedTransaction(
                    context.<PointInTime>deserialize(baseObject.get(KEY_EXPIRATION), TypeToken.get(PointInTime.class).getType()),
                    null,
                    baseObject.get(KEY_REF_BLOCK_NUM).getAsInt(),
                    baseObject.get(KEY_REF_BLOCK_PREFIX).getAsInt(),
                    operations
            );
        }
    }
}
