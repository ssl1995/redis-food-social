package com.note.ssl.service;

import com.note.ssl.commons.model.domain.SignInIdentity;
import com.note.ssl.commons.model.pojo.Diners;
import com.note.ssl.commons.utils.AssertUtil;
import com.note.ssl.mapper.DinersMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/10/11 16:27
 * @Describe: 登录校验
 */
@Service
public class UserService implements UserDetailsService {


    @Autowired
    private DinersMapper dinersMapper;


    /**
     * 权限验证，默认需要提供的username名字登录验证的接口
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AssertUtil.isNotEmpty(username, "请输入用户名！");
        Diners diners = dinersMapper.selectByAccountInfo(username);
        if (Objects.isNull(diners)) {
            throw new UsernameNotFoundException("用户名或者密码错误，请重新输入！");
        }
//        return new User(username, diners.getPassword(),
//                AuthorityUtils.commaSeparatedStringToAuthorityList(diners.getRoles()));
        SignInIdentity signInIdentity = new SignInIdentity();
        BeanUtils.copyProperties(diners, signInIdentity);
        return signInIdentity;
    }
}
