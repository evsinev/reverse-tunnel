package com.payneteasy.rtunnel.server.util;

import com.google.gson.*;
import com.payneteasy.tlv.HexUtil;

import java.lang.reflect.Type;

public class HexTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return HexUtil.parseHex(json.getAsString());
    }

    public JsonElement serialize(byte[] data, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(HexUtil.toHexString(data));
    }
}


