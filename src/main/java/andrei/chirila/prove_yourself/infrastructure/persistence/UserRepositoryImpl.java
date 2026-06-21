package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final PostgresUserRepository postgresUserRepository;

    public UserRepositoryImpl(PostgresUserRepository postgresUserRepository) {
        this.postgresUserRepository = postgresUserRepository;
    }

    @Override
    public User save(User user) {
        return postgresUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return postgresUserRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        postgresUserRepository.deleteById(id);
    }
}
