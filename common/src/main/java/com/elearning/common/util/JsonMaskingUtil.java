package com.elearning.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;

public class JsonMaskingUtil {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "pass", "pwd", "secret", "token", "key", "auth", "credential",
            "username", "user", "login", "email", "pin", "otp", "apikey", "api_key", "access_token", "refresh_token"
    );

    public static String maskSensitiveFields(ObjectMapper mapper, String json) {
        try {
            JsonNode node = mapper.readTree(json);

            maskNode(node);

            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            // log error
            return json; // fallback
        }
    }

    private static void maskNode(JsonNode node) {
        if (node instanceof ObjectNode objNode) {
            objNode.fieldNames().forEachRemaining(field -> {
                JsonNode child = objNode.get(field);
                if (SENSITIVE_FIELDS.contains(field)) {
                    objNode.put(field, "*****");
                } else {
                    maskNode(child);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                maskNode(item);
            }
        }
    }
}
