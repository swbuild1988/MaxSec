package com.bandweaver.maxsec.config.shiro;

import com.bandweaver.maxsec.entity.Permission;
import com.bandweaver.maxsec.entity.Role;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.PermissionService;
import com.bandweaver.maxsec.service.RoleService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 身份识别
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        log.info("-----------------------doGetAuthenticationInfo-------------------------");

        String token = (String) authenticationToken.getCredentials();
        log.info("token:  " + token);

        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("401");
        }

        // 当前从数据库获取，每次访问都将耗时，以后修改为从缓存获取
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new AuthenticationException("401");
        }

        if (!JwtUtil.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("401");
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }

    /**
     * 授权访问控制
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String username = JwtUtil.getUsername(principalCollection.toString());

        // TODO
        // 后续通过redis实现本体缓存
        User user = userService.getUserByName(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<Role> t_rs = roleService.getRolesByUser(user.getId());
        List<String> roles = new ArrayList<>();
        for (Role r : t_rs) {
            roles.add(r.getName());
        }
        simpleAuthorizationInfo.addRoles(roles);

        List<Permission> t_ps = permissionService.getListByUser(user.getId());
        List<String> permissions = new ArrayList<>();
        for (Permission p : t_ps) {
            permissions.add((p.getName()));
        }
        simpleAuthorizationInfo.addStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

}
