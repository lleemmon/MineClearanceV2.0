package com.mc.controller;

import cn.hutool.core.util.IdUtil;
import com.mc.common.Constant;
import com.mc.domain.User;
import com.mc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 江辉彬
 * @version 1.0
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping({"/"})
    public String ToLogin()
    {
        return "login";
    }

    @RequestMapping({"/register"})
    public String ToRegister()
    {
        return "register";
    }

    @PostMapping("/addUser")
    public String addUser(User user, HttpServletRequest request){
        String usernameState = userService.checkRegister(user);
        if(usernameState.equals(Constant.RegisterState.UserNameExist)){
            request.setAttribute("msg", "用户名已被注册！");
        }
        else {
            String registerState = userService.insertUser(user);
            switch (registerState) {
                case Constant.RegisterState.Success:
                    request.setAttribute("msg", "用户已成功注册！");
                case Constant.UnKnowError:
                    request.setAttribute("msg", "发生未知错误,注册失败！");
            }
        }
        return "forward:/register";
    }


    @GetMapping("/main")
    public String ToMain(String uuid, HttpSession httpSession){
        User CurrUser = (User)httpSession.getAttribute("CurrUser");
        String sessionUuid = CurrUser.getUuid();
        if(sessionUuid == null)
            return "redirect:/";
        if(uuid != null && uuid.equals(sessionUuid))
            return "main";
        return "redirect:/";
    }

    @PostMapping("/login")
    public String CheckLogin(User user, HttpSession httpSession, HttpServletRequest request){
        String loginState = userService.checkLogin(user);
        switch (loginState) {
            case Constant.LoginState.Success:
                User CurrUser = userService.getUserList(user).get(0);
                String uuid = IdUtil.simpleUUID();
                CurrUser.setUuid(uuid);
                httpSession.setAttribute("CurrUser",CurrUser);
                return "redirect:/main?uuid=" + uuid;
            case Constant.LoginState.PasswordError:
                request.setAttribute("msg", "密码输入错误!");
                return "forward:/";
            case Constant.LoginState.UserNameNotFind:
                request.setAttribute("msg", "用户名不存在!");
                return "forward:/";
        }
        return "forward:/";
    }

    @ResponseBody
    @GetMapping("/checkUserName")
    public String CheckUserName(String username){
        User user = new User();
        user.setUsername(username);
        String state = userService.checkRegister(user);
        switch (state){
            case Constant.RegisterState.UserNameExist:
                return "用户名已存在";
            case Constant.RegisterState.UserNameNotExist:
                return "用户名可用";
        }
        return "";
    }

    @ResponseBody
    @PostMapping("/getUserData")
    public User GetUserData(HttpServletRequest request){
        HttpSession session = request.getSession();
        return (User)(session.getAttribute("CurrUser"));
    }
}
