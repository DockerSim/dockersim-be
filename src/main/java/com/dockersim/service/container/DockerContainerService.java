package com.dockersim.service.container;


import com.dockersim.dto.response.DockerContainerResponse;
import java.util.List;

/**
 * Docker 컨테이너 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 */
public interface DockerContainerService {

    /**
     * 실행 중인 컨테이너의 현재 상태를 기반으로 새로운 이미지를 생성합니다.
     *
     * @param containerIdOrName 컨테이너의 ID 또는 이름
     * @param newImageName      새로운 이미지의 repository[:tag]
     * @return 생성된 이미지의 정보
     */
    List<String> commitContainer(String containerIdOrName, String newImageName);

    /**
     * 실행 중인 컨테이너의 표준 입출력에 연결합니다. (시뮬레이션)
     *
     * @param nameOrId 컨테이너 이름 또는 ID
     * @return 연결 관련 안내 메시지
     */
    List<String> attach(String nameOrId);

    /**
     * 지정된 이미지를 기반으로 새로운 컨테이너를 생성합니다. (실행은 하지 않음)
     * <p>
     * 생성할 컨테이너의 정보 (이미지, 이름, 포트 등)
     *
     * @return 생성된 컨테이너의 정보
     */
    DockerContainerResponse createContainer(String imageName, String containerName);

    /**
     * 호스트와 컨테이너 간에 파일을 복사합니다. (시뮬레이션)
     *
     * @param source      원본 경로 (e.g., /host/path or containerId:/container/path)
     * @param destination 대상 경로 (e.g., /host/path or containerId:/container/path)
     * @return 복사 작업 결과 메시지
     */
    List<String> copyToFromContainer(String source, String destination);

    /**
     * 컨테이너가 생성된 이후 파일 시스템의 변경 사항을 출력합니다.
     *
     * @param containerIdOrName 컨테이너의 ID 또는 이름
     * @return 변경된 파일 목록
     */
    List<String> diffContainer(String containerIdOrName);

    /**
     * 컨테이너의 파일 시스템을 tar 아카이브로 출력합니다. (시뮬레이션)
     *
     * @param containerIdOrName 컨테이너의 ID 또는 이름
     * @return tar 생성 관련 메시지
     */
    List<String> exportContainer(String containerIdOrName);

    /**
     * 실행 중인 컨테이너 내부에서 명령을 실행합니다. (시뮬레이션)
     *
     * @param containerIdOrName 컨테이너의 ID 또는 이름
     * @param command           실행할 명령어
     * @param interactive       -i, 표준 입력(stdin)을 활성화할지 여부
     * @param tty               -t, 가상 TTY를 할당할지 여부
     * @return 명령어 실행 결과 메시지
     */
    List<String> executeInContainer(String containerIdOrName, String command, boolean interactive,
        boolean tty);

    /**
     * 컨테이너 목록을 조회합니다.
     *
     * @param all   중지된 컨테이너를 포함할지 여부
     * @param quiet 컨테이너 ID만 출력할지 여부
     * @return 조회된 컨테이너 정보 목록
     */
    List<String> listContainers(boolean all, boolean quiet);

    /**
     * 컨테이너의 포트 매핑 정보를 확인합니다.
     *
     * @param containerIdOrName 컨테이너의 ID 또는 이름
     * @return 포트 매핑 정보
     */
    List<String> getContainerPorts(String containerIdOrName);

    /**
     * 중지(exited) 상태의 모든 컨테이너를 삭제합니다.
     *
     * @return 삭제된 컨테이너 정보 및 정리된 공간
     */
    List<String> pruneContainers();

    /**
     * 컨테이너의 이름을 변경합니다.
     *
     * @param oldNameOrId 변경할 컨테이너의 현재 이름 또는 ID
     * @param newName     새로운 이름
     */
    void renameContainer(String oldNameOrId, String newName);

    /**
     * 하나 이상의 컨테이너를 재시작합니다.
     *
     * @param containerIdsOrNames 재시작할 컨테이너의 ID 또는 이름 목록
     * @return 재시작된 컨테이너의 ID 또는 이름 목록
     */
    List<String> restartContainers(List<String> containerIdsOrNames);

    /**
     * 하나 이상의 컨테이너를 삭제합니다.
     *
     * @param containerIdsOrNames 삭제할 컨테이너의 ID 또는 이름 목록
     * @param force               실행 중인 컨테이너를 강제로 삭제할지 여부
     * @return 삭제된 컨테이너의 ID 또는 이름 목록
     */
    List<String> removeContainers(List<String> containerIdsOrNames, boolean force);

    /**
     * 하나 이상의 중지된 컨테이너를 시작합니다.
     *
     * @param containerIdsOrNames 시작할 컨테이너의 ID 또는 이름 목록
     * @return 시작된 컨테이너의 ID 또는 이름 목록
     */
    List<String> startContainers(List<String> containerIdsOrNames);

    /**
     * 하나 이상의 실행 중인 컨테이너를 중지합니다.
     *
     * @param containerIdsOrNames 중지할 컨테이너의 ID 또는 이름 목록
     * @return 중지된 컨테이너의 ID 또는 이름 목록
     */
    List<String> stopContainers(List<String> containerIdsOrNames);

    /**
     * 하나 이상의 실행 중인 컨테이너를 강제로 중지(SIGKILL)합니다.
     *
     * @param containerIdsOrNames 강제 중지할 컨테이너의 ID 또는 이름 목록
     * @return 강제 중지된 컨테이너의 ID 또는 이름 목록
     */
    List<String> killContainers(List<String> containerIdsOrNames);

    /**
     * 컨테이너의 모든 프로세스를 일시 정지합니다.
     *
     * @param containerIdsOrNames 일시 정지할 컨테이너의 ID 또는 이름 목록
     * @return 일시 정지된 컨테이너의 ID 또는 이름 목록
     */
    List<String> pauseContainers(List<String> containerIdsOrNames);

    /**
     * 일시 정지된 컨테이너의 모든 프로세스를 재개합니다.
     *
     * @param containerIdsOrNames 재개할 컨테이너의 ID 또는 이름 목록
     * @return 재개된 컨테이너의 ID 또는 이름 목록
     */
    List<String> unpauseContainers(List<String> containerIdsOrNames);

    /**
     * 컨테이너의 상세 정보를 JSON 형식으로 반환합니다.
     *
     * @param containerIdOrName 조회할 컨테이너의 ID 또는 이름
     * @return 컨테이너 상세 정보 (JSON 문자열)
     */
    String inspectContainer(String containerIdOrName);
}