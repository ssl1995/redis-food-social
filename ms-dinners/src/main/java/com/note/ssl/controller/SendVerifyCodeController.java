package com.note.ssl.controller;

import com.note.ssl.commons.model.domain.ResultInfo;
import com.note.ssl.commons.utils.ResultInfoUtil;
import com.note.ssl.service.SendVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: SongShengLin
 * @Date: 2022/10/14 15:07
 * @Describe:
 */
@RestController
public class SendVerifyCodeController {

    @Autowired
    private SendVerifyCodeService sendVerifyCodeService;

    @Autowired
    private HttpServletRequest request;


    /**
     * 发送短信功能
     *
     * @param phone 手机号
     * @return 结果
     * send需要网关放行
     */
    @GetMapping("/send")
    public ResultInfo send(String phone) {
        sendVerifyCodeService.send(phone);
        return ResultInfoUtil.buildSuccess(request.getServletPath(), "发送成功");
    }
}
