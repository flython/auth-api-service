package top.okfly.auth.business.service;

import top.okfly.auth.business.domain.Auth;

public interface PwdEncryptHandler {

    Auth doEncryptAuth(Auth auth);
}
