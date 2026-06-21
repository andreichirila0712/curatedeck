package andrei.chirila.prove_yourself.domain;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    void deleteById(String id);
}
