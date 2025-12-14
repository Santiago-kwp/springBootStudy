package com.ssg.myGallery.account.helper;

import com.ssg.myGallery.block.service.BlockService;
import com.ssg.myGallery.common.util.HttpUtils;
import com.ssg.myGallery.common.util.TokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenUtils tokenUtils;
    private final BlockService blockService; // 차단된 토큰 확인

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = HttpUtils.getBearerToken(request);

        if (token != null) {
            // 1. 토큰 유효성 검증
            if (tokenUtils.validateToken(token)) {
                // 2. 블랙리스트 확인
                if (!blockService.has(token)) {
                    // 3. 이미 인증된 상태가 아니라면 SecurityContext에 저장
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        Authentication authentication = tokenUtils.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 디버깅용 로그
                        Claims claims = tokenUtils.parseClaims(token);
                        log.debug("JWT 인증 성공: loginId={}, memberId={}, auth={}",
                                claims.getSubject(),
                                claims.get("memberId"),
                                claims.get("auth"));
                    }
                } else {
                    log.warn("차단된 토큰 사용 시도: {}", token);
                }
            } else {
                log.warn("유효하지 않은 JWT 토큰: {}", token);
            }
        }
        filterChain.doFilter(request, response);
    }
}
