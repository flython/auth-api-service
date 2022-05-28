package top.okfly.auth.business.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import top.okfly.auth.business.domain.Token;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

@SpringBootTest
@WebAppConfiguration
class DefaultEncryptHelperTest {

    @Autowired
    private DefaultEncryptHelper helper;

    @Test
    void encryptToken() {
        Token token = Token.builder().userId(1L).roles(List.of("Admin")).expire(9999999999L).build();
        helper.encryptToken(token);
        String tokenString = token.getTokenString();
        var token2 = helper.decryptTokenStr(tokenString.substring(DefaultEncryptHelper.BEARER.length()));
        assertEquals(token.getId(), token2.getId());
        assertEquals(token.getUserId(), token2.getUserId());
        assertLinesMatch(token.getRoles(), token2.getRoles());
    }

}