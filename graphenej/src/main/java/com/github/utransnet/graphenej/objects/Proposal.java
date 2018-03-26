package com.github.utransnet.graphenej.objects;

import com.github.utransnet.graphenej.GrapheneObject;
import com.github.utransnet.graphenej.Varint;
import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Artem on 25.03.2018.
 */
public class Proposal extends GrapheneObject implements GrapheneSerializable {
    public Proposal(String id) {
        super(id);
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
}
