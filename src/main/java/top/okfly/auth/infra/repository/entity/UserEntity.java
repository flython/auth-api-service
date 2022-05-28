package top.okfly.auth.infra.repository.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserEntity {
    long userId;
    String userName;
    String auth;
    String salt;
}
