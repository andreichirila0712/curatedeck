package andrei.chirila.prove_yourself.domain;

import andrei.chirila.prove_yourself.infrastructure.persistence.PostgresUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {
    private static final String USER_ID = "1";

    @Autowired
    PostgresUserRepository userRepository;
    @Autowired
    TestEntityManager entityManager;

    @Test
    void savesAndFindsUser() {
        var user = new User();
        user.setId(USER_ID);

        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findById(USER_ID)).isPresent();
    }

    @Test
    void deletesUser() {
        var user = new User();
        user.setId(USER_ID);

        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        userRepository.deleteById(USER_ID);

        var result = userRepository.findById(USER_ID);
        assertThat(result).isNotPresent();
    }
}
