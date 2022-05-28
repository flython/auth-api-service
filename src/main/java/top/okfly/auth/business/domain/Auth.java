package top.okfly.auth.business.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Auth {
    String password;
    String salt;
    String auth;

    public Auth(@NonNull String password) {
        this.password = password;
    }
}
