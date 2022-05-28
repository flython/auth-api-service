package top.okfly.auth.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.okfly.auth.business.domain.Auth;
import top.okfly.auth.business.domain.Token;
import top.okfly.auth.business.service.PwdEncryptHandler;
import top.okfly.auth.business.service.TokenEncryptHandler;
import top.okfly.auth.infra.LongIdGenerator;
import top.okfly.auth.infra.exception.UserException;
import top.okfly.auth.infra.repository.UserRepository;
import top.okfly.auth.infra.repository.entity.UserEntity;
import top.okfly.auth.portal.controller.req.AuthReq;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    /**
     * Record the tokenid that is in effect
     */
    private final Set<Long> tokenEfficientIds = new HashSet<>(16);
    /**
     * Record what id the currently logged in user holds, only one token is valid for a user
     */
    private final Map<Long, Long> userHoldingTokenId = new HashMap<>(16);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Value("${auth.token-effective-time:#{2*60*1000}}")
    private long tokenEffectiveTime;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PwdEncryptHandler pwdEncryptHandler;
    @Autowired
    private TokenEncryptHandler tokenEncryptHandler;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LongIdGenerator idGenerator;

    public Token doAuth(AuthReq authReq) {

        Optional<UserEntity> userEntity = userRepository.find(authReq.getUserName());
        var user = userEntity.orElseThrow(() ->
                UserException.of(UserException.Situation.USERNAME_OR_PWD_ERROR));

        Auth toDoAuth = new Auth(authReq.getPassword());
        toDoAuth.setSalt(user.getSalt());
        Auth authed = pwdEncryptHandler.doEncryptAuth(toDoAuth);
        if (!authed.getAuth().equals(user.getAuth())) {
            //failed
            throw UserException.of(UserException.Situation.USERNAME_OR_PWD_ERROR);
        }

        Token token = Token.builder().userId(user.getUserId())
                .roles(userRoleService.listRoles(user.getUserId()))
                .expire(Instant.now().plusMillis(tokenEffectiveTime).toEpochMilli())
                .id(idGenerator.generateId())
                .build();
        tokenEncryptHandler.encryptToken(token);

        synchronized (tokenEfficientIds) {
            Long id = token.getId();
            Long userId = token.getUserId();
            tokenEfficientIds.add(id);
            userHoldingTokenId.put(userId, token.getId());
            //schedule to remove efficient
            scheduler.schedule(() -> {
                synchronized (tokenEfficientIds) {
                    tokenEfficientIds.remove(id);
                    // remove if still hold the id
                    userHoldingTokenId.compute(userId, (k, oldv) -> oldv.equals(id) ? null : oldv);
                }
            }, token.getExpire(), TimeUnit.MILLISECONDS);
        }
        return token;

    }

    public void invalidate(String tokenStr) {
        Token token = tokenEncryptHandler.decryptTokenStr(tokenStr);
        synchronized (tokenEfficientIds) {
            tokenEfficientIds.remove(token.getId());
            // remove if still hold the id
            userHoldingTokenId.compute(token.getUserId(), (k, oldv) -> oldv.equals(token.getId()) ? null : oldv);
        }
    }

    public boolean ifTokenEfficient(Token token) {
        return token.getExpire() > System.currentTimeMillis() && tokenEfficientIds.contains(token.getId());
    }
}
