package com.dockersim.service.volume;

import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.DockerVolumeResponse;
import java.util.List;

public interface DockerVolumeService {

    /**
     * Creates a new volume that containers can consume and store data in. If a name is not
     * specified, Docker generates a random name.
     * <p>
     * 익명/명명 볼륨을 생성합니다. 바인드 볼륨은 컨테이너의 문자열 필드로 대체합니다.
     *
     * @param principal 인증 정보
     * @param name      불륨명
     * @param anonymous 익명 볼륨 여부
     * @return 생성된 불륨 응답 정보
     */
    DockerVolumeResponse create(SimulationUserPrincipal principal, String name, boolean anonymous);

    /**
     * Returns information about a volume.
     *
     * @param principal 인증 정보
     * @param name      조회할 불륨명
     * @return 조회된 불륨 상세 정보
     */
    List<String> inspect(SimulationUserPrincipal principal, String name);

    /**
     * List all the volumes known to Docker
     *
     * @param principal 인증 정보
     * @param quiet     불륨명만 출력 여부
     * @return 조회된 불륨명 목록
     */
    List<String> ls(SimulationUserPrincipal principal, boolean quiet);

    /**
     * Remove all unused local volumes. Unused local volumes are those which are not referenced by
     * any containers. By default, it only removes anonymous volumes.
     *
     * @param principal 인증 정보
     * @param all       익명 볼륨 뿐 아니라, 참조되지 않는 불륨의 삭제 여부
     * @return 삭제된 불륨명 목록
     */
    List<DockerVolumeResponse> prune(SimulationUserPrincipal principal, boolean all);

    /**
     * Remove one or more volumes. You can't remove a volume that's in use by a container.
     *
     * @param principal 인증 정보
     * @param name      삭제할 불륨명
     * @return 삭제된 불륨명
     */
    DockerVolumeResponse rm(SimulationUserPrincipal principal, String name);
}
