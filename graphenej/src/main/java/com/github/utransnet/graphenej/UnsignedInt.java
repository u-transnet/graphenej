package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Artem on 23.03.2018.
 */
public class UnsignedInt implements GrapheneSerializable {



    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteArrayOutputStream);
        /*try {
            Varint.writeUnsignedVarLong(asset.instance, out);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public JsonElement toJsonObject() {
        return null;
    }
}
