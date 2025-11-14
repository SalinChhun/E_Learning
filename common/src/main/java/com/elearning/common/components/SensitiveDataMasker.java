package com.elearning.common.components;


import com.elearning.common.components.logging.AppLogManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SensitiveDataMasker {

    private final ObjectMapper objectMapper;

    private static final Set<String> SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
            "password", "pass", "pwd", "secret", "token", "key", "auth", "credential",
            "username", "user", "login", "email", "pin", "otp", "apikey", "api_key", "access_token", "refresh_token"
    ));

    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            ".*(password|pass|pwd|secret|token|key|auth|credential|username|user|login|email|phone|pin|otp).*",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Mask sensitive data in JSON string
     */
    public String maskSensitiveData(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return jsonString;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode maskedNode = maskJsonNode(rootNode);
            return objectMapper.writeValueAsString(maskedNode);
        } catch (Exception e) {
            AppLogManager.debug("Failed to parse and mask JSON string", e);
            return "[INVALID_JSON]";
        }
    }

    /**
     * Mask sensitive data in JsonNode
     */
    public JsonNode maskSensitiveData(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }

        try {
            return maskJsonNode(jsonNode.deepCopy());
        } catch (Exception e) {
            AppLogManager.debug("Failed to mask JsonNode", e);
            return jsonNode;
        }
    }

    /**
     * Create a log-safe version of config JSON
     */
    public String createLogSafeConfig(JsonNode configJson) {
        if (configJson == null) {
            return "null";
        }

        try {
            JsonNode maskedConfig = maskJsonNode(configJson.deepCopy());
            return objectMapper.writeValueAsString(maskedConfig);
        } catch (Exception e) {
            AppLogManager.debug("Failed to create log-safe config", e);
            return "[MASKING_ERROR]";
        }
    }

    private JsonNode maskJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                if (isSensitiveField(fieldName)) {
                    objectNode.put(fieldName, maskValue(objectNode.get(fieldName)));
                } else if (objectNode.get(fieldName).isContainerNode()) {
                    objectNode.set(fieldName, maskJsonNode(objectNode.get(fieldName)));
                }
            });
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                JsonNode arrayElement = node.get(i);
                if (arrayElement.isContainerNode()) {
                    ((com.fasterxml.jackson.databind.node.ArrayNode) node).set(i, maskJsonNode(arrayElement));
                }
            }
        }

        return node;
    }

    private boolean isSensitiveField(String fieldName) {
        if (fieldName == null) {
            return false;
        }

        String lowerFieldName = fieldName.toLowerCase();

        // Check exact matches
        if (SENSITIVE_FIELDS.contains(lowerFieldName)) {
            return true;
        }

        // Check pattern matches
        return SENSITIVE_PATTERN.matcher(lowerFieldName).matches();
    }

    private String maskValue(JsonNode valueNode) {
        if (valueNode == null || valueNode.isNull()) {
            return null;
        }

        return "*****";
    }
}