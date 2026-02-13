package com.github.leonardra.data_encryption_at_rest.security;

public class WhitelistUtil {
    public static final String[] WHITE_LIST_URL =
            {"/api/v1/auth/**", "/api/v1/countries/**", "/api/v1/health" + "-check", "/api/v1/actuator", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/*",
             "/swagger-resources", "/swagger-resources/*", "/configuration/ui", "/configuration/security",
             "/swagger-ui/*", "/webjars/*", "/swagger-ui.html", "/actuator/*", "/ws/**"};
}
