package top.okfly.auth.infra.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleRelationEntity {
    Long userId;
    String roleName;
}
