package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OncePerRequestFilter를 상속 받는다면 모든 요청마다 한 번만 실행!
 * JWT Token을 검사하고, 인증 정보를 SecurityContextHolder에 등록
 * 인증 실패 시, 에러 응답을 JSON으로 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // JWT Token을 파싱하고 검증하는 유틸 클래스
    private final ObjectMapper objectMapper; // 에러 응답을 JSON으로 직렬화 하는데 사용

    /**
     * 인증 흐름
     * doFilterInternal -> Authorization 헤더 확인 -> JWT 추출 -> JWT 검증 및 인증 처리
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String jwt = jwtUtil.substringToken(authorizationHeader);

        if (!processAuthentication(jwt, request, response)) { // 검증 실패 시 에러 응답 반환하고 요청 중단
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean processAuthentication(String jwt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Claims claims = jwtUtil.extractClaims(jwt); // JWT를 파싱해서 Claims 객체 추출

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                setAuthentication(claims);
            }
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT 만료: userId={}, URI={}", e.getClaims().getSubject(), request.getRequestURI());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            log.error("JWT 검증 실패 [{}]: URI={}", e.getClass().getSimpleName(), request.getRequestURI(), e);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "인증이 필요합니다.");
        } catch (Exception e) {
            log.error("예상치 못한 오류: URI={}", request.getRequestURI(), e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다.");
        }
        return false; // 검증 실패
    }

    // 인증 객체 등록
    private void setAuthentication(Claims claims) {

        Long userId = Long.valueOf(claims.getSubject());

        String email = claims.get("email", String.class);

        UserRole userRole = UserRole.of(claims.get("userRole", String.class));

        AuthUser authUser = new AuthUser(userId, email, userRole); // JWT에서 사용자 정보를 추출 후 AuthUser 객체 생성

        // JwtAuthenticationToken 으로 감싸서 SpringSecurity 인증 컨텍스트에 등록
        Authentication authenticationToken = new JwtAuthenticationToken(authUser);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // 에러 응답
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", status.value());
        errorResponse.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
