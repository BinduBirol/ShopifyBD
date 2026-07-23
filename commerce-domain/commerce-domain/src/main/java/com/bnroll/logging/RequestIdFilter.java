package com.bnroll.logging;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String requestId = request.getHeader(LoggingConstants.REQUEST_ID);

            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString();
            }

            MdcUtil.setRequestId(requestId);

            // Return it to the client as well
            response.setHeader(LoggingConstants.REQUEST_ID, requestId);

            filterChain.doFilter(request, response);

        } finally {
            MdcUtil.clear();
        }
    }
}