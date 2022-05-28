package top.okfly.auth.portal.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.okfly.auth.business.domain.Token;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResp {
    Token token;
    long expireTs;

    public String getToken() {
        return token.getTokenString();
    }

    public void setToken(String tokenStr) {
        token = new Token();
        token.setTokenString(tokenStr);
    }
}
