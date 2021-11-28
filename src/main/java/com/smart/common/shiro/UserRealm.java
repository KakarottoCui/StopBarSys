package com.smart.common.shiro;

import com.smart.common.util.ShiroUtils;
import com.smart.module.sys.entity.SysUser;
import com.smart.module.sys.service.SysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.List;

/**
 * 用户认证
 * 爪哇笔记：https://blog.52itstyle.vip
 * @author 小柒2012
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private SysUserService userService;

    /**
     * 获取授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Long userId = ShiroUtils.getUserId();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> rolesSet = userService.listUserRoles(userId);
        List<String> permsSet = userService.listUserPerms(userId);
        info.setRoles(new HashSet<>(rolesSet));
        info.setStringPermissions(new HashSet<>(permsSet));
        return info;
    }

    /**
     * 获取认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());
        SysUser user = userService.getUser(username);
        if (user == null) {
            throw new UnknownAccountException("账户不存在");
        }
        if(!password.equals(user.getPassword())) {
            throw new IncorrectCredentialsException("密码不正确");
        }
        return new SimpleAuthenticationInfo(user, password, getName());
    }
}

