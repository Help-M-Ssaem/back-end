package com.example.mssaem_backend.global.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // refresh Token이 있다면 refresh Token 사용
        String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        if (refreshToken != null && ((HttpServletRequest) request).getRequestURI()
                .equals("/member/refresh") && jwtTokenProvider.validateRefreshToken(refreshToken)) {
            Authentication authentication = jwtTokenProvider.getRefreshAuthentication(refreshToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
