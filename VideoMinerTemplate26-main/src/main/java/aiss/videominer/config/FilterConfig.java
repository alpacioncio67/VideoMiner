// src/main/java/aiss/videominer/config/FilterConfig.java
package aiss.videominer.config;

import aiss.videominer.filter.ApiKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/videominer/*"); // Solo aplica a tus rutas de API
        registration.setOrder(1);
        return registration;
    }
}