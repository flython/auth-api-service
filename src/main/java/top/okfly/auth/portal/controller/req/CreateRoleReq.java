package top.okfly.auth.portal.controller.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * create user request dto
 */
@Data
public class CreateRoleReq {

    @Size(max = 16)
    @NotEmpty
    private String roleName;
}
