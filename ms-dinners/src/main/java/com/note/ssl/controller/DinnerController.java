package com.note.ssl.controller;

import com.note.ssl.commons.model.domain.ResultInfo;
import com.note.ssl.commons.model.dto.DinersDTO;
import com.note.ssl.commons.utils.ResultInfoUtil;
import com.note.ssl.service.DinersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SongShengLin
 * @date 2022/10/13 20:32
 * @description
 */
@RestController
public class DinnerController {

    @Autowired
    private DinersService dinnerService;
    @Autowired
    private HttpServletRequest request;


    /**
     * 注册
     */
    @PostMapping("register")
    public ResultInfo register(@RequestBody DinersDTO dinersDTO) {
        return dinnerService.register(dinersDTO, request.getServletPath());
    }

    /**
     * 校验手机号
     */
    @GetMapping("checkPhone")
    public ResultInfo checkPhone(String phone) {
        dinnerService.checkPhoneIsRegister(phone);
        return ResultInfoUtil.buildSuccess(request.getServletPath(), "手机号已注册！");
    }

    /**
     * 登录
     */
    @GetMapping("signin")
    public ResultInfo signIn(String account, String password) {
        return dinnerService.signIn(account, password, request.getServletPath());
    }


}
