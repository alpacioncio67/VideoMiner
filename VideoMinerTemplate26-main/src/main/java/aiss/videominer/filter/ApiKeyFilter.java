// src/main/java/aiss/videominer/filter/ApiKeyFilter.java
package aiss.videominer.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    // Métodos que requieren autenticación
    private static final List<String> PROTECTED_METHODS = List.of("POST", "PUT", "DELETE");

    @Value("${api.key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();

        // Si el método NO requiere protección, dejamos pasar
        if (!PROTECTED_METHODS.contains(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Comprobamos la API Key del header
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(validApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"API Key inválida o ausente\"}");
            return;
        }

        // API Key correcta, continuamos
        filterChain.doFilter(request, response);
    }
}