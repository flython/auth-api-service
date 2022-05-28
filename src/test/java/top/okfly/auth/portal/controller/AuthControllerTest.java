package top.okfly.auth.portal.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import top.okfly.auth.business.AuthService;
import top.okfly.auth.infra.exception.AuthException;
import top.okfly.auth.portal.controller.advice.CommonRespWrapper;
import top.okfly.auth.portal.controller.req.AuthReq;
import top.okfly.auth.portal.controller.req.BindRoleReq;
import top.okfly.auth.portal.controller.req.CreateRoleReq;
import top.okfly.auth.portal.controller.req.CreateUserReq;
import top.okfly.auth.portal.controller.resp.AuthResp;
import top.okfly.auth.portal.controller.resp.CreateUserResp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
class AuthControllerTest extends ApiTest {


    public static final String PASSWORD = "123456";
    public static final String USER_NAME = "FlyInAuthTest";
    public static final String TEST_ROLE = "testUsers";
    public static final String DEV_ROLE = "devUsers";
    public static final String NO_USER_ROLE = "noUsers";
    static boolean setup = false;
    static Long testUserId;


    @Autowired
    AuthController authController;
    @Autowired
    UserController userController;
    @Autowired
    RoleController roleController;
    @Autowired
    AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController, userController, roleController).setControllerAdvice(CommonRespWrapper.class).build();
        if (!setup) {
            CreateUserReq req = new CreateUserReq();
            req.setUserName(USER_NAME);
            req.setPassword(PASSWORD);
            MvcResult result = post(req, "/user");
            CreateUserResp resp = readData(result, CreateUserResp.class);
            testUserId = resp.getUserId();

            var createRoleReq = new CreateRoleReq();
            createRoleReq.setRoleName(TEST_ROLE);
            post(createRoleReq, "/role");
            createRoleReq.setRoleName(DEV_ROLE);
            post(createRoleReq, "/role");
            createRoleReq.setRoleName(NO_USER_ROLE);
            post(createRoleReq, "/role");

            var bindRoleReq = new BindRoleReq();
            bindRoleReq.setUserId(testUserId);
            bindRoleReq.setRoleName(TEST_ROLE);
            post(bindRoleReq, "/role/binding");
            bindRoleReq.setRoleName(DEV_ROLE);
            post(bindRoleReq, "/role/binding");


            setup = true;
        }
    }

    @SneakyThrows
    @Test
    void authorize() {
        var authReq = new AuthReq();
        authReq.setUserName(USER_NAME);
        authReq.setPassword(PASSWORD);
        MvcResult login = post(authReq, "/authentication");
        AuthResp authResp = readData(login, AuthResp.class);
        assertNotNull(authResp);
        assertNotNull(authResp.getToken());
        assertTrue(StringUtils.hasText(authResp.getToken()));
        assertFalse(login.getResponse().getContentAsString().contains(PASSWORD));
        assertFalse(login.getResponse().getContentAsString().contains(USER_NAME));
        assertTrue(authResp.getExpireTs() > System.currentTimeMillis());
        Field tokenEffectiveTime = ReflectionUtils.findField(AuthService.class, "tokenEffectiveTime");
        tokenEffectiveTime.setAccessible(true);
        Long tokenEffectiveTimeVal = (Long) ReflectionUtils.getField(tokenEffectiveTime, authService);
        assertNotNull(tokenEffectiveTimeVal);
        assertTrue(authResp.getExpireTs() <= System.currentTimeMillis() + tokenEffectiveTimeVal);

    }

    @Test
    void invalidate() {
        // login
        var authReq = new AuthReq();
        authReq.setUserName(USER_NAME);
        authReq.setPassword(PASSWORD);
        MvcResult login = post(authReq, "/authentication");
        AuthResp authResp = readData(login, AuthResp.class);
        cacheHeader("Authorization", authResp.getToken());

        // check token Efficient
        MvcResult afterLogin = get("/authentication/allRoles");
        assertNotNull(readData(afterLogin, ArrayList.class));

        delete("/authentication", Map.of("authorization", authResp.getToken()));

        MvcResult expired = get("/authentication/allRoles");
        //no token will exception
        assertEquals(AuthException.Situation.TOKEN_EXPIRED.msg, readMsg(expired));

    }


    @SneakyThrows
    @Test
    void timeOutExpired() {
        //overwrite expireTime to 3 seconds
        Field tokenEffectiveTime = ReflectionUtils.findField(AuthService.class, "tokenEffectiveTime");
        tokenEffectiveTime.setAccessible(true);
        Long effectTimeBak = (Long) ReflectionUtils.getField(tokenEffectiveTime, authService);
        ReflectionUtils.setField(tokenEffectiveTime, authService, 1000);

        // login
        var authReq = new AuthReq();
        authReq.setUserName(USER_NAME);
        authReq.setPassword(PASSWORD);
        MvcResult login = post(authReq, "/authentication");
        AuthResp authResp = readData(login, AuthResp.class);
        cacheHeader("Authorization", authResp.getToken());

        // prove logined
        MvcResult logined = get("/authentication/checkRole", Map.of("role", DEV_ROLE));
        assertEquals(Boolean.TRUE, readData(logined, Boolean.class));

        Thread.sleep(2000);

        MvcResult expired = get("/authentication/allRoles");
        //token will expired
        assertEquals(AuthException.Situation.TOKEN_EXPIRED.msg, readMsg(expired));


        //recover time
        ReflectionUtils.setField(tokenEffectiveTime, authService, effectTimeBak.longValue());
    }

    @Test
    void checkRole() {
        MvcResult unlogin = get("/authentication/checkRole", Map.of("role", DEV_ROLE));
        assertEquals(AuthException.Situation.TOKEN_PARSE_ERROR.msg, readMsg(unlogin));

        var authReq = new AuthReq();
        authReq.setUserName(USER_NAME);
        authReq.setPassword(PASSWORD);
        MvcResult login = post(authReq, "/authentication");
        AuthResp authResp = readData(login, AuthResp.class);
        cacheHeader("Authorization", authResp.getToken());
        MvcResult logined = get("/authentication/checkRole", Map.of("role", DEV_ROLE));
        assertEquals(Boolean.TRUE, readData(logined, Boolean.class));
        logined = get("/authentication/checkRole", Map.of("role", TEST_ROLE));
        assertEquals(Boolean.TRUE, readData(logined, Boolean.class));

        logined = get("/authentication/checkRole", Map.of("role", TEST_ROLE));
        assertEquals(Boolean.TRUE, readData(logined, Boolean.class));

        logined = get("/authentication/checkRole", Map.of("role", NO_USER_ROLE));
        assertEquals(Boolean.FALSE, readData(logined, Boolean.class));

        logined = get("/authentication/checkRole", Map.of("role", "DOES_NOT_EXIST"));
        assertEquals(Boolean.FALSE, readData(logined, Boolean.class));
    }

    @Test
    void allRoles() {
        MvcResult mvcResult = get("/authentication/allRoles");
        //no token will exception
        assertEquals(AuthException.Situation.TOKEN_PARSE_ERROR.msg, readMsg(mvcResult));

        // login
        var authReq = new AuthReq();
        authReq.setUserName(USER_NAME);
        authReq.setPassword(PASSWORD);
        MvcResult login = post(authReq, "/authentication");
        AuthResp authResp = readData(login, AuthResp.class);
        cacheHeader("Authorization", authResp.getToken());

        MvcResult afterLogin = get("/authentication/allRoles");
        ArrayList<String> roles = readData(afterLogin, ArrayList.class);
        roles.sort(String::compareTo);

        List<String> expected = new ArrayList<>(List.of(DEV_ROLE, TEST_ROLE));
        expected.sort(String::compareTo);
        assertLinesMatch(expected, roles);

    }
}