package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.UserDto;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SendMessageUtil;
import com.itheima.reggie.utils.ValidateCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SendMessageUtil sendMessageUtil;

    @PostMapping("/sendMsg")
    public R<String>sendMsg(HttpServletRequest request, @RequestBody User user){

        if(StringUtils.hasText(user.getPhone())){
            Integer code = ValidateCodeUtil.generateValidateCode(4);
            sendMessageUtil.sendMessages(user.getPhone(),code);
            request.getSession().setAttribute(user.getPhone(),code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }
    @PostMapping("/login")
    public R<User>login(HttpServletRequest request,@RequestBody UserDto user){

        if(StringUtils.hasText(user.getPhone())&&StringUtils.hasText(user.getCode())){
            Integer code = (Integer) request.getSession().getAttribute(user.getPhone());
            if(code==null)return R.error("登录失败");
            if(!code.toString().equals(user.getCode())){
                log.info("{}",code);
                log.info("{}",user.getCode());
                return R.error("验证码错误");}
            LambdaQueryWrapper<User>lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,user.getPhone());
            User one = userService.getOne(lambdaQueryWrapper);
            if(one==null){
                one = new User();
                user.setStatus(1);
                BeanUtils.copyProperties(user,one);
                userService.save(one);
            }
            request.getSession().setAttribute("user",one.getId());
            log.info("{}",one.getId());
            log.info("{}",request.getSession().getAttribute("user"));
            return R.success(one);
        }
        return R.error("登录失败");
    }

}
