package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

    private final HttpServletRequest request;

    /**
     * Debug로 실행 후 체크하는 법
     * 브레이크 포인트 설정 후 메서드 호출
     * Evaluate 창에서 조회
     * <p>
     * joinPoint.getSignature().getName()
     * result = {"changeUserRole"}
     * <p>
     * joinPoint.getArgs()
     * result ={value = 1, role = "ADMIN"}
     * <p>
     * request.getRequestURI()
     * result = {"/admin/users/1"}
     * <p>
     * request.getAttribute("userId")
     * result = {value = 1}
     * <p>
     * 로그 출력 확인 (Console 화면에서 확인)
     * Admin Access Log - User ID: 1, Request Time: 2025-09-19T09:40:31.999309, Request URL: /admin/users/1, Method: changeUserRole
     */
    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logAfterChangeUserRole(JoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());
    }
}
