package top.okfly.auth.infra.mapping;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.okfly.auth.infra.repository.entity.UserEntity;
import top.okfly.auth.infra.repository.entity.UserRoleRelationEntity;
import top.okfly.auth.portal.controller.req.BindRoleReq;
import top.okfly.auth.portal.controller.resp.CreateUserResp;

@Mapper
public interface AuthBeanMapper {
    AuthBeanMapper Ins = Mappers.getMapper(AuthBeanMapper.class);

    UserRoleRelationEntity toUserRoleRelationEntity(BindRoleReq bindRoleReq);

    CreateUserResp toCreateUserResp(UserEntity user);
}
