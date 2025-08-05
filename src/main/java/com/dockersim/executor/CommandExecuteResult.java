package com.dockersim.executor;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Docker 명령어 실행 결과
 */
@Getter
@Builder
@ToString
public class CommandExecuteResult {

    /**
     * 실행된 명령어
     */
    private final String command;

    /**
     * 실행 성공 여부
     */
    private final boolean success;

    /**
     * 콘솔 출력 내용
     */
    private final ConsoleOutput console;

    /**
     * 상태 변화 정보
     */
    private final StateChanges stateChanges;

    /**
     * 요약 정보 (카운터 등)
     */
    private final StateSummary summary;

    /**
     * 학습 힌트
     */
    private final LearningHints hints;

    /**
     * 실행 시간
     */
    private final LocalDateTime executedAt;

    /**
     * 에러 메시지 (실행 실패 시)
     */
    private final String errorMessage;

    /**
     * 생성된 리소스 ID (컨테이너 ID 등)
     */
    private final String resourceId;
}

/**
 * 콘솔 출력 정보
 */
@Getter
@Builder
@ToString
class ConsoleOutput {
    /**
     * 출력 라인들
     */
    private final List<String> output;

    /**
     * 최종 출력 라인 (컨테이너 ID 등)
     */
    private final String finalLine;

    /**
     * 성공 여부
     */
    private final boolean success;
}

/**
 * 상태 변화 정보
 */
@Getter
@Builder
@ToString
class StateChanges {
    /**
     * 컨테이너 변화
     */
    private final ResourceChanges containers;

    /**
     * 이미지 변화
     */
    private final ResourceChanges images;

    /**
     * 네트워크 변화
     */
    private final ResourceChanges networks;

    /**
     * 볼륨 변화
     */
    private final ResourceChanges volumes;
}

/**
 * 리소스 변화 정보
 */
@Getter
@Builder
@ToString
class ResourceChanges {
    /**
     * 추가된 리소스들
     */
    private final List<Object> added;

    /**
     * 수정된 리소스들
     */
    private final List<Object> modified;

    /**
     * 제거된 리소스들
     */
    private final List<Object> removed;
}

/**
 * 상태 요약 정보 (백엔드 계산)
 */
@Getter
@Builder
@ToString
class StateSummary {
    /**
     * 총 컨테이너 수
     */
    private final Long totalContainers;

    /**
     * 실행 중인 컨테이너 수
     */
    private final Long runningContainers;

    /**
     * 총 이미지 수
     */
    private final Long totalImages;

    /**
     * 총 네트워크 수
     */
    private final Long totalNetworks;

    /**
     * 총 볼륨 수
     */
    private final Long totalVolumes;
}

/**
 * 학습 힌트 정보
 */
@Getter
@Builder
@ToString
class LearningHints {
    /**
     * 메인 메시지
     */
    private final String message;

    /**
     * 다음 추천 명령어들
     */
    private final List<String> nextSuggestions;

    /**
     * 학습 팁
     */
    private final String learningTip;
}