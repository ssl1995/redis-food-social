package com.note.ssl.service;

import cn.hutool.core.bean.BeanUtil;
import com.note.ssl.commons.constant.ApiConstant;
import com.note.ssl.commons.model.domain.ResultInfo;
import com.note.ssl.commons.utils.AssertUtil;
import com.note.ssl.commons.utils.ResultInfoUtil;
import com.note.ssl.config.OAuth2ClientConfiguration;
import com.note.ssl.domain.OAuthDinerInfo;
import com.note.ssl.vo.LoginDinerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.LinkedHashMap;

/**
 * @author SongShengLin
 * @date 2022/10/13 20:33
 * @description
 */
@Service
public class DinnerService {

    @Resource
    private RestTemplate restTemplate;
    @Value("${service.name.ms-oauth-server}")
    private String oauthServerName;
    @Resource
    private OAuth2ClientConfiguration oAuth2ClientConfiguration;

    /**
     * 登录校验
     */
    public ResultInfo signIn(String account, String password, String path) {
        // 参数校验
        AssertUtil.isNotEmpty(account, "请输入登录账号！");
        AssertUtil.isNotEmpty(password, "请输入登录密码！");

        // 请求头和请求体
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.set("username", account);
        body.set("password", password);
        // 将Bean转换成Map
        body.setAll(BeanUtil.beanToMap(oAuth2ClientConfiguration));
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        // 发送Http请求
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(oAuth2ClientConfiguration.getClientId(), oAuth2ClientConfiguration.getSecret()));
        // 网关->dinner服务>权限认证服务
        ResponseEntity<ResultInfo> result = restTemplate.postForEntity(oauthServerName + "oauth/token", entity, ResultInfo.class);

        AssertUtil.isTrue(result.getStatusCode() != HttpStatus.OK, "登录失败！");
        ResultInfo resultInfo = result.getBody();
        // 登录失败
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            resultInfo.setData(resultInfo.getMessage());
            return resultInfo;
        }
        // 登录成功
        // 将Map转成Bean
        OAuthDinerInfo dinnerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(), new OAuthDinerInfo(), false);
        LoginDinerInfo loginDinerInfo = new LoginDinerInfo();
        loginDinerInfo.setNickname(dinnerInfo.getNickname());
        loginDinerInfo.setAvatarUrl(dinnerInfo.getAvatarUrl());
        loginDinerInfo.setToken(dinnerInfo.getAccessToken());
        return ResultInfoUtil.buildSuccess(path, loginDinerInfo);
    }


}
