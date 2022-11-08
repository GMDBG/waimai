package com.example.rgwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rgwaimai.common.R;
import com.example.rgwaimai.entity.User;
import com.example.rgwaimai.service.UserService;
import com.example.rgwaimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @authro zl
 * @create 2022-11-07-23:35
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位手机号
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将验证码保存到session
            log.info("手机为{}，验证码为{}",phone,code);
            session.setAttribute(phone,code);

            return R.success("手机验证码发送成功");
        }

        return R.error("手机验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取session中的验证码
        Object sCode = session.getAttribute(phone);
        //比较验证码
        if(code!=null && sCode.equals(code)){
            //如果成功则登陆成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号是不是新用户，如果是新用户则自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        String userId = session.getAttribute("user").toString();
        session.removeAttribute(userId);
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}