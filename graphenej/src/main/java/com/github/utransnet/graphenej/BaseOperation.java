package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.operations.ProposalCreateOperation;
import com.github.utransnet.graphenej.operations.TransferOperation;
import com.google.gson.*;
import com.github.utransnet.graphenej.interfaces.ByteSerializable;
import com.github.utransnet.graphenej.interfaces.JsonSerializable;

import java.lang.reflect.Type;

/**
 * Created by nelson on 11/5/16.
 */
public abstract class BaseOperation implements ByteSerializable, JsonSerializable {

    public static final String KEY_FEE = "fee";
    public static final String KEY_EXTENSIONS = "extensions";

    protected OperationType type;
    protected Extensions extensions;

    public BaseOperation(OperationType type){
        this.type = type;
        this.extensions = new Extensions();
    }

    public byte getId() {
        return (byte) this.type.ordinal();
    }

    public OperationType getType() {
        return type;
    }

    public abstract void setFee(AssetAmount assetAmount);

    public JsonElement toJsonObject(){
        JsonArray array = new JsonArray();
        array.add(this.getId());
        return array;
    }

    public static class BaseOperationDeserializer implements JsonDeserializer<BaseOperation> {

        @Override
        public BaseOperation deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonArray serializedTransfer = json.getAsJsonArray();
            int opType = serializedTransfer.get(0).getAsInt();
            if (opType == OperationType.TRANSFER_OPERATION.ordinal()) {
                return context.deserialize(serializedTransfer.get(1), TransferOperation.class);
            } else if (opType == OperationType.PROPOSAL_CREATE_OPERATION.ordinal()) {
                return context.deserialize(serializedTransfer.get(1), ProposalCreateOperation.class);
            } else {
                // Not implemented operation
                return null;
            }
        }
    }
}
