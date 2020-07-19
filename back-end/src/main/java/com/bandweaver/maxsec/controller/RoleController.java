package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.Role;
import com.bandweaver.maxsec.service.RoleService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @GetMapping("/roles")
    public R getList(HttpServletRequest request) {
        log.info("get roles:");
        String authorization = request.getHeader("Authorization");
        Integer id = JwtUtil.getId(authorization);

        List<Role> roles = roleService.getRolesByUser(id.intValue());
        return new R(roles);
    }

    @GetMapping("/allroles")
    public R getAllList() {
        return new R(roleService.getRoles());
    }

}
