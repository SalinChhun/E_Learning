package com.elearning.common.config;

import com.elearning.common.components.properties.RsaKeysProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;


@Configuration
public class AppConfig {

    @Bean
    JwtDecoder jwtDecoder(RsaKeysProperties rsaKeysProperties) {
        return NimbusJwtDecoder.withPublicKey(rsaKeysProperties.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder(RsaKeysProperties rsaKeysProperties) {
        JWK jwk= new RSAKey.Builder(rsaKeysProperties.publicKey()).privateKey(rsaKeysProperties.privateKey()).build();
        JWKSource<SecurityContext> jwkSource= new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = Jackson2ObjectMapperBuilder
                .json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .featuresToDisable(SerializationFeature.INDENT_OUTPUT)
                .modules(modules -> {
                    modules.add(new JavaTimeModule());
                })
                .serializationInclusion(JsonInclude.Include.ALWAYS)
                .build();

        //allow leading zeros (05) => 5 as Integer
        mapper.enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());
//        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
//        mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        return mapper;
    }

    /**
     * Creates a custom HTTP message converter that uses our configured ObjectMapper
     * This ensures the ObjectMapper is used for all HTTP request/response conversions
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
            ObjectMapper objectMapper
    ) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
