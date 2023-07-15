package com.example.mssaem_backend.global.config.security.jwt;

import com.example.mssaem_backend.domain.member.dto.MemberResponseDto;
import com.example.mssaem_backend.global.config.exception.BaseException;
import com.example.mssaem_backend.global.config.exception.errorCode.AuthErrorCode;
import com.example.mssaem_backend.global.config.security.auth.PrincipalDetails;
import com.example.mssaem_backend.global.config.security.auth.PrincipalDetailsService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.jwt-key}")
    private String jwtSecretKey;

    @Value("${jwt.refresh-key}")
    private String refreshSecretKey;

    private final PrincipalDetailsService principalDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "refreshToken";
    private static final long TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L;  // 유효기간 24시간
    private static final long REF_TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L * 60L;  // 유효기간 2달

    @PostConstruct
    protected void init() {
        jwtSecretKey = Base64.getEncoder().encodeToString(jwtSecretKey.getBytes());
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }

    public String generateAccessToken(Long memberId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);

        Date now = new Date();
        Date accessTokenExpirationTime = new Date(now.getTime() + TOKEN_VALID_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(accessTokenExpirationTime)
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    public MemberResponseDto.TokenInfo generateToken(Long memberId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);

        Date now = new Date();
        Date refreshTokenExpirationTime = new Date(now.getTime() + REF_TOKEN_VALID_TIME);

        String accessToken = generateAccessToken(memberId);
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(refreshTokenExpirationTime)
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();

        return new MemberResponseDto.TokenInfo(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String token) {
        try {
            PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(
                    getMemberIdByToken(token));
            return new UsernamePasswordAuthenticationToken(principalDetails,
                    "", principalDetails.getAuthorities());
        } catch (UsernameNotFoundException exception) {
            throw new BaseException(AuthErrorCode.UNSUPPORTED_JWT);
        }
    }

    public Authentication getRefreshAuthentication(String token) {
        try {
            PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(
                    getMemberIdByRefreshToken(token));
            return new UsernamePasswordAuthenticationToken(principalDetails,
                    "", principalDetails.getAuthorities());
        } catch (UsernameNotFoundException exception) {
            throw new BaseException(AuthErrorCode.UNSUPPORTED_JWT);
        }
    }

    public String getMemberIdByToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).
                getBody().get("memberId").toString();
    }
    public String getMemberIdByRefreshToken(String token) {
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token).
                getBody().get("memberId").toString();
    }
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader(REFRESH_HEADER);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new BaseException(AuthErrorCode.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            throw new BaseException(AuthErrorCode.EXPIRED_MEMBER_JWT);
        } catch (UnsupportedJwtException | SignatureException e) {
            throw new BaseException(AuthErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new BaseException(AuthErrorCode.EMPTY_JWT);
        }
    }
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new BaseException(AuthErrorCode.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            throw new BaseException(AuthErrorCode.EXPIRED_MEMBER_JWT);
        } catch (UnsupportedJwtException | SignatureException e) {
            throw new BaseException(AuthErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new BaseException(AuthErrorCode.EMPTY_JWT);
        }
    }
}
