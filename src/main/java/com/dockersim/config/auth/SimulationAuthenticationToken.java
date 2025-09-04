package com.dockersim.config.auth;

import com.dockersim.config.SimulationUserPrincipal;
import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class SimulationAuthenticationToken extends AbstractAuthenticationToken {

    private final SimulationUserPrincipal principal;

    public SimulationAuthenticationToken(SimulationUserPrincipal principal) {
        super(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
