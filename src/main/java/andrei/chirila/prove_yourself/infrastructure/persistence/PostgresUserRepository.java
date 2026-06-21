package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostgresUserRepository extends JpaRepository<User, String> {
}
