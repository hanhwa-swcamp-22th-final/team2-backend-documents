package com.team2.documents.command.infrastructure.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "currency.api")
public class CurrencyApiProperties {

    private boolean enabled = true;
    private boolean fallbackEnabled = true;
    private String primaryBaseUrl = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api";
}
