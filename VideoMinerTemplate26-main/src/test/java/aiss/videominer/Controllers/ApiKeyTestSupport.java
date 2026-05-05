package aiss.videominer.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

abstract class ApiKeyTestSupport {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Value("${api.key}")
    private String apiKey;

    protected <T> HttpEntity<T> authorizedEntity(T body) {
        return new HttpEntity<>(body, headers());
    }

    protected HttpEntity<Void> authorizedEntity() {
        return new HttpEntity<>(headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, apiKey);
        return headers;
    }
}
