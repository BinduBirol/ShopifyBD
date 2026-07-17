package com.bnroll.billing.security;

import com.bnroll.commercedomain.enums.ServiceName;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");


        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        try {

            String token = authHeader.substring(7);

            Claims claims = jwtService.extractClaims(token);


            String tokenType = claims.get("type", String.class);


            // Service token handling
            if ("SERVICE".equals(tokenType)) {


                String service = claims.get("service", String.class);


                if (!ServiceName.exists(service)) {
                    throw new BadCredentialsException("Invalid service token");
                }


                ServicePrincipal principal =
                        new ServicePrincipal(service);


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_INTERNAL_SERVICE"
                                        )
                                )
                        );


                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);


                filterChain.doFilter(request, response);
                return;
            }


            // User token handling

            Long userId = Long.parseLong(
                    claims.getSubject()
            );


            String email = claims.get("email", String.class);

            String phone = claims.get("phone", String.class);

            String role = claims.get("role", String.class);


            UserPrincipal principal =
                    new UserPrincipal(
                            userId,
                            email,
                            phone,
                            role
                    );


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + role
                                    )
                            )
                    );


            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);


        } catch (Exception e) {

            e.printStackTrace();

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }


        filterChain.doFilter(request, response);
    }
}