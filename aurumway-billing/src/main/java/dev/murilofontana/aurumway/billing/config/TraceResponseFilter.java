package dev.murilofontana.aurumway.billing.config;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Exposes the current trace id on the {@code X-Trace-Id} response header so
 * clients can correlate a request with server-side logs/traces.
 */
@Component
public class TraceResponseFilter extends OncePerRequestFilter {

    private static final String TRACE_HEADER = "X-Trace-Id";

    private final Tracer tracer;

    public TraceResponseFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var span = tracer.currentSpan();
        if (span != null) {
            response.setHeader(TRACE_HEADER, span.context().traceId());
        }
        chain.doFilter(request, response);
    }
}
