package com.dockersim.service.user;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dockersim.domain.User;
import com.dockersim.dto.request.UserRequest;
import com.dockersim.dto.response.UserResponse;
import com.dockersim.exception.BusinessException;
import com.dockersim.exception.code.UserErrorCode;
import com.dockersim.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private final UserRepository userRepository;
	// private final UserFinder userFinder; // UserFinder 제거

	@Override
	public UserResponse createUser(UserRequest request) {
		validateEmailFormat(request.getEmail());
		validateEmailDuplication(request.getEmail());

		User savedUser = userRepository.save(User.fromUserRequest(request));
		return UserResponse.from(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUser(String publicId) { // 파라미터 이름 변경
		// userFinder 대신 userRepository 사용
		return UserResponse.from(userRepository.findByPublicId(publicId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, publicId)));
	}

	@Override
	public void deleteUser(String publicId) { // 파라미터 이름 변경
		// userFinder 대신 userRepository 사용
		userRepository.delete(userRepository.findByPublicId(publicId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, publicId)));
	}

	@Override
	public void updateEmail(String publicId, String email) {
		validateEmailFormat(email);
		validateEmailDuplication(email); // 새 이메일 중복 확인

		User user = userRepository.findByPublicId(publicId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND, publicId));

		user.setEmail(email); // User 엔티티에 setEmail 메서드가 필요합니다.
		userRepository.save(user); // 변경된 이메일 저장
	}

	private void validateEmailFormat(String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new BusinessException(UserErrorCode.INVALID_EMAIL_FORMAT, email);
		}
	}

	private void validateEmailDuplication(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS, email);
		}
	}

	// findUserByUUID, findUserByEmail 메서드 제거
}
