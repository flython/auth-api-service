package top.okfly.auth.portal.controller.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * create user request dto
 */
@Data
public class CreateUserReq {
    @NotEmpty
    private String userName;

    @Size(min = 6, max = 16)
    private String password;
}
