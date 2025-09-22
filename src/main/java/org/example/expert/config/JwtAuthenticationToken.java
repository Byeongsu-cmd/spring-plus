package org.example.expert.config;

import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * AbstractAuthenticationToken을 상속 한다면?
 * getPrincipal (사용자 정보)
 * getCredentials (자격 증명) - 예를 들어 비밀번호
 * getAuthorities (권한 목록)
 * isAuthenticated (인증 여부)
 * 이 위의 메서드들을 기본적으로 구현해주는 추상 클래스
 * 인증 객체로서 필요한 기능을 대부분 제공하므로, 편리하고 안전하게 커스텀 인증 로직을 만들 수 있다. - 실수나 누락 위험이 없다.
 * SecurityContextHolder에 등록할 수 있는 정식 인증 객체가 된다.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final AuthUser authUser; // 사용자의 정보를 담고 있는 dto

  public JwtAuthenticationToken(AuthUser authUser) { // jwt를 통해 인증된 사용자 정보를 받아서 인증 객체로 반환
      super(authUser.getAuthorities());
      this.authUser = authUser;
      setAuthenticated(true);
  }

    @Override
    public Object getCredentials() { // 서버는 사용자의 토큰만 검증, 사용자의 자격 증명이 불필요 그렇기에 null을 반환
        return null;
    }

    @Override
    public Object getPrincipal() { // 사용자의 정보를 반환
        return authUser;
    }
}
