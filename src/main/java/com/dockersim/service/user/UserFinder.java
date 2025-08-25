package com.dockersim.service.user;

import com.dockersim.domain.User;

public interface UserFinder {

    User findUserByUserId(String userId);

    User findUserByEmail(String email);
}
