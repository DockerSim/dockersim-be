package com.dockersim.service.image;


import com.dockersim.config.SimulationUserPrincipal;
import com.dockersim.dto.response.DockerImageResponse;
import java.util.List;

public interface DockerImageService {

    /**
     * Dockerfile을 통해 이미지를 생성합니다.
     *
     * @param principal      인증 정보
     * @param dockerFilePath 도커 파일 경로
     * @param name           생성할 이미지 이름(repo[:tag])
     */
    DockerImageResponse build(SimulationUserPrincipal principal, String dockerFilePath,
        String name);

    /**
     * Show the history of an image
     *
     * @param principal   인증 정보
     * @param nameOrHexId 레이어를 조회할 이미지명 또는 ID
     */
    List<String> history(SimulationUserPrincipal principal, String nameOrHexId);


    /**
     * Display detailed information on one or more images
     *
     * @param principal   인증 정보
     * @param nameOrHexId 레이어를 조회할 이미지명 또는 ID
     */
    List<String> inspect(SimulationUserPrincipal principal, String nameOrHexId);

    /**
     * The default docker images will show all top level images, their repository and tags.
     *
     * @param principal 인증 정보
     * @param all       댕글링 이미지 반환 여부
     * @param quiet     이미지 ID만 반환 여부
     */
    List<String> ls(SimulationUserPrincipal principal, boolean all, boolean quiet);

    /**
     * Remove all dangling images. If '-a' is specified, also remove all images not referenced by
     * any container.
     *
     * @param principal 인증 정보
     * @param all       컨테이너가 연결되지 않는 모든 이미지 삭제로 동작 변경
     */
    List<DockerImageResponse> prune(SimulationUserPrincipal principal, boolean all);

    /**
     * 도커 이미지를 다운로드 받습니다. repo 필드에 '/'가 없으면 'library/' 를 붙인 후 공식저장소에서만 검색 '/'가 있다면 개인 허브에서만 검색
     *
     * @param principal 인증 정보
     * @param name      업로드할 도커명
     * @param all       동일한 이름을 가진 모든 이미지 반환 여부
     */
    List<DockerImageResponse> pull(SimulationUserPrincipal principal, String name, boolean all);

    /**
     * 도커 이미지를 개인 허브에 업로드 합니다. 허브에 동일한 ID의 이미지가 있다면, 덮어쓰지 않습니다.
     *
     * @param principal 인증 정보
     * @param name      업로드할 도커명
     */
    DockerImageResponse push(SimulationUserPrincipal principal, String name);

    /**
     * 도커 이미지를 삭제합니다. 기본적으로, 삭제하려는 이미지를 기반으로 하는 컨테이너가 있다면 삭제에 실패합니다.
     *
     * @param principal   인증 정보
     * @param nameOrHexId 삭제할 도커 이미지명 또는 ID
     * @param force       해당 이미지를 기반으로 만들어진 컨테이너가 있어도 삭제 여부
     */
    DockerImageResponse rm(SimulationUserPrincipal principal, String nameOrHexId, boolean force);
}