package top.okfly.auth.business.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.okfly.auth.business.AuthService;
import top.okfly.auth.business.domain.Token;
import top.okfly.auth.business.service.DefaultEncryptHelper;
import top.okfly.auth.business.service.TokenEncryptHandler;
import top.okfly.auth.infra.exception.AuthException;

import javax.servlet.http.HttpServletRequest;


@Aspect
@Component
public class PreAuthorizeHandler {

    @Autowired
    private TokenEncryptHandler tokenEncryptHandler;
    @Autowired
    private AuthService authService;

    @Pointcut("@annotation(top.okfly.auth.business.annotation.PreAuthorize)")
    public void aroundMethod() {
    }


    @Around(value = "(aroundMethod() && @annotation(preAuthorize))")
    public Object advice(ProceedingJoinPoint pjp, PreAuthorize preAuthorize) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        // here can try to cache in session
        parseAndSetTokenToSession(attributes);
        return pjp.proceed();
    }

    private void parseAndSetTokenToSession(ServletRequestAttributes attributes) {
        HttpServletRequest request = attributes.getRequest();
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            throw AuthException.of(AuthException.Situation.TOKEN_PARSE_ERROR);
        }
        var tokenStr = authorization.substring(DefaultEncryptHelper.BEARER.length());

        Token token = tokenEncryptHandler.decryptTokenStr(tokenStr);
        if (!authService.ifTokenEfficient(token)) {
            throw AuthException.of(AuthException.Situation.TOKEN_EXPIRED);
        }
        attributes.setAttribute("token", token, RequestAttributes.SCOPE_REQUEST);
    }
}
