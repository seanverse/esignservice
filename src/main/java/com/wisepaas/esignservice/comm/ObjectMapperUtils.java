package com.wisepaas.esignservice.comm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ObjectMapperUtils {
    private static final Gson gson = new GsonBuilder().create();

    private ObjectMapperUtils() {
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }

    public static <T> List<T> toList(String json, Class<T> elementType) {
        Type type = TypeToken.getParameterized(List.class, elementType).getType();
        return fromJson(json, type);
    }

    public static <T> List<Map<String, T>> toListMaps(String json) {
        Type type = new TypeToken<List<Map<String, T>>>() {
        }.getType();
        return fromJson(json, type);
    }

    public static <T> Map<String, T> toMaps(String json) {
        Type type = new TypeToken<Map<String, T>>() {
        }.getType();
        return fromJson(json, type);
    }
}

