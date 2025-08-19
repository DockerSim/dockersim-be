package com.dockersim.service.user;

import com.dockersim.domain.User;
import java.util.UUID;

public interface UserFinder {

    User findUserByUUID(UUID userId);

    User findUserByEmail(String email);
}
