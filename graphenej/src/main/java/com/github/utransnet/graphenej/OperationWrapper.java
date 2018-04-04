package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.github.utransnet.graphenej.operations.ProposalCreateOperation;
import com.google.common.primitives.Bytes;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Artem on 24.03.2018.
 */
public class OperationWrapper implements GrapheneSerializable {


    public static final String KEY_OP = "op";

    BaseOperation op;

    public OperationWrapper(BaseOperation op) {
        this.op = op;
    }

    @Override
    public byte[] toBytes() {
        return Bytes.concat(new byte[]{op.getId()}, op.toBytes());
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(OperationWrapper.class, new OperationWrapper.OperationWrapperSerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonObject opWrapper = new JsonObject();
        opWrapper.add(KEY_OP, op.toJsonObject());
        return opWrapper;
    }

    public void setFee(AssetAmount fee) {
        this.op.setFee(fee);
    }

    public static class OperationWrapperSerializer implements JsonSerializer<OperationWrapper> {

        @Override
        public JsonElement serialize(OperationWrapper operationWrapper, Type type, JsonSerializationContext jsonSerializationContext) {
            return operationWrapper.toJsonObject();
        }
    }

    public static class OperationWrapperDeserializer implements JsonDeserializer<OperationWrapper> {


        @Override
        public OperationWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            BaseOperation op = context.deserialize(json.getAsJsonObject().get(KEY_OP), BaseOperation.class);
            return new OperationWrapper(op);
        }
    }
}
