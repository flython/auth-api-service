package top.okfly.auth.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.okfly.auth.business.UserRoleService;
import top.okfly.auth.business.domain.Auth;
import top.okfly.auth.infra.mapping.AuthBeanMapper;
import top.okfly.auth.infra.repository.entity.UserEntity;
import top.okfly.auth.portal.controller.req.CreateUserReq;
import top.okfly.auth.portal.controller.resp.CreateUserResp;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRoleService userRoleService;

    @PostMapping()
    public CreateUserResp createUser(@Valid @RequestBody CreateUserReq createUserReq) {
        UserEntity user = userRoleService.createUser(createUserReq.getUserName(), new Auth(createUserReq.getPassword()));
        return AuthBeanMapper.Ins.toCreateUserResp(user);
    }


    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userRoleService.deleteUser(userId);
    }
}
