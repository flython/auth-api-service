package top.okfly.auth.infra.repository;

import org.springframework.stereotype.Repository;
import top.okfly.auth.infra.exception.RoleException;
import top.okfly.auth.infra.repository.entity.RoleEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class MemRoleRepository implements RoleRepository {

    final private Set<String> roleIndex = new HashSet<>();


    @Override
    public synchronized RoleEntity save(RoleEntity role) {
        if (roleIndex.contains(role.getRoleName())) {
            throw RoleException.of(RoleException.Situation.ROLE_EXIST);
        }
        roleIndex.add(role.getRoleName());
        return new RoleEntity(role.getRoleName());
    }

    @Override
    public Optional<RoleEntity> find(String roleName) {
        if (roleIndex.contains(roleName)) {
            return Optional.of(new RoleEntity(roleName));
        }
        return Optional.empty();
    }

    @Override
    public synchronized Integer delete(RoleEntity role) {
        return null;
    }

    @Override
    public boolean ifExistRole(String roleName) {
        return find(roleName).isPresent();
    }
}
