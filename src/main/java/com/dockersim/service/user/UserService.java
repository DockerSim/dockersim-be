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
     * @param publicId 조회할 사용자 public ID (String)
     * @return 조회된 사용자 정보
     */
    UserResponse getUser(String publicId);

    /**
     * 사용자 삭제
     *
     * @param publicId 삭제할 사용자 public ID (String)
     */
    void deleteUser(String publicId);

    /**
     * 사용자 이메일 업데이트
     *
     * @param publicId 업데이트할 사용자의 public ID (String)
     * @param email 새로운 이메일 주소
     */
    void updateEmail(String publicId, String email);
}
