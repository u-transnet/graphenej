package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.common.primitives.Bytes;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Artem on 24.03.2018.
 */
public class Array<T extends GrapheneSerializable> implements GrapheneSerializable {

    private List<T> values;

    public Array(List<T> values) {
        this.values = values;
    }

    public Array() {
        values = new ArrayList<>();
    }

    public Array(int initialCapacity) {
        values = new ArrayList<>(initialCapacity);
    }



    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(T o) {
        return values.contains(o);
    }

    public Object[] toArray() {
        return values.toArray();
    }

    public T[] toArray(T[] a) {
        return values.toArray(a);
    }

    public boolean add(T t) {
        return values.add(t);
    }

    public boolean remove(T o) {
        return values.remove(o);
    }

    public boolean addAll(Collection<? extends T> c) {
        return values.addAll(c);
    }

    public boolean removeAll(Collection<T> c) {
        return values.removeAll(c);
    }

    public void clear() {
        values.clear();
    }

    public T get(int index) {
        return values.get(index);
    }

    public T set(int index, T element) {
        return values.set(index, element);
    }

    public void add(int index, T element) {
        values.add(index, element);
    }

    public T remove(int index) {
        return values.remove(index);
    }

    public int indexOf(T o) {
        return values.indexOf(o);
    }

    public int lastIndexOf(T o) {
        return values.lastIndexOf(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Array)) return false;

        Array<?> array = (Array<?>) o;

        return values != null ? values.equals(array.values) : array.values == null;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }

    @Override
    public byte[] toBytes() {
        List<Byte> requiredAuthsSerialized = new ArrayList<>();
        for (GrapheneSerializable obj : values) {
            requiredAuthsSerialized.addAll(Bytes.asList(obj.toBytes()));
        }
        return Bytes.concat(Varint.writeUnsignedVarInt(values.size()), Bytes.toArray(requiredAuthsSerialized));
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Array.class, new Array.ArraySerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray proposedOpsArray = new JsonArray();
        for (GrapheneSerializable obj : values) {
            proposedOpsArray.add(obj.toJsonObject());
        }
        return proposedOpsArray;
    }

    public static  <V extends GrapheneSerializable> Array<V> fromJsonObject(JsonElement json, Class<V> clazz, JsonDeserializationContext context) {
        JsonArray jsonArray = json.getAsJsonArray();
        Array<V> array = new Array<>();
        for (JsonElement element: jsonArray) {
            V obj = context.deserialize(element, TypeToken.get(clazz).getType());
            array.add(obj);
        }
        return array;
    }

    public static class ArraySerializer implements JsonSerializer<Array> {

        @Override
        public JsonElement serialize(Array array, Type type, JsonSerializationContext jsonSerializationContext) {
            return array.toJsonObject();
        }
    }
}
