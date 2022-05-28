package top.okfly.auth.infra.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.okfly.auth.infra.LongIdGenerator;
import top.okfly.auth.infra.exception.UserException;
import top.okfly.auth.infra.repository.entity.UserEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemUserRepository implements UserRepository {

    final private Map<String, UserEntity> userNameIndex = new ConcurrentHashMap<>(8);
    final private Map<Long, UserEntity> userIdIndex = new ConcurrentHashMap<>(8);
    @Autowired
    LongIdGenerator idGenerator;

    @Override
    public synchronized UserEntity save(UserEntity user) {
        return userNameIndex.compute(user.getUserName(), (k, oldv) -> {
            if (oldv != null) {
                throw UserException.of(UserException.Situation.USER_EXIST);
            }
            user.setUserId(idGenerator.generateId());
            userIdIndex.put(user.getUserId(), user);
            return user;
        });
    }

    @Override
    public synchronized Optional<UserEntity> find(String userName) {
        return Optional.ofNullable(userNameIndex.get(userName));
    }

    @Override
    public synchronized Integer deleteById(long id) {
        UserEntity remove = userIdIndex.remove(id);
        if (remove == null) {
            return 0;
        }
        userNameIndex.remove(remove.getUserName());
        return 1;
    }

    @Override
    public boolean ifExistUser(String userName) {
        return find(userName).isPresent();
    }
}
