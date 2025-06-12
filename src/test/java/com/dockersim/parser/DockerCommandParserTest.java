package com.dockersim.parser;

import com.dockersim.dto.ParsedDockerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

class DockerCommandParserTest {

    private DockerCommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new DockerCommandParser();
    }

    @Nested
    @DisplayName("Container 명령어 테스트")
    class ContainerCommandTests {

        @Test
        @DisplayName("docker run 명령어 - 기본 형태")
        void testDockerRun_Basic() {
            // Given
            String command = "docker run nginx";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getMainCommand()).isEqualTo("docker");
            assertThat(result.getGroup()).isEqualTo("container");
            assertThat(result.getSubCommand()).isEqualTo("run");
            assertThat(result.getImageName()).isEqualTo("nginx");
            assertThat(result.getImageTag()).isEqualTo("latest");
        }

        @Test
        @DisplayName("docker run 명령어 - 태그 포함")
        void testDockerRun_WithTag() {
            // Given
            String command = "docker run nginx:1.20";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getImageName()).isEqualTo("nginx");
            assertThat(result.getImageTag()).isEqualTo("1.20");
            assertThat(result.getFullImageName()).isEqualTo("nginx:1.20");
        }

        @Test
        @DisplayName("docker run 명령어 - 모든 옵션 포함")
        void testDockerRun_WithAllOptions() {
            // Given
            String command = "docker run -d --name mycontainer -p 8080:80 -e ENV=production -v /host:/container --network mynetwork nginx:latest";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getContainerName()).isEqualTo("mycontainer");
            assertThat(result.getNetworkName()).isEqualTo("mynetwork");
            assertThat(result.hasFlag("-d")).isTrue();
            assertThat(result.getMultiOption("-p")).containsExactly("8080:80");
            assertThat(result.getMultiOption("-e")).containsExactly("ENV=production");
            assertThat(result.getMultiOption("-v")).containsExactly("/host:/container");
        }

        @Test
        @DisplayName("docker ps 명령어")
        void testDockerPs() {
            // Given
            String command = "docker ps -a";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("container");
            assertThat(result.getSubCommand()).isEqualTo("ps");
            assertThat(result.hasFlag("-a")).isTrue();
        }

        @Test
        @DisplayName("docker stop 명령어")
        void testDockerStop() {
            // Given
            String command = "docker stop mycontainer";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("container");
            assertThat(result.getSubCommand()).isEqualTo("stop");
            assertThat(result.getArguments()).containsExactly("mycontainer");
        }

        @Test
        @DisplayName("docker exec 명령어")
        void testDockerExec() {
            // Given
            String command = "docker exec -it mycontainer bash";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("container");
            assertThat(result.getSubCommand()).isEqualTo("exec");
            assertThat(result.hasFlag("-it")).isTrue();
            assertThat(result.getArguments()).containsExactly("mycontainer", "bash");
        }
    }

    @Nested
    @DisplayName("Image 명령어 테스트")
    class ImageCommandTests {

        @Test
        @DisplayName("docker pull 명령어")
        void testDockerPull() {
            // Given
            String command = "docker pull redis:alpine";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("image");
            assertThat(result.getSubCommand()).isEqualTo("pull");
            assertThat(result.getArguments()).containsExactly("redis:alpine");
        }

        @Test
        @DisplayName("docker images 명령어")
        void testDockerImages() {
            // Given
            String command = "docker images";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("image");
            assertThat(result.getSubCommand()).isEqualTo("images");
        }

        @Test
        @DisplayName("docker build 명령어")
        void testDockerBuild() {
            // Given
            String command = "docker build -t myapp:v1.0 .";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("image");
            assertThat(result.getSubCommand()).isEqualTo("build");
            assertThat(result.getOption("-t")).isEqualTo("myapp:v1.0");
            assertThat(result.getArguments()).containsExactly(".");
        }

        @Test
        @DisplayName("docker tag 명령어")
        void testDockerTag() {
            // Given
            String command = "docker tag nginx:latest mynginx:v1.0";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("image");
            assertThat(result.getSubCommand()).isEqualTo("tag");
            assertThat(result.getArguments()).containsExactly("nginx:latest", "mynginx:v1.0");
        }
    }

    @Nested
    @DisplayName("Network 명령어 테스트")
    class NetworkCommandTests {

        @Test
        @DisplayName("docker network create 명령어")
        void testDockerNetworkCreate() {
            // Given
            String command = "docker network create mynetwork";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("network");
            assertThat(result.getSubCommand()).isEqualTo("create");
            assertThat(result.getArguments()).containsExactly("mynetwork");
        }

        @Test
        @DisplayName("docker network ls 명령어")
        void testDockerNetworkLs() {
            // Given
            String command = "docker network ls";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("network");
            assertThat(result.getSubCommand()).isEqualTo("ls");
        }

        @Test
        @DisplayName("docker network connect 명령어")
        void testDockerNetworkConnect() {
            // Given
            String command = "docker network connect mynetwork mycontainer";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("network");
            assertThat(result.getSubCommand()).isEqualTo("connect");
            assertThat(result.getArguments()).containsExactly("mynetwork", "mycontainer");
        }
    }

    @Nested
    @DisplayName("Volume 명령어 테스트")
    class VolumeCommandTests {

        @Test
        @DisplayName("docker volume create 명령어")
        void testDockerVolumeCreate() {
            // Given
            String command = "docker volume create myvolume";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("volume");
            assertThat(result.getSubCommand()).isEqualTo("create");
            assertThat(result.getArguments()).containsExactly("myvolume");
        }

        @Test
        @DisplayName("docker volume ls 명령어")
        void testDockerVolumeLs() {
            // Given
            String command = "docker volume ls";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getGroup()).isEqualTo("volume");
            assertThat(result.getSubCommand()).isEqualTo("ls");
        }
    }

    @Nested
    @DisplayName("복합 옵션 테스트")
    class ComplexOptionsTests {

        @Test
        @DisplayName("멀티 포트 매핑")
        void testMultiplePortMappings() {
            // Given
            String command = "docker run -p 8080:80 -p 8443:443 nginx";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getMultiOption("-p")).containsExactly("8080:80", "8443:443");
        }

        @Test
        @DisplayName("멀티 환경 변수")
        void testMultipleEnvironmentVariables() {
            // Given
            String command = "docker run -e ENV=prod -e DEBUG=false -e PORT=3000 myapp";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getMultiOption("-e")).containsExactly("ENV=prod", "DEBUG=false", "PORT=3000");
        }

        @Test
        @DisplayName("멀티 볼륨 마운트")
        void testMultipleVolumeMounts() {
            // Given
            String command = "docker run -v /host1:/container1 -v /host2:/container2 nginx";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getMultiOption("-v")).containsExactly("/host1:/container1", "/host2:/container2");
        }
    }

    @Nested
    @DisplayName("따옴표 처리 테스트")
    class QuoteHandlingTests {

        @Test
        @DisplayName("공백이 포함된 값 처리")
        void testQuotedValues() {
            // Given
            String command = "docker run -e \"MESSAGE=hello world\" --name \"my container\" nginx";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getMultiOption("-e")).containsExactly("MESSAGE=hello world");
            assertThat(result.getContainerName()).isEqualTo("my container");
        }

        @Test
        @DisplayName("단일 따옴표 처리")
        void testSingleQuotes() {
            // Given
            String command = "docker run -e 'PASSWORD=my secret' nginx";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isTrue();
            assertThat(result.getMultiOption("-e")).containsExactly("PASSWORD=my secret");
        }
    }

    @Nested
    @DisplayName("오류 케이스 테스트")
    class ErrorCaseTests {

        @Test
        @DisplayName("빈 명령어")
        void testEmptyCommand() {
            // Given
            String command = "";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).isEqualTo("명령어가 비어있습니다.");
        }

        @Test
        @DisplayName("docker로 시작하지 않는 명령어")
        void testNonDockerCommand() {
            // Given
            String command = "ls -la";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).isEqualTo("명령어는 'docker'로 시작해야 합니다.");
        }

        @Test
        @DisplayName("docker run 이미지 이름 누락")
        void testRunWithoutImage() {
            // Given
            String command = "docker run";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("이미지 이름이 필요합니다");
        }

        @Test
        @DisplayName("docker exec 인수 부족")
        void testExecInsufficientArgs() {
            // Given
            String command = "docker exec mycontainer";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("컨테이너 이름과 실행할 명령어가 필요합니다");
        }

        @Test
        @DisplayName("지원하지 않는 명령어")
        void testUnsupportedCommand() {
            // Given
            String command = "docker unsupported";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("지원하지 않는 명령어입니다");
        }

        @Test
        @DisplayName("docker tag 인수 부족")
        void testTagInsufficientArgs() {
            // Given
            String command = "docker tag nginx";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("원본 이미지와 새 태그가 필요합니다");
        }

        @Test
        @DisplayName("docker network connect 인수 부족")
        void testNetworkConnectInsufficientArgs() {
            // Given
            String command = "docker network connect mynetwork";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("네트워크 이름과 컨테이너 이름이 필요합니다");
        }
    }

    @Nested
    @DisplayName("엣지 케이스 테스트")
    class EdgeCaseTests {

        @Test
        @DisplayName("null 입력")
        void testNullInput() {
            // Given
            String command = null;

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).isEqualTo("명령어가 비어있습니다.");
        }

        @Test
        @DisplayName("공백만 있는 입력")
        void testWhitespaceOnlyInput() {
            // Given
            String command = "   ";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).isEqualTo("명령어가 비어있습니다.");
        }

        @Test
        @DisplayName("docker 명령어만 입력")
        void testDockerCommandOnly() {
            // Given
            String command = "docker";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("docker 뒤에 실행할 명령어를 입력해주세요");
        }

        @Test
        @DisplayName("그룹 명령어만 입력")
        void testGroupCommandOnly() {
            // Given
            String command = "docker container";

            // When
            ParsedDockerCommand result = parser.parse(command);

            // Then
            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrorMessage()).contains("container 뒤에 실행할 명령어를 입력해주세요");
        }
    }
}