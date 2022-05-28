package top.okfly.auth.business;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okfly.auth.business.domain.Auth;
import top.okfly.auth.business.domain.Role;
import top.okfly.auth.business.service.PwdEncryptHandler;
import top.okfly.auth.infra.exception.RoleException;
import top.okfly.auth.infra.exception.UserException;
import top.okfly.auth.infra.mapping.AuthBeanMapper;
import top.okfly.auth.infra.repository.RoleRepository;
import top.okfly.auth.infra.repository.UserRepository;
import top.okfly.auth.infra.repository.UserRoleRelationRepository;
import top.okfly.auth.infra.repository.entity.RoleEntity;
import top.okfly.auth.infra.repository.entity.UserEntity;
import top.okfly.auth.infra.repository.entity.UserRoleRelationEntity;
import top.okfly.auth.portal.controller.req.BindRoleReq;

import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    private PwdEncryptHandler pwdEncryptService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRelationRepository userRoleRelationRepository;

    public UserEntity createUser(String userName, Auth auth) {
        if (userRepository.ifExistUser(userName)) {
            throw UserException.of(UserException.Situation.USER_EXIST);
        }

        auth = pwdEncryptService.doEncryptAuth(auth);

        UserEntity user = UserEntity.builder()
                .userName(userName)
                .auth(auth.getAuth())
                .salt(auth.getSalt())
                .build();
        userRepository.save(user);
        return user;

    }

    public void deleteUser(long id) {
        if (userRepository.deleteById(id) == 0) {
            throw UserException.of(UserException.Situation.DELETE_FAIL_NOT_EXIST);
        }
    }

    public void createRole(Role rolePo) {
        roleRepository.save(new RoleEntity(rolePo.getRoleName()));
    }

    public void deleteRole(Role rolePo) {
        roleRepository.find(rolePo.getRoleName()).ifPresentOrElse(r -> {
            roleRepository.delete(new RoleEntity(rolePo.getRoleName()));
        }, () -> {
            throw RoleException.of(RoleException.Situation.ROLE_NOT_EXIST);
        });
    }

    public void bindRole(BindRoleReq bindRoleReq) {
        if (roleRepository.find(bindRoleReq.getRoleName()).isEmpty()) {
            throw RoleException.of(RoleException.Situation.ROLE_NOT_EXIST);
        }


        UserRoleRelationEntity urre = AuthBeanMapper.Ins.toUserRoleRelationEntity(bindRoleReq);
        if (userRoleRelationRepository.existRelation(urre).isEmpty()) {
            userRoleRelationRepository.save(urre);
        }
    }

    public List<String> listRoles(@NonNull Long userId) {
        return userRoleRelationRepository.listRoleByUserId(userId);
    }

    public void unbindRole(Long userId, String roleName) {
        if (userRoleRelationRepository.listRoleByUserId(userId).contains(roleName)) {
            userRoleRelationRepository.delete(new UserRoleRelationEntity(userId, roleName));
        }
    }
}
