package top.okfly.auth.portal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.okfly.auth.business.AuthService;
import top.okfly.auth.business.UserRoleService;
import top.okfly.auth.business.annotation.PreAuthorize;
import top.okfly.auth.business.domain.Token;
import top.okfly.auth.business.service.DefaultEncryptHelper;
import top.okfly.auth.infra.utils.TokenHelper;
import top.okfly.auth.portal.controller.req.AuthReq;
import top.okfly.auth.portal.controller.resp.AuthResp;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/authentication")
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    UserRoleService userRoleService;

    @PostMapping
    public AuthResp authorize(@Valid @RequestBody AuthReq authReq) {
        Token token = authService.doAuth(authReq);
        return AuthResp.builder()
                .token(token)
                .expireTs(token.getExpire())
                .build();
    }


    @DeleteMapping()
    public void invalidate(String authorization) {
        var token = authorization.substring(DefaultEncryptHelper.BEARER.length());
        authService.invalidate(token);
    }


    @PreAuthorize
    @GetMapping("/checkRole")
    public boolean checkRole(String role) {
        Token token = TokenHelper.getToken();
        return token.getRoles().contains(role);
    }

    @PreAuthorize
    @GetMapping("/allRoles")
    public List<String> allRoles() {
        Long userId = TokenHelper.getToken().getUserId();
        return userRoleService.listRoles(userId);
    }

}
