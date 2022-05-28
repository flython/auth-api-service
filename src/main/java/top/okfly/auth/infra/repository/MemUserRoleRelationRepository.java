package top.okfly.auth.infra.repository;

import org.springframework.stereotype.Repository;
import top.okfly.auth.infra.repository.entity.UserRoleRelationEntity;

import java.util.*;

@Repository
public class MemUserRoleRelationRepository implements UserRoleRelationRepository {

    final private Map<Long, List<String>> user2Roles = new HashMap<>();
    final private Map<String, List<Long>> role2Users = new HashMap<>();

    @Override
    public synchronized UserRoleRelationEntity save(UserRoleRelationEntity userRoleRelation) {
        List<String> roles = user2Roles.computeIfAbsent(userRoleRelation.getUserId(), k -> new LinkedList<>());
        List<Long> users = role2Users.computeIfAbsent(userRoleRelation.getRoleName(), k -> new LinkedList<>());
        roles.add(userRoleRelation.getRoleName());
        users.add(userRoleRelation.getUserId());
        return userRoleRelation;
    }

    @Override
    public Integer delete(UserRoleRelationEntity userRoleRelation) {
        List<String> roles = user2Roles.computeIfAbsent(userRoleRelation.getUserId(), k -> new LinkedList<>());
        List<Long> users = role2Users.computeIfAbsent(userRoleRelation.getRoleName(), k -> new LinkedList<>());
        roles.remove(userRoleRelation.getRoleName());
        users.remove(userRoleRelation.getUserId());
        return 1;
    }

    @Override
    public Optional<UserRoleRelationEntity> existRelation(UserRoleRelationEntity userRoleRelation) {
        if (user2Roles.getOrDefault(userRoleRelation.getUserId(), Collections.emptyList()).contains(userRoleRelation.getRoleName())) {
            return Optional.of(userRoleRelation);
        }
        return Optional.empty();
    }


    @Override
    public List<String> listRoleByUserId(Long userId) {
        return user2Roles.getOrDefault(userId, Collections.emptyList());
    }
}
