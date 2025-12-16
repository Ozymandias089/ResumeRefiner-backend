package com.resumerefiner.resumerefinerbackend.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestStatusLogFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestStatusLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("[REQ] {} {} -> {} (cookie={})",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    request.getHeader("Cookie") != null ? "Y" : "N"
            );
        }
    }
}
