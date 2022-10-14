package com.note.ssl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.note.ssl.commons.constant.ApiConstant;
import com.note.ssl.commons.utils.ResultInfoUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: SongShengLin
 * @Date: 2022/10/11 15:50
 * @Describe: 认证失败
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 写出
        PrintWriter out = response.getWriter();
        String errorMessage = authException.getMessage();
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = "登录失败！";
        }
        ResultInfoUtil.buildError(ApiConstant.ERROR_CODE, errorMessage, request.getRequestURI());
        try {
            out.write(objectMapper.writeValueAsString(request));
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println("out.write出现异常：" + e.getCause());
        }

    }
}
