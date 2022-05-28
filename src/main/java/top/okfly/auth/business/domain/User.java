package top.okfly.auth.business.domain;

import lombok.Data;

@Data
public class User {
    private String userName;
    private Auth auth;
}
