package top.okfly.auth.business.service;

import top.okfly.auth.business.domain.Token;

public interface TokenEncryptHandler {

    void encryptToken(Token token);

    Token decryptTokenStr(String token);
}
