package com.dockersim.service;

import com.dockersim.entity.CommandHistory;
import com.dockersim.repository.CommandHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommandHistoryService {

    private final CommandHistoryRepository commandHistoryRepository;

    public void saveCommandHistory(Long userId, String command, String output,
            boolean success, String simulationId) {
        try {
            // User 참조를 위해서는 UserService가 필요하지만, 일단 기본 구현으로 진행
            CommandHistory history = CommandHistory.builder()
                    .command(command)
                    .result(output)
                    .success(success)
                    .simulationId(simulationId)
                    .build();

            commandHistoryRepository.save(history);
            log.debug("명령어 히스토리 저장 완료: userId={}, command={}", userId, command);

        } catch (Exception e) {
            log.error("명령어 히스토리 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public List<CommandHistory> getCommandHistory(Long userId, String simulationId) {
        // 현재는 User 참조가 설정되지 않으므로 시뮬레이션 ID로만 조회
        return commandHistoryRepository.findAll()
                .stream()
                .filter(h -> simulationId.equals(h.getSimulationId()))
                .toList();
    }

    public List<CommandHistory> getCommandHistory(String simulationId) {
        return commandHistoryRepository.findAll()
                .stream()
                .filter(h -> simulationId.equals(h.getSimulationId()))
                .toList();
    }

    public List<CommandHistory> getRecentCommandHistory(Long userId, int limit) {
        return commandHistoryRepository.findAll()
                .stream()
                .limit(limit)
                .toList();
    }
}