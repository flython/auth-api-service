package top.okfly.auth.portal.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.okfly.auth.infra.exception.RoleException;
import top.okfly.auth.portal.controller.advice.CommonRespWrapper;
import top.okfly.auth.portal.controller.req.BindRoleReq;
import top.okfly.auth.portal.controller.req.CreateRoleReq;
import top.okfly.auth.portal.controller.req.CreateUserReq;
import top.okfly.auth.portal.controller.resp.CreateUserResp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@WebAppConfiguration
class RoleControllerTest extends ApiTest {

    // using different username pre unit test to avoid collision
    public static final String USER_NAME = "FlyInRoleTest";
    public static final String PASSWORD = "123456";
    static boolean setup = false;
    static Long testUserId;

    @Autowired
    UserController userController;
    @Autowired
    RoleController roleController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController, userController).setControllerAdvice(CommonRespWrapper.class).build();
        if (!setup) {
            CreateUserReq req = new CreateUserReq();
            req.setUserName(USER_NAME);
            req.setPassword(PASSWORD);
            MvcResult result = post(req, "/user");
            CreateUserResp resp = readData(result, CreateUserResp.class);
            testUserId = resp.getUserId();
            setup = true;
        }
    }


    @CsvSource(value = {
            "user1",
            "user2",
            "guest"
    })
    @ParameterizedTest
    void createRole(String roleName) {
        var req = new CreateRoleReq();
        req.setRoleName(roleName);
        MvcResult result1 = post(req, "/role");
        //Should fail if the role already exists
        MvcResult result2 = post(req, "/role");
        assertEquals(RoleException.Situation.ROLE_EXIST.msg, readMsg(result2));
        delete("/role/" + roleName);
    }

    @CsvSource(value = {
            "''",
            "toLongToCreateAAA",
    })
    @ParameterizedTest
    void createRoleError(String roleName) {
        var req = new CreateRoleReq();
        req.setRoleName(roleName);
        MvcResult result1 = post(req, "/role");
        assertTrue(readMsg(result1).strip().startsWith("Validation failed"));
    }

    @Test
    void deleteRole() {
        MvcResult result = delete("/role/" + "someNotExist");
        //Should fail if the role doesn't exist
        assertEquals(RoleException.Situation.ROLE_NOT_EXIST.msg, readMsg(result));
    }

    @Test
    void bindRole2User() {

        var bindRoleReq = new BindRoleReq();
        bindRoleReq.setUserId(testUserId);
        bindRoleReq.setRoleName("testUsers");

        // prepare for role
        var createRoleReq = new CreateRoleReq();
        createRoleReq.setRoleName("testUser");
        MvcResult result1 = post(createRoleReq, "/role");

        MvcResult bind1 = post(bindRoleReq, "/role/binding");
        //If the role is already associated with the user, nothing should happen
        MvcResult bind2 = post(bindRoleReq, "/role/binding");

        bindRoleReq.setRoleName("notExistRole");
        MvcResult notExist = post(bindRoleReq, "/role/binding");

        assertEquals(RoleException.Situation.ROLE_NOT_EXIST.msg, readMsg(notExist));
    }

}