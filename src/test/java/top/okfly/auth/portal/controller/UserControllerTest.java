package top.okfly.auth.portal.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.okfly.auth.infra.exception.UserException;
import top.okfly.auth.portal.controller.advice.CommonRespWrapper;
import top.okfly.auth.portal.controller.req.CreateUserReq;
import top.okfly.auth.portal.controller.resp.CreateUserResp;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@WebAppConfiguration
class UserControllerTest extends ApiTest {

    @Autowired
    UserController userController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(CommonRespWrapper.class).build();
    }


    @CsvSource(value = {
            "Fly0,4864684sa",
            "Fly1,486468wagw4sa",
            "Fly2,wgwgwegssge",
            "Fly3,gsdhwy",
            "Fly4,yurhfngirhjfudhg",
    })
    @ParameterizedTest
    void createUserSuccess(String name, String pwd) throws Exception {
        CreateUserReq req = new CreateUserReq();
        req.setUserName(name);
        req.setPassword(pwd);

        MvcResult mvcResult = post(req, "/user");
        var resp = readData(mvcResult, CreateUserResp.class);
        log.info("errot+" + name);
        assertNotNull(resp);
        assertNotNull(resp.getUserId());
        assertEquals(req.getUserName(), resp.getUserName());

        //Should fail if the user already exists
        MvcResult exception = post(req, "/user");
        assertEquals(readMsg(exception), UserException.Situation.USER_EXIST.msg);

        MvcResult delResult = delete("/user/" + resp.getUserId());
        MvcResult mvcResult2 = post(req, "/user");
        var resp2 = readData(mvcResult2, CreateUserResp.class);

        assertNotEquals(resp.getUserId(), resp2.getUserId());
        assertEquals(resp.getUserName(), resp2.getUserName());

    }

    @CsvSource(value = {
            ",nameEmpty",
            "pwdToolong,11111111111111111",
            "pwdTooshort,11111",
    })
    @ParameterizedTest
    void createUserFailed(String name, String pwd) throws Exception {
        CreateUserReq req = new CreateUserReq();
        req.setUserName(name);
        req.setPassword(pwd);

        //exception
        MvcResult exception = post(req, "/user");
        assertNotNull(readMsg(exception));

    }

    @Test
    void deleteUser() {
        //Should fail if the role already exists
        MvcResult mvcResult = delete("/user/" + "114514");
        assertEquals(UserException.Situation.DELETE_FAIL_NOT_EXIST.msg, readMsg(mvcResult));
    }
}