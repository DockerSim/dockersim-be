// Docker 시뮬레이터 V2 - 3영역 구조

// 전역 상태 관리
const state = {
    currentPath: ['docker'],
    containers: [],
    images: [],
    networks: ['default'],
    activeNetwork: 'default',
    commandHistory: []
};

// 명령어 구조 정의
const commandStructure = {
    docker: {
        // Container 명령어
        run: {
            icon: 'fas fa-play',
            color: 'linear-gradient(135deg, #28a745, #20c997)',
            description: '컨테이너 실행',
            next: 'images' // 이미지 선택으로 이동
        },
        ps: {
            icon: 'fas fa-list',
            color: 'linear-gradient(135deg, #17a2b8, #138496)',
            description: '컨테이너 목록',
            execute: true
        },
        stop: {
            icon: 'fas fa-stop',
            color: 'linear-gradient(135deg, #dc3545, #c82333)',
            description: '컨테이너 중지',
            next: 'running-containers'
        },
        start: {
            icon: 'fas fa-play-circle',
            color: 'linear-gradient(135deg, #28a745, #20c997)',
            description: '컨테이너 시작',
            next: 'stopped-containers'
        },
        rm: {
            icon: 'fas fa-trash',
            color: 'linear-gradient(135deg, #dc3545, #c82333)',
            description: '컨테이너 삭제',
            next: 'all-containers'
        },
        exec: {
            icon: 'fas fa-terminal',
            color: 'linear-gradient(135deg, #6f42c1, #5a2d91)',
            description: '컨테이너 접속',
            next: 'running-containers'
        },
        logs: {
            icon: 'fas fa-file-alt',
            color: 'linear-gradient(135deg, #fd7e14, #e55d00)',
            description: '로그 확인',
            next: 'all-containers'
        },

        // Image 명령어
        pull: {
            icon: 'fas fa-download',
            color: 'linear-gradient(135deg, #007bff, #0056b3)',
            description: '이미지 다운로드',
            next: 'image-names'
        },
        push: {
            icon: 'fas fa-upload',
            color: 'linear-gradient(135deg, #6f42c1, #5a2d91)',
            description: '이미지 업로드',
            next: 'local-images'
        },
        images: {
            icon: 'fas fa-layer-group',
            color: 'linear-gradient(135deg, #ffc107, #e0a800)',
            description: '이미지 목록',
            execute: true
        },
        build: {
            icon: 'fas fa-hammer',
            color: 'linear-gradient(135deg, #20c997, #17a2b8)',
            description: '이미지 빌드',
            next: 'build-options'
        },
        tag: {
            icon: 'fas fa-tag',
            color: 'linear-gradient(135deg, #6c757d, #545b62)',
            description: '이미지 태그',
            next: 'local-images'
        },
        rmi: {
            icon: 'fas fa-trash-alt',
            color: 'linear-gradient(135deg, #dc3545, #c82333)',
            description: '이미지 삭제',
            next: 'local-images'
        },

        // Network 명령어
        network: {
            icon: 'fas fa-network-wired',
            color: 'linear-gradient(135deg, #17a2b8, #138496)',
            description: '네트워크 관리',
            next: {
                create: { description: '네트워크 생성', execute: true },
                ls: { description: '네트워크 목록', execute: true },
                rm: { description: '네트워크 삭제', next: 'networks' },
                connect: { description: '컨테이너 연결', next: 'network-connect' },
                disconnect: { description: '컨테이너 분리', next: 'network-disconnect' }
            }
        },

        // Volume 명령어
        volume: {
            icon: 'fas fa-hdd',
            color: 'linear-gradient(135deg, #fd7e14, #e55d00)',
            description: '볼륨 관리',
            next: {
                create: { description: '볼륨 생성', execute: true },
                ls: { description: '볼륨 목록', execute: true },
                rm: { description: '볼륨 삭제', next: 'volumes' },
                inspect: { description: '볼륨 정보', next: 'volumes' }
            }
        }
    }
};

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
    renderInitialCards();
});

function initializeApp() {
    // 초기 상태 설정
    addToTerminal('Docker 시뮬레이터가 시작되었습니다!', 'success');
    addToTerminal('카드를 클릭하여 명령어를 구성하거나 직접 입력하세요.', 'text-muted');
    addToTerminal('');
    
    // 샘플 이미지 추가
    state.images = [
        { name: 'nginx', tag: 'latest', size: '133MB' },
        { name: 'redis', tag: 'alpine', size: '32MB' },
        { name: 'mysql', tag: '8.0', size: '544MB' },
        { name: 'node', tag: '18-alpine', size: '110MB' }
    ];
}

function setupEventListeners() {
    const cliInput = document.getElementById('cliInput');
    
    // CLI 입력 이벤트
    cliInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            executeCommand();
        }
    });

    // 히스토리 탐색
    cliInput.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowUp' && state.commandHistory.length > 0) {
            e.preventDefault();
            cliInput.value = state.commandHistory[state.commandHistory.length - 1];
        }
    });
}

function renderInitialCards() {
    const cardWrapper = document.getElementById('cardWrapper');
    const currentCommands = commandStructure.docker;
    
    cardWrapper.innerHTML = '';
    
    Object.keys(currentCommands).forEach(command => {
        const config = currentCommands[command];
        const card = createCommandCard(command, config);
        cardWrapper.appendChild(card);
    });
}

function createCommandCard(command, config) {
    const card = document.createElement('div');
    card.className = 'command-card';
    card.style.background = config.color;
    card.onclick = () => handleCardClick(command, config);
    
    card.innerHTML = `
        <i class="${config.icon}"></i>
        <div><strong>${command}</strong></div>
        <small>${config.description}</small>
    `;
    
    return card;
}

function handleCardClick(command, config) {
    // 경로 업데이트
    if (state.currentPath.length === 1) {
        state.currentPath.push(command);
    } else {
        state.currentPath[1] = command;
    }
    
    updateBreadcrumb();
    
    if (config.execute) {
        // 즉시 실행
        const fullCommand = state.currentPath.join(' ');
        executeDockerCommand(fullCommand);
    } else if (config.next) {
        // 다음 단계로 이동
        renderNextCards(config.next);
    }
}

function updateBreadcrumb() {
    const breadcrumb = document.getElementById('commandBreadcrumb');
    breadcrumb.innerHTML = state.currentPath.map((item, index) => {
        if (index === 0) {
            return `<span class="text-primary">${item}</span>`;
        }
        return `<i class="fas fa-chevron-right mx-2"></i><span class="text-secondary">${item}</span>`;
    }).join('');
}

function renderNextCards(nextType) {
    const cardWrapper = document.getElementById('cardWrapper');
    cardWrapper.innerHTML = '';
    
    if (typeof nextType === 'object') {
        // 서브 명령어들
        Object.keys(nextType).forEach(subCommand => {
            const config = nextType[subCommand];
            const card = createSubCommandCard(subCommand, config);
            cardWrapper.appendChild(card);
        });
    } else {
        // 동적 카드 생성
        switch (nextType) {
            case 'images':
                renderImageCards();
                break;
            case 'running-containers':
                renderContainerCards(true);
                break;
            case 'stopped-containers':
                renderContainerCards(false);
                break;
            case 'all-containers':
                renderContainerCards();
                break;
            case 'local-images':
                renderLocalImageCards();
                break;
            case 'image-names':
                renderPopularImageCards();
                break;
            default:
                renderGenericCards(nextType);
        }
    }
}

function createSubCommandCard(subCommand, config) {
    const card = document.createElement('div');
    card.className = 'command-card';
    card.style.background = 'linear-gradient(135deg, #6c757d, #545b62)';
    card.onclick = () => handleSubCommandClick(subCommand, config);
    
    card.innerHTML = `
        <i class="fas fa-arrow-right"></i>
        <div><strong>${subCommand}</strong></div>
        <small>${config.description}</small>
    `;
    
    return card;
}

function handleSubCommandClick(subCommand, config) {
    state.currentPath.push(subCommand);
    updateBreadcrumb();
    
    if (config.execute) {
        const fullCommand = state.currentPath.join(' ');
        executeDockerCommand(fullCommand);
    } else if (config.next) {
        renderNextCards(config.next);
    }
}

// 이미지 카드 렌더링
function renderImageCards() {
    const cardWrapper = document.getElementById('cardWrapper');
    
    const popularImages = [
        'nginx', 'redis', 'mysql', 'postgres', 'mongo', 'node', 'python', 'ubuntu'
    ];
    
    popularImages.forEach(image => {
        const card = document.createElement('div');
        card.className = 'command-card';
        card.style.background = 'linear-gradient(135deg, #28a745, #20c997)';
        card.onclick = () => executeImageCommand(image);
        
        card.innerHTML = `
            <i class="fab fa-docker"></i>
            <div><strong>${image}</strong></div>
            <small>인기 이미지</small>
        `;
        
        cardWrapper.appendChild(card);
    });
}

function executeImageCommand(image) {
    const command = `${state.currentPath.join(' ')} ${image}`;
    executeDockerCommand(command);
}

// 컨테이너 카드 렌더링
function renderContainerCards(running) {
    const cardWrapper = document.getElementById('cardWrapper');
    
    let containers = state.containers;
    if (running !== undefined) {
        containers = containers.filter(c => c.running === running);
    }
    
    if (containers.length === 0) {
        cardWrapper.innerHTML = '<div class="text-muted text-center">컨테이너가 없습니다</div>';
        return;
    }
    
    containers.forEach(container => {
        const card = document.createElement('div');
        card.className = 'command-card';
        card.style.background = container.running ? 
            'linear-gradient(135deg, #28a745, #20c997)' : 
            'linear-gradient(135deg, #6c757d, #495057)';
        card.onclick = () => executeContainerCommand(container.name);
        
        card.innerHTML = `
            <i class="fas fa-cube"></i>
            <div><strong>${container.name}</strong></div>
            <small>${container.running ? '실행중' : '중지됨'}</small>
        `;
        
        cardWrapper.appendChild(card);
    });
}

function executeContainerCommand(containerName) {
    const command = `${state.currentPath.join(' ')} ${containerName}`;
    executeDockerCommand(command);
}

// 명령어 실행
async function executeCommand() {
    const input = document.getElementById('cliInput');
    const command = input.value.trim();
    
    if (!command) return;
    
    input.value = '';
    state.commandHistory.push(command);
    
    executeDockerCommand(command);
}

async function executeDockerCommand(command) {
    addToTerminal(`$ ${command}`, 'prompt');
    
    const requestBody = {
        command: command,
        simulationId: 'test-simulation',
        userId: 1
    };
    
    console.log('API 요청:', requestBody);
    
    try {
        const response = await fetch('/api/docker/execute', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });
        
        console.log('HTTP 응답 상태:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.log('오류 응답 내용:', errorText);
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('API 응답:', result);
        
        if (result.code === 'SUCCESS' && result.data) {
            handleCommandResult(command, result.data);
        } else {
            addToTerminal(`오류: ${result.message || '명령어 실행 실패'}`, 'error');
        }
        
    } catch (error) {
        console.error('API 오류 상세:', error);
        addToTerminal(`API 오류: ${error.message}`, 'error');
    }
    
    addToTerminal('');
}

function handleCommandResult(command, data) {
    addToTerminal('✓ 명령어가 성공적으로 실행되었습니다.', 'success');
    
    // 서버 응답 출력
    if (data.output) {
        addToTerminal(data.output, 'text-light');
    }
    
    // 서버에서 받은 상태 데이터로 업데이트
    if (data.containers) {
        updateContainersFromServer(data.containers);
    }
    if (data.images) {
        updateImagesFromServer(data.images);
    }
    if (data.networks) {
        updateNetworksFromServer(data.networks);
    }
    
    // 시각화 업데이트
    updateVisualization();
}

// 서버 데이터로 상태 업데이트
function updateContainersFromServer(containers) {
    state.containers = containers.map(container => ({
        id: container.id,
        name: container.name,
        image: `${container.imageName}:${container.imageTag}`,
        running: container.status === 'RUNNING',
        network: state.activeNetwork,
        ports: parseJsonArray(container.ports),
        volumes: parseJsonArray(container.volumes),
        position: getRandomPosition()
    }));
}

function updateImagesFromServer(images) {
    state.images = images.map(image => ({
        name: image.imageName,
        tag: image.imageTag,
        size: image.size || '???MB'
    }));
}

