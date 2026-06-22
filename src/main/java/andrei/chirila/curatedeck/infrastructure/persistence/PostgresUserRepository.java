package andrei.chirila.curatedeck.infrastructure.persistence;

import andrei.chirila.curatedeck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostgresUserRepository extends JpaRepository<User, String> {
}
