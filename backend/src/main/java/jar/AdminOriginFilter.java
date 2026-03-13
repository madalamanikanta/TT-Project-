package jar;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Rejects requests to /admin endpoints if they originate from disallowed origins.
 *
 * This is used to enforce that admin pages are only served from the backend (localhost:8080)
 * and not accessed from the frontend developer server (localhost:5173).
 */
@Component
public class AdminOriginFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:8080"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/admin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String origin = request.getHeader("Origin");

        // If origin header is present, require it to be from localhost:8080.
        // Browsers will include Origin on fetch/XHR; navigation requests usually omit it.
        if (origin != null && !ALLOWED_ORIGINS.contains(origin)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Admin routes are not accessible from this origin\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