function updateNetworksFromServer(networks) {
    const networkNames = networks.map(network => network.name);
    
    // 새로운 네트워크가 있으면 탭 추가
    networkNames.forEach(networkName => {
        if (!state.networks.includes(networkName)) {
            state.networks.push(networkName);
            addNetworkTab(networkName);
        }
    });
}

function parseJsonArray(jsonString) {
    try {
        return jsonString ? JSON.parse(jsonString) : [];
    } catch (e) {
        return [];
    }
}

// 시각화 업데이트
function updateVisualization() {
    const containersArea = document.getElementById('containersArea');
    containersArea.innerHTML = '';
    
    if (state.containers.length === 0) {
        containersArea.innerHTML = `
            <div class="text-center text-muted mt-5">
                <i class="fas fa-cubes" style="font-size: 48px; opacity: 0.3;"></i>
                <p class="mt-3">컨테이너가 실행되면 여기에 표시됩니다</p>
            </div>
        `;
        return;
    }
    
    state.containers.forEach(container => {
        const containerEl = createContainerElement(container);
        containersArea.appendChild(containerEl);
    });
}

function createContainerElement(container) {
    const div = document.createElement('div');
    div.className = `container-visual ${container.running ? 'running' : 'stopped'}`;
    div.style.left = container.position.x + 'px';
    div.style.top = container.position.y + 'px';
    div.onclick = () => showContainerInfo(container);
    
    div.innerHTML = `
        <i class="fas fa-cube"></i>
        <div style="font-size: 12px;">${container.name}</div>
        <small style="font-size: 10px;">${container.image}</small>
    `;
    
    return div;
}

function showContainerInfo(container) {
    const modal = new bootstrap.Modal(document.getElementById('containerModal'));
    const modalBody = document.getElementById('containerModalBody');
    
    modalBody.innerHTML = `
        <div class="row">
            <div class="col-md-6">
                <h6><i class="fas fa-info-circle"></i> 기본 정보</h6>
                <table class="table table-sm">
                    <tr><td>이름</td><td>${container.name}</td></tr>
                    <tr><td>이미지</td><td>${container.image}</td></tr>
                    <tr><td>상태</td><td><span class="badge ${container.running ? 'bg-success' : 'bg-secondary'}">${container.running ? '실행중' : '중지됨'}</span></td></tr>
                    <tr><td>네트워크</td><td>${container.network}</td></tr>
                </table>
            </div>
            <div class="col-md-6">
                <h6><i class="fas fa-network-wired"></i> 포트 매핑</h6>
                ${container.ports.length > 0 ? 
                    `<ul class="list-unstyled">${container.ports.map(p => `<li>${p}</li>`).join('')}</ul>` :
                    '<p class="text-muted">포트 매핑 없음</p>'
                }
                
                <h6><i class="fas fa-hdd"></i> 볼륨 마운트</h6>
                ${container.volumes.length > 0 ? 
                    `<ul class="list-unstyled">${container.volumes.map(v => `<li>${v}</li>`).join('')}</ul>` :
                    '<p class="text-muted">볼륨 마운트 없음</p>'
                }
            </div>
        </div>
    `;
    
    modal.show();
}

// 이미지 목록 표시
function showImagesList() {
    const modal = new bootstrap.Modal(document.getElementById('imagesModal'));
    const modalBody = document.getElementById('imagesModalBody');
    
    if (state.images.length === 0) {
        modalBody.innerHTML = '<p class="text-muted">로컬 이미지가 없습니다.</p>';
    } else {
        modalBody.innerHTML = `
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>이미지</th>
                            <th>태그</th>
                            <th>크기</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${state.images.map(img => `
                            <tr>
                                <td><i class="fab fa-docker text-primary"></i> ${img.name}</td>
                                <td><span class="badge bg-secondary">${img.tag}</span></td>
                                <td>${img.size}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }
    
    modal.show();
}

// 네트워크 생성
function createNetwork() {
    const modal = new bootstrap.Modal(document.getElementById('networkModal'));
    modal.show();
}

function executeNetworkCreate() {
    const networkName = document.getElementById('networkName').value.trim();
    if (!networkName) return;
    
    executeDockerCommand(`docker network create ${networkName}`);
    
    // 입력 필드 초기화
    document.getElementById('networkName').value = '';
    
    bootstrap.Modal.getInstance(document.getElementById('networkModal')).hide();
}

