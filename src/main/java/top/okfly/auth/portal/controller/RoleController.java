package top.okfly.auth.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.okfly.auth.business.UserRoleService;
import top.okfly.auth.business.annotation.PreAuthorize;
import top.okfly.auth.business.domain.Role;
import top.okfly.auth.infra.utils.TokenHelper;
import top.okfly.auth.portal.controller.req.BindRoleReq;
import top.okfly.auth.portal.controller.req.CreateRoleReq;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    UserRoleService userRoleService;

    @PostMapping
    public void createRole(@Valid @RequestBody CreateRoleReq createRoleReq) {
        userRoleService.createRole(new Role(createRoleReq.getRoleName()));
    }


    @DeleteMapping("/{roleName}")
    public void deleteRole(@PathVariable String roleName) {
        userRoleService.deleteRole(new Role(roleName));
    }

    @PostMapping("/binding")
    public void bindRole2User(@Valid @RequestBody BindRoleReq bindRoleReq) {
        userRoleService.bindRole(bindRoleReq);

    }

    @PreAuthorize
    @DeleteMapping("/binding/{roleName}")
    public void unBindRole(@PathVariable String roleName) {

        Long userId = TokenHelper.getToken().getUserId();
        userRoleService.unbindRole(userId, roleName);
    }

}
