package com.trendsole.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "trendsole.company")
@Getter
@Setter
public class CompanyProperties {
    private String name = "TrendSole";
    private String email = "trendsole.business@gmail.com";
    private String website = "https://trendsole.com";
    private String phone = "";
    private String address = "";
}
