package com.zhang.grade.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhang.grade.common.AuthException;
import com.zhang.grade.dto.LoginDTO;
import com.zhang.grade.entity.SysUser;
import com.zhang.grade.mapper.SysUserMapper;
import com.zhang.grade.security.JwtUtil;
import com.zhang.grade.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;

    public LoginVO login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new AuthException("用户名或密码错误");
        }
        String md5 = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!md5.equalsIgnoreCase(user.getPassword())) {
            throw new AuthException("用户名或密码错误");
        }
        String token = jwtUtil.create(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole());
    }
}