function addNetworkTab(networkName) {
    const tabsContainer = document.querySelector('.network-tabs');
    const addButton = tabsContainer.querySelector('.add-network-btn');
    
    const tab = document.createElement('div');
    tab.className = 'network-tab';
    tab.dataset.network = networkName;
    tab.innerHTML = `<i class="fas fa-network-wired"></i> ${networkName}`;
    tab.onclick = () => switchNetwork(networkName);
    
    tabsContainer.insertBefore(tab, addButton);
}

function switchNetwork(networkName) {
    // 탭 활성화 업데이트
    document.querySelectorAll('.network-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelector(`[data-network="${networkName}"]`).classList.add('active');
    
    state.activeNetwork = networkName;
    updateVisualization();
}

// 카드 선택 초기화
function resetCardSelection() {
    state.currentPath = ['docker'];
    updateBreadcrumb();
    renderInitialCards();
}

// 터미널 관련 함수들
function addToTerminal(text, className = '') {
    const terminal = document.getElementById('cliOutput');
    const div = document.createElement('div');
    div.className = className;
    div.textContent = text;
    terminal.appendChild(div);
    terminal.scrollTop = terminal.scrollHeight;
}

function clearTerminal() {
    const terminal = document.getElementById('cliOutput');
    terminal.innerHTML = `
        <div class="prompt">docker-sim:~$ </div>
        <div class="text-muted">터미널이 초기화되었습니다.</div>
        <div class="prompt">docker-sim:~$ </div>
    `;
}

// 유틸리티 함수들
function extractImageName(command) {
    const parts = command.split(' ');
    return parts[parts.length - 1] || 'unknown';
}

function extractContainerName(command) {
    const nameIndex = command.indexOf('--name');
    if (nameIndex !== -1) {
        const parts = command.split(' ');
        return parts[nameIndex + 1];
    }
    return null;
}

function extractPorts(command) {
    const ports = [];
    const regex = /-p\s+(\S+)/g;
    let match;
    while ((match = regex.exec(command)) !== null) {
        ports.push(match[1]);
    }
    return ports;
}

function extractVolumes(command) {
    const volumes = [];
    const regex = /-v\s+(\S+)/g;
    let match;
    while ((match = regex.exec(command)) !== null) {
        volumes.push(match[1]);
    }
    return volumes;
}

function getRandomPosition() {
    return {
        x: Math.random() * 300 + 50,
        y: Math.random() * 200 + 50
    };
}

// 인기 이미지 카드 렌더링
function renderPopularImageCards() {
    const cardWrapper = document.getElementById('cardWrapper');
    
    const popularImages = [
        'nginx:latest', 'redis:alpine', 'mysql:8.0', 'postgres:13', 
        'mongo:latest', 'node:18-alpine', 'python:3.9', 'ubuntu:20.04'
    ];
    
    cardWrapper.innerHTML = '';
    
    popularImages.forEach(image => {
        const card = document.createElement('div');
        card.className = 'command-card';
        card.style.background = 'linear-gradient(135deg, #007bff, #0056b3)';
        card.onclick = () => executeImageCommand(image);
        
        card.innerHTML = `
            <i class="fab fa-docker"></i>
            <div><strong>${image.split(':')[0]}</strong></div>
            <small>${image.split(':')[1] || 'latest'}</small>
        `;
        
        cardWrapper.appendChild(card);
    });
}

// 로컬 이미지 카드 렌더링
function renderLocalImageCards() {
    const cardWrapper = document.getElementById('cardWrapper');
    cardWrapper.innerHTML = '';
    
    if (state.images.length === 0) {
        cardWrapper.innerHTML = '<div class="text-muted text-center mt-4">로컬 이미지가 없습니다</div>';
        return;
    }
    
    state.images.forEach(image => {
        const card = document.createElement('div');
        card.className = 'command-card';
        card.style.background = 'linear-gradient(135deg, #ffc107, #e0a800)';
        card.onclick = () => executeImageCommand(`${image.name}:${image.tag}`);
        
        card.innerHTML = `
            <i class="fas fa-layer-group"></i>
            <div><strong>${image.name}</strong></div>
            <small>${image.tag}</small>
        `;
        
        cardWrapper.appendChild(card);
    });
} 