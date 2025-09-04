package com.dockersim.service.user;

import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;

public interface UserService {

    /**
     * 사용자 생성
     *
     * @param request 사용자 생성 요청 정보
     * @return 생성된 사용자 정보
     */
    UserResponse createUser(UserRequest request);

    /**
     * 사용자 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자 정보
     */
    UserResponse getUser(String userId);

    /**
     * 사용자 삭제
     *
     * @param userId 삭제할 사용자 ID
     */
    void deleteUser(String userId);
}
