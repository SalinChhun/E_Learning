package com.elearning.common.payload.CBS;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic DataBody that can handle any fields dynamically
 */
@Getter
public class GenericDataBody {
    @JsonIgnore
    private Map<String, Object> properties = new HashMap<>();

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    @JsonAnySetter
    public void add(String key, Object value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Get a specific property with type casting
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        Object value = properties.get(key);
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass() + " to " + type);
        }
    }

    /**
     * Helper method to get list property with proper object mapping
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getListProperty(String key, Class<T> elementType) {
        Object value = properties.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof List<?> rawList) {
            List<T> typedList = new ArrayList<>();

            for (Object item : rawList) {
                if (item instanceof Map) {
                    // Convert Map to the target type using ObjectMapper
                    // First convert to JSON string, then back to object to handle naming strategies
                    try {
                        String jsonString = objectMapper.writeValueAsString(item);
                        T convertedItem = objectMapper.readValue(jsonString, elementType);
                        typedList.add(convertedItem);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to convert map to " + elementType.getSimpleName(), e);
                    }
                } else if (elementType.isAssignableFrom(item.getClass())) {
                    typedList.add((T) item);
                } else {
                    throw new ClassCastException("Cannot convert list element to " + elementType);
                }
            }
            return typedList;
        } else {
            throw new ClassCastException("Property " + key + " is not a List");
        }
    }

    /**
     * Add a property with a specific key
     */
    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }
}