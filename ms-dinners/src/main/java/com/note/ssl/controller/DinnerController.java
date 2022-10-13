package com.note.ssl.controller;

import com.note.ssl.commons.model.domain.ResultInfo;
import com.note.ssl.service.DinnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    private DinnerService dinnerService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("signin")
    public ResultInfo signIn(String username, String password) {
        return dinnerService.signIn(username, password, request.getServletPath());
    }


}
