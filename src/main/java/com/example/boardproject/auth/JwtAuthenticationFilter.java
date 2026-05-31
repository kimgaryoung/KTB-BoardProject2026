package com.example.boardproject.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor

//1.request에 한번만 실행되는 필터.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    //토큰 검증 없이 통과시킬 url
    private static final String[] WHITE_LIST = {
            "/users", // 회원가입
            "/auth", //로그인
            "/users/token/refresh" // 토큰 재발급
    };


    //2.WHITE_LIST에 있는 url은 토큰 검증 없이 건너뜀
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, request.getRequestURI());
    }

    //3.실제 토큰 검증 로직 -모든 요청마다 실행함.
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        //jwt header에서 서명값 꺼내기.
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 없거나 Bearer로 시작 안하면  401에러(인증 안됨)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰에서 Bearer이루 문자열만 추출
        String token = authHeader.substring(7);

        try {
            // 토큰 서명 + 만료 검증
            jwtProvider.parse(token);

            // access 토큰인지 확인
            if (!jwtProvider.isAccessToken(token)) {
                throw new IllegalArgumentException("Not access token");
            }

            // 여기서는 인증 정보 전달 없이 통과만 시킴
            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            //검증 실패 -401반환(인증 안됨)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}