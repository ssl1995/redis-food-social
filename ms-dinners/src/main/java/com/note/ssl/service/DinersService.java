package com.note.ssl.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.note.ssl.commons.constant.ApiConstant;
import com.note.ssl.commons.model.domain.ResultInfo;
import com.note.ssl.commons.model.dto.DinersDTO;
import com.note.ssl.commons.model.pojo.Diners;
import com.note.ssl.commons.utils.AssertUtil;
import com.note.ssl.commons.utils.ResultInfoUtil;
import com.note.ssl.config.OAuth2ClientConfiguration;
import com.note.ssl.domain.OAuthDinerInfo;
import com.note.ssl.mapper.DinersMapper;
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
public class DinersService {

    @Resource
    private RestTemplate restTemplate;
    @Value("${service.name.ms-oauth-server}")
    private String oauthServerName;
    @Resource
    private OAuth2ClientConfiguration oAuth2ClientConfiguration;
    @Resource
    private SendVerifyCodeService sendVerifyCodeService;

    @Autowired
    private DinersMapper dinersMapper;


    /**
     * 用户注册
     *
     * @param dinersDTO
     * @param path
     * @return
     */
    public ResultInfo register(DinersDTO dinersDTO, String path) {
        // 参数非空校验
        String username = dinersDTO.getUsername();
        AssertUtil.isNotEmpty(username, "请输入用户名");
        String password = dinersDTO.getPassword();
        AssertUtil.isNotEmpty(password, "请输入密码");
        String phone = dinersDTO.getPhone();
        AssertUtil.isNotEmpty(phone, "请输入手机号");
        String verifyCode = dinersDTO.getVerifyCode();
        AssertUtil.isNotEmpty(verifyCode, "请输入验证码");
        // 获取验证码
        String code = sendVerifyCodeService.getCodeByPhone(phone);
        // 验证是否过期
        AssertUtil.isNotEmpty(code, "验证码已过期，请重新发送");
        // 验证码一致性校验
        AssertUtil.isTrue(!dinersDTO.getVerifyCode().equals(code), "验证码不一致，请重新输入");
        // 验证用户名是否已注册
        Diners diners = dinersMapper.selectByUsername(username.trim());
        AssertUtil.isTrue(diners != null, "用户名已存在，请重新输入");
        // 注册
        // 密码加密
        dinersDTO.setPassword(DigestUtil.md5Hex(password.trim()));
        dinersMapper.save(dinersDTO);
        // 自动登录
        return signIn(username.trim(), password.trim(), path);
    }

    public void checkPhoneIsRegister(String phone) {
        AssertUtil.isNotEmpty(phone, "手机号为空");
        Diners diners = dinersMapper.selectByPhone(phone);
        // 直接抛出异常，不优化，需要一个全局异常处理
        AssertUtil.isTrue(diners == null, "该手机号未注册！");
        AssertUtil.isTrue(diners.getIsValid() == 0, "该用户手机号已被锁定！请重新输入！");
    }

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
