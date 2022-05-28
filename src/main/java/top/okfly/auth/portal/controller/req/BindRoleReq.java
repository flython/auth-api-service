package top.okfly.auth.portal.controller.req;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * create user request dto
 */
@Data
public class BindRoleReq {

    @NotNull
    @Min(0)
    Long userId;

    @Size(max = 16)
    @NotEmpty
    private String roleName;
}
