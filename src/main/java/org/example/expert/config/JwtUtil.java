package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
/**
 * JWT를 생성(create), 파싱(parse), 검증(validate)하는 기능을 담당
 * 참고 파싱이란? 데이터를 분석하고 해석해서 원하는 구조로 변환하는 작업을 말한다.
 * 예를 들어
 * - String data = "name = 철수 & age = 15";
 * - 이 문자를 파싱한다면 -> name = 철수, age = 15 라는 key-value 로 나눌 수 있다.
 * - 결과적으로 -> Map<String, String> 형태로 변환 가능
 * Spring Security의 인증 필터 JwtAuthenticationFilter 에서 이 클래스를 사용해 Token을 처리
 */
@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // base64로 인코딩된 문자열을 디코딩해서 JWT 서명에 사용할 Key 객체로 변환
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Token 생성
    public String createToken(Long userId, String email, UserRole userRole, String nickname) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("userRole", userRole)
                        .claim("nickname", nickname) // token에 유저의 닉네임 정보를 추가한다.
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7); // 순수한 token의 값만을 가져오기 위해 "bearer " 총 7자를 제외하고 가져온다.
        }
        throw new ServerException("Not Found Token");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
