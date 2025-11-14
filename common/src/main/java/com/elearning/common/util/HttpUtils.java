package com.elearning.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<>();

        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> {
                    if (!"Authorization".equalsIgnoreCase(headerName) && !"User-Agent".equalsIgnoreCase(headerName)) {
                        String headerValue = request.getHeader(headerName);
                        map.put(headerName, headerValue);
                    }
                });

        return map;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getResponseBody(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        HttpHeaders responseHeaders = response.getHeaders();
        long contentLength = responseHeaders.getContentLength();

        if (contentLength != 0 && hasTextBody(responseHeaders)) {
            String bodyText = StreamUtils.copyToString(response.getBody(), determineCharset(responseHeaders));
            return bodyText;
        }
//        else if (contentLength != 0) {
//            return "{}";
//        }

        return "{}";
    }

    protected static boolean hasTextBody(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            if ("text".equals(contentType.getType())) {
                return true;
            }
            String subtype = contentType.getSubtype();
            if (subtype != null) {
                return "json".equals(subtype) ||
                        subtype.endsWith("+xml") || subtype.endsWith("+json");
            }
        }
        return false;
    }

    protected static Charset determineCharset(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            try {
                Charset charSet = contentType.getCharset();
                if (charSet != null) {
                    return charSet;
                }
            } catch (UnsupportedCharsetException e) {
            }
        }
        return StandardCharsets.UTF_8;
    }

}
