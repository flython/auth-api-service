package top.okfly.auth.infra.repository;

import top.okfly.auth.infra.repository.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    UserEntity save(UserEntity user);

    Optional<UserEntity> find(String userName);

    Integer deleteById(long id);

    boolean ifExistUser(String userName);
}
