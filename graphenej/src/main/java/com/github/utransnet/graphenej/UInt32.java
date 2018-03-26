package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.common.primitives.UnsignedInteger;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Artem on 24.03.2018.
 */
public class UInt32 implements GrapheneSerializable {

    UnsignedInteger value;

    public UInt32(UnsignedInteger value) {
        this.value = value;
    }

    public UInt32(long value) {
        this.value = UnsignedInteger.valueOf(value);
    }


    @Override
    public byte[] toBytes() {
        return Util.revertInteger(value.intValue());
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(UInt32.class, new UInt32.UInt32Serializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        return new JsonPrimitive(value.intValue());
    }

    public static class UInt32Serializer implements JsonSerializer<UInt32> {

        @Override
        public JsonElement serialize(UInt32 uInt32, Type type, JsonSerializationContext jsonSerializationContext) {
            return uInt32.toJsonObject();
        }
    }
}
