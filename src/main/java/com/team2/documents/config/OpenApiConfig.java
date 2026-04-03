package com.team2.documents.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Documents Service API")
                        .version("1.0")
                        .description("PI, PO, CI, PL, 선적지시서, 생산지시서, 출하, 수금, 결재 관리 API"));
    }
}
