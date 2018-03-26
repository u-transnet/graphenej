package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.github.utransnet.graphenej.interfaces.JsonSerializable;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelson on 11/9/16.
 */
public class Extensions implements GrapheneSerializable {
    public static final String KEY_EXTENSIONS = "extensions";

    //TODO: it should be Set
    private List<JsonSerializable> extensions;

    public Extensions(){
        extensions = new ArrayList<>();
    }

    public Extensions(List<JsonSerializable> extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        for(JsonSerializable o : extensions)
            array.add(o.toJsonObject());
        return array;
    }

    @Override
    public byte[] toBytes() {
        return new byte[1];
    }

    public int size(){
        return extensions.size();
    }

    public List<JsonSerializable> getExtensions() {
        return this.extensions;
    }

    public static class ExtensionsDeserializer implements JsonDeserializer
    {

        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //TODO: implement deserialization
            return new Extensions();
        }
    }
}
