package top.okfly.auth.infra.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.okfly.auth.business.domain.Token;

public abstract class TokenHelper {

    public static Token getToken() {
        return (Token) RequestContextHolder.getRequestAttributes().getAttribute("token", RequestAttributes.SCOPE_REQUEST);
    }
}
