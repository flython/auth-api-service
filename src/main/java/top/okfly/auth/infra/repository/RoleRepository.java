package top.okfly.auth.infra.repository;

import top.okfly.auth.infra.repository.entity.RoleEntity;

import java.util.Optional;

public interface RoleRepository {

    RoleEntity save(RoleEntity role);

    Optional<RoleEntity> find(String roleName);

    Integer delete(RoleEntity role);

    boolean ifExistRole(String roleName);
}
