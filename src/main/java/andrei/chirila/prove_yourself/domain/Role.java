package andrei.chirila.prove_yourself.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    NOT_VERIFIED;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
