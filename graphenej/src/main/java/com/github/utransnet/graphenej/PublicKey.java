package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.gson.*;
import org.bitcoinj.core.ECKey;
import org.spongycastle.math.ec.ECPoint;

import java.lang.reflect.Type;

/**
 * Created by nelson on 11/30/16.
 */
public class PublicKey implements GrapheneSerializable {
    private ECKey publicKey;

    public PublicKey(ECKey key) {
        if (key.hasPrivKey()) {
            throw new IllegalStateException("Passing a private key to PublicKey constructor");
        }
        this.publicKey = key;
    }

    public ECKey getKey() {
        return publicKey;
    }

    @Override
    public byte[] toBytes() {
        if (publicKey.isCompressed()) {
            return publicKey.getPubKey();
        } else {
            publicKey = ECKey.fromPublicOnly(ECKey.compressPoint(publicKey.getPubKeyPoint()));
            return publicKey.getPubKey();
        }
    }

    public String getAddress() {
        ECKey pk = ECKey.fromPublicOnly(publicKey.getPubKey());
        if (!pk.isCompressed()) {
            ECPoint point = ECKey.compressPoint(pk.getPubKeyPoint());
            pk = ECKey.fromPublicOnly(point);
        }
        return new Address(pk).toString();
    }

    @Override
    public int hashCode() {
        return publicKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        PublicKey other = (PublicKey) obj;
        return this.publicKey.equals(other.getKey());
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PublicKey.class, new PublicKey.PublicKeySerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        return new JsonPrimitive(new Address(this.getKey()).toString());
    }


    public static class PublicKeySerializer implements JsonSerializer<PublicKey> {

        @Override
        public JsonElement serialize(PublicKey publicKey, Type type, JsonSerializationContext jsonSerializationContext) {
            return publicKey.toJsonObject();
        }
    }
}