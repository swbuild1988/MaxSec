package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.TestRabbitMQ.MQConsumer;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.Permission;
import com.bandweaver.maxsec.entity.Role;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.PermissionService;
import com.bandweaver.maxsec.service.RoleService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private MQConsumer mqConsumer;

    @PostMapping("login")
    public R login(@RequestBody User user) {
        log.info("get userInfo: " + user.toString());

        User loginUser = userService.getUserByName(user.getName());

        if (loginUser == null) {
            return new R(500, new Throwable("用户不存在"));
        } else if (!loginUser.getPassword().equals(user.getPassword())) {
            return new R(500, new Throwable("密码错误"));
        } else {

            List<Role> t_rs = roleService.getRolesByUser(loginUser.getId());
            List<String> roles = new ArrayList<>();
            for (Role r : t_rs) {
                roles.add(r.getName());
            }
            List<Permission> t_ps = permissionService.getListByUser(loginUser.getId());
            List<String> permissions = new ArrayList<>();
            for (Permission p : t_ps) {
                permissions.add((p.getName()));
            }

            // jwt注册
            String token = JwtUtil.sign(loginUser.getId(), loginUser.getName(), loginUser.getPassword());

            JSONObject object = new JSONObject();
            object.put("user", loginUser);
            object.put("roles", roles);
            object.put("permissions", permissions);
            object.put("token", token);

            return new R(object);
        }
    }

    /**
     * 登出，删除对应的消息队列
     *
     * @param request
     * @return
     */
    @GetMapping("logout")
    public R Logout(HttpServletRequest request) {
        return new R();
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("get hello request");
        return "Hello World";
    }

    @GetMapping("/require_auth")
    @RequiresAuthentication
    public String hello2() {
        log.info("get hello require_permission");
        return "require_auth";
    }

    @GetMapping("/require_role")
    @RequiresRoles("ADMIN")
    public String hello3() {
        log.info("get hello require_role");
        return "require_role";
    }

    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"admin", "common:edit", "common:list"})
    public String hello4() {
        log.info("get hello require_permission");
        return "require_permission";
    }


}
