package com.zhang.grade.security;

import com.zhang.grade.common.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException("未登录或 token 缺失");
        }
        try {
            Claims c = jwtUtil.parse(header.substring(7));
            UserContext.set(new LoginUser(
                    Long.valueOf(String.valueOf(c.get("uid"))),
                    String.valueOf(c.get("username")),
                    String.valueOf(c.get("role"))));
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthException("登录已过期，请重新登录");
        } catch (Exception e) {
            throw new AuthException("token 无效");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
