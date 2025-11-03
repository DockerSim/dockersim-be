package com.dockersim.service.user;

import com.dockersim.domain.User;

public interface UserFinder {

    User findUserById(Long id);

    User findUserByPublicId(String userPublicId);

    User findUserByEmail(String email);
}
