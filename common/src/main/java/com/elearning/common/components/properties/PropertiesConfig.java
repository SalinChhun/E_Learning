package com.elearning.common.components.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        RsaKeysProperties.class
        , JwtProperties.class
})
public class PropertiesConfig {
}
