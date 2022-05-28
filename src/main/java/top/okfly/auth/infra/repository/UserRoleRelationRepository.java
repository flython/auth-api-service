package top.okfly.auth.infra.repository;

import top.okfly.auth.infra.repository.entity.UserRoleRelationEntity;

import java.util.List;
import java.util.Optional;

public interface UserRoleRelationRepository {

    UserRoleRelationEntity save(UserRoleRelationEntity userRoleRelation);

    Integer delete(UserRoleRelationEntity userRoleRelation);

    Optional<UserRoleRelationEntity> existRelation(UserRoleRelationEntity userRoleRelation);

    List<String> listRoleByUserId(Long userId);
}
