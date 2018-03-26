package com.github.utransnet.graphenej;

import com.github.utransnet.graphenej.interfaces.GrapheneSerializable;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Artem on 24.03.2018.
 */
public class PointInTime implements GrapheneSerializable {

    // Number of bytes used for the timestamp field.
    private final int BYTE_LENGTH = 4;

    //TODO: may be we should use long?
    private int timestamp;

    public PointInTime(int timestamp) {
        this.timestamp = timestamp;
    }

    public PointInTime(Date date) {
        this.timestamp = (int) (date.getTime() / 1000);
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(BYTE_LENGTH);
        buffer.putInt(timestamp);
        return  Util.revertBytes(buffer.array());
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PointInTime.class, new PointInTime.PointInTimeSerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Util.TIME_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new JsonPrimitive(simpleDateFormat.format(new Date((long) timestamp * 1000)));
    }

    public static class PointInTimeSerializer implements JsonSerializer<PointInTime> {

        @Override
        public JsonElement serialize(PointInTime pointInTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return pointInTime.toJsonObject();
        }
    }

    public static PointInTime fromNow(int periodInSeconds) {
        return new PointInTime((int) (new Date().getTime() / 1000 + periodInSeconds));
    }
}
