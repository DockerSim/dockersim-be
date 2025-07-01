// Docker Simulator Enhanced - 도메인 중심 UI with 드래그앤드롭 & 리사이저블 패널
class DockerSimulatorEnhanced {
    constructor() {
        this.containers = new Map();
        this.images = new Map();
        this.volumes = new Map();
        this.networks = new Map(['default', { name: 'default', driver: 'bridge' }]);
        this.gridSlots = [];
        this.gridSize = { cols: 6, rows: 4 };
        this.currentNetwork = 'default';
        this.draggedContainer = null;
        
        this.init();
    }

    init() {
        this.initializeGrid();
        this.setupEventListeners();
        this.setupResizers();
        this.setupDomainTabs();
        this.updateResourceCounts();
        this.loadSampleData();
    }

    // 그리드 시스템 초기화
    initializeGrid() {
        const grid = document.getElementById('containersGrid');
        grid.innerHTML = '';
        this.gridSlots = [];

        for (let i = 0; i < this.gridSize.rows * this.gridSize.cols; i++) {
            const slot = document.createElement('div');
            slot.className = 'grid-slot';
            slot.dataset.slotIndex = i;
            slot.innerHTML = `<small>슬롯 ${i + 1}</small>`;
            
            // 드롭 이벤트 리스너
            slot.addEventListener('dragover', (e) => this.handleDragOver(e));
            slot.addEventListener('drop', (e) => this.handleDrop(e, i));
            
            grid.appendChild(slot);
            this.gridSlots.push({ element: slot, occupied: false, container: null });
        }
    }

    // 도메인 탭 설정
    setupDomainTabs() {
        const tabs = document.querySelectorAll('.domain-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const domain = tab.dataset.domain;
                this.switchDomain(domain);
            });
        });
    }

    switchDomain(domain) {
        // 탭 활성화
        document.querySelectorAll('.domain-tab').forEach(tab => {
            tab.classList.remove('active');
        });
        document.querySelector(`[data-domain="${domain}"]`).classList.add('active');

        // 명령어 영역 전환
        document.querySelectorAll('.domain-commands').forEach(cmd => {
            cmd.classList.remove('active');
        });
        document.getElementById(`${domain}-commands`).classList.add('active');
    }

    // 리사이저 설정
    setupResizers() {
        const horizontalResizer = document.getElementById('horizontalResizer');
        const verticalResizer = document.getElementById('verticalResizer');
        const mainContainer = document.querySelector('.main-container');

        // 가로 리사이저 (왼쪽 패널 크기 조절)
        let isResizingHorizontal = false;
        horizontalResizer.addEventListener('mousedown', (e) => {
            isResizingHorizontal = true;
            document.body.style.cursor = 'col-resize';
        });

        // 세로 리사이저 (위/아래 패널 크기 조절)
        let isResizingVertical = false;
        verticalResizer.addEventListener('mousedown', (e) => {
            isResizingVertical = true;
            document.body.style.cursor = 'row-resize';
        });

        document.addEventListener('mousemove', (e) => {
            if (isResizingHorizontal) {
                const newWidth = Math.max(250, Math.min(500, e.clientX));
                mainContainer.style.gridTemplateColumns = `${newWidth}px 8px 1fr`;
            }
            if (isResizingVertical) {
                const rect = mainContainer.getBoundingClientRect();
                const newHeight = Math.max(200, Math.min(rect.height - 250, e.clientY - rect.top));
                mainContainer.style.gridTemplateRows = `${newHeight}px 8px 1fr`;
            }
        });

        document.addEventListener('mouseup', () => {
            isResizingHorizontal = false;
            isResizingVertical = false;
            document.body.style.cursor = 'default';
        });
    }

    // 이벤트 리스너 설정
    setupEventListeners() {
        // CLI 입력
        const cliInput = document.getElementById('cliInput');
        cliInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.executeCommand();
            }
        });

        // 네트워크 탭 클릭
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('network-tab')) {
                this.switchNetwork(e.target.dataset.network);
            }
        });
    }

    // 드래그 앤 드롭 이벤트
    handleDragOver(e) {
        e.preventDefault();
        e.currentTarget.classList.add('available');
    }

    handleDrop(e, slotIndex) {
        e.preventDefault();
        e.currentTarget.classList.remove('available');
        
        if (this.draggedContainer && !this.gridSlots[slotIndex].occupied) {
            this.moveContainerToSlot(this.draggedContainer, slotIndex);
        }
        this.draggedContainer = null;
    }

    // 컨테이너를 그리드 슬롯으로 이동
    moveContainerToSlot(containerId, slotIndex) {
        const container = this.containers.get(containerId);
        if (!container) return;

        // 이전 슬롯 정리
        if (container.slotIndex !== null) {
            this.gridSlots[container.slotIndex].occupied = false;
            this.gridSlots[container.slotIndex].container = null;
            this.gridSlots[container.slotIndex].element.classList.remove('occupied');
            this.gridSlots[container.slotIndex].element.innerHTML = `<small>슬롯 ${container.slotIndex + 1}</small>`;
        }

        // 새 슬롯에 배치
        container.slotIndex = slotIndex;
        this.gridSlots[slotIndex].occupied = true;
        this.gridSlots[slotIndex].container = containerId;
        this.gridSlots[slotIndex].element.classList.add('occupied');

        this.renderContainer(containerId, slotIndex);
    }

    // 컨테이너 렌더링
    renderContainer(containerId, slotIndex) {
        const container = this.containers.get(containerId);
        const slot = this.gridSlots[slotIndex].element;

        slot.innerHTML = `
            <div class="container-visual ${container.status}" 
                 draggable="true" 
                 data-container-id="${containerId}"
                 ondragstart="simulator.startDrag(event, '${containerId}')"
                 ondblclick="simulator.showContainerDetails('${containerId}')">
                <div class="container-name">${container.name}</div>
                <div class="container-status">${container.status}</div>
                ${container.ports ? `<div class="container-ports">${container.ports}</div>` : ''}
            </div>
        `;
    }

    // 드래그 시작
    startDrag(event, containerId) {
        this.draggedContainer = containerId;
        event.target.classList.add('dragging');
        
        // 드래그 종료 시 스타일 정리
        setTimeout(() => {
            if (event.target) {
                event.target.classList.remove('dragging');
            }
        }, 100);
    }

    // 명령어 실행
    executeCommand(command = null) {
        const input = document.getElementById('cliInput');
        const cmd = command || input.value.trim();
        
        if (!cmd) return;

        this.addToTerminal(`<span class="prompt">$ ${cmd}</span>`);
        
        // 명령어 파싱 및 실행
        const [mainCmd, ...args] = cmd.split(' ');
        
        switch (mainCmd) {
            case 'docker':
                this.handleDockerCommand(args);
                break;
            default:
                this.addToTerminal(`<span class="error">명령어를 찾을 수 없습니다: ${mainCmd}</span>`);
        }

        if (!command) {
            input.value = '';
        }
    }

    // Docker 명령어 처리
    handleDockerCommand(args) {
        if (args.length === 0) {
            this.addToTerminal('<span class="error">Docker 명령어를 입력해주세요</span>');
            return;
        }

        const [subCmd, ...params] = args;

        switch (subCmd) {
            case 'run':
                this.dockerRun(params);
                break;
            case 'ps':
                this.dockerPs(params);
                break;
            case 'images':
                this.dockerImages();
                break;
            case 'pull':
                this.dockerPull(params);
                break;
            case 'stop':
            case 'start':
            case 'restart':
                this.dockerContainerControl(subCmd, params);
                break;
            case 'rm':
                this.dockerRm(params);
                break;
            case 'rmi':
                this.dockerRmi(params);
                break;
            case 'volume':
                this.dockerVolume(params);
                break;
            case 'network':
                this.dockerNetwork(params);
                break;
            default:
                this.addToTerminal(`<span class="error">알 수 없는 Docker 명령어: ${subCmd}</span>`);
        }
    }

    // Docker run 명령어
    dockerRun(params) {
        if (params.length === 0) {
            this.addToTerminal('<span class="error">이미지 이름을 입력해주세요</span>');
            return;
        }

        const imageName = params[params.length - 1];
        if (!this.images.has(imageName)) {
            this.addToTerminal(`<span class="error">이미지를 찾을 수 없습니다: ${imageName}</span>`);
            return;
        }

        const containerName = `${imageName.split(':')[0]}_${Date.now()}`;
        const containerId = this.generateId();

        // 포트 매핑 파싱
        let ports = null;
        const portIndex = params.indexOf('-p');
        if (portIndex !== -1 && portIndex + 1 < params.length) {
            ports = params[portIndex + 1];
        }

        const container = {
            id: containerId,
            name: containerName,
            image: imageName,
            status: 'running',
            network: this.currentNetwork,
            ports: ports,
            slotIndex: null
        };

        this.containers.set(containerId, container);

        // 빈 슬롯 찾아서 배치
        const emptySlot = this.gridSlots.findIndex(slot => !slot.occupied);
        if (emptySlot !== -1) {
            this.moveContainerToSlot(containerId, emptySlot);
        }

        this.addToTerminal(`<span class="success">컨테이너 실행됨: ${containerName}</span>`);
        this.updateResourceCounts();
    }

    // Docker ps 명령어
    dockerPs(params) {
        const showAll = params.includes('-a');
        let containers = Array.from(this.containers.values());
        
        if (!showAll) {
            containers = containers.filter(c => c.status === 'running');
        }

        if (containers.length === 0) {
            this.addToTerminal('<span class="text-muted">실행 중인 컨테이너가 없습니다</span>');
            return;
        }

        this.addToTerminal('<span class="success">CONTAINER ID   IMAGE     STATUS    PORTS    NAMES</span>');
        containers.forEach(container => {
            const shortId = container.id.substring(0, 12);
            const ports = container.ports || '';
            this.addToTerminal(`<span class="text-muted">${shortId}   ${container.image}   ${container.status}   ${ports}   ${container.name}</span>`);
        });
    }

    // Docker images 명령어
    dockerImages() {
        if (this.images.size === 0) {
            this.addToTerminal('<span class="text-muted">로컬 이미지가 없습니다</span>');
            return;
        }

        this.addToTerminal('<span class="success">REPOSITORY   TAG   IMAGE ID   CREATED   SIZE</span>');
        this.images.forEach((image, name) => {
            this.addToTerminal(`<span class="text-muted">${name}   latest   ${image.id.substring(0, 12)}   ${image.created}   ${image.size}</span>`);
        });
    }

    // 컨테이너 상세 정보 표시 (더블클릭)
    showContainerDetails(containerId) {
        const container = this.containers.get(containerId);
        if (!container) return;

        const modal = new bootstrap.Modal(document.getElementById('containerModal'));
        const body = document.getElementById('containerModalBody');

        body.innerHTML = `
            <div class="container-info-grid">
                <div class="info-card">
                    <div class="info-label">컨테이너 ID</div>
                    <div class="info-value">${container.id}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">이름</div>
                    <div class="info-value">${container.name}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">이미지</div>
                    <div class="info-value">${container.image}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">상태</div>
                    <div class="info-value">
                        <span class="badge bg-${container.status === 'running' ? 'success' : 'secondary'}">
                            ${container.status}
                        </span>
                    </div>
                </div>
                <div class="info-card">
                    <div class="info-label">네트워크</div>
                    <div class="info-value">${container.network}</div>
                </div>
                <div class="info-card">
                    <div class="info-label">포트</div>
                    <div class="info-value">${container.ports || '없음'}</div>
                </div>
            </div>
            
            <div class="mt-4">
                <h6>컨테이너 관리</h6>
                <div class="btn-group" role="group">
                    <button class="btn btn-success btn-sm" onclick="simulator.dockerContainerControl('start', ['${containerId}'])">
                        <i class="fas fa-play"></i> 시작
                    </button>
                    <button class="btn btn-warning btn-sm" onclick="simulator.dockerContainerControl('stop', ['${containerId}'])">
                        <i class="fas fa-stop"></i> 중지
                    </button>
                    <button class="btn btn-info btn-sm" onclick="simulator.dockerContainerControl('restart', ['${containerId}'])">
                        <i class="fas fa-redo"></i> 재시작
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="simulator.dockerRm(['${containerId}'])">
                        <i class="fas fa-trash"></i> 삭제
                    </button>
                </div>
            </div>
        `;

        modal.show();
    }

    // 리소스 패널 표시 함수들
    showImagesList() {
        const modal = new bootstrap.Modal(document.getElementById('imagesModal'));
        const body = document.getElementById('imagesModalBody');

        if (this.images.size === 0) {
            body.innerHTML = '<p class="text-muted text-center">로컬 이미지가 없습니다</p>';
        } else {
            let html = '<div class="table-responsive"><table class="table table-hover">';
            html += '<thead><tr><th>Repository</th><th>Tag</th><th>Size</th><th>Created</th></tr></thead><tbody>';
            
            this.images.forEach((image, name) => {
                html += `<tr>
                    <td><strong>${name.split(':')[0]}</strong></td>
                    <td><span class="badge bg-primary">${name.split(':')[1] || 'latest'}</span></td>
                    <td>${image.size}</td>
                    <td>${image.created}</td>
                </tr>`;
            });
            
            html += '</tbody></table></div>';
            body.innerHTML = html;
        }

        modal.show();
    }

    showVolumesList() {
        const modal = new bootstrap.Modal(document.getElementById('volumesModal'));
        const body = document.getElementById('volumesModalBody');

        if (this.volumes.size === 0) {
            body.innerHTML = '<p class="text-muted text-center">생성된 볼륨이 없습니다</p>';
        } else {
            let html = '<div class="table-responsive"><table class="table table-hover">';
            html += '<thead><tr><th>Volume Name</th><th>Driver</th><th>Mountpoint</th></tr></thead><tbody>';
            
            this.volumes.forEach((volume, name) => {
                html += `<tr>
                    <td><strong>${name}</strong></td>
                    <td>${volume.driver}</td>
                    <td><small>${volume.mountpoint}</small></td>
                </tr>`;
            });
            
            html += '</tbody></table></div>';
            body.innerHTML = html;
        }

        modal.show();
    }

    showNetworksList() {
        const modal = new bootstrap.Modal(document.getElementById('networksModal'));
        const body = document.getElementById('networksModalBody');

        let html = '<div class="table-responsive"><table class="table table-hover">';
        html += '<thead><tr><th>Network Name</th><th>Driver</th><th>Scope</th></tr></thead><tbody>';
        
        this.networks.forEach((network, name) => {
            html += `<tr>
                <td><strong>${name}</strong></td>
                <td>${network.driver}</td>
                <td>local</td>
            </tr>`;
        });
        
        html += '</tbody></table></div>';
        body.innerHTML = html;

        modal.show();
    }

    // 컨테이너 제어
    dockerContainerControl(action, params) {
        if (params.length === 0) {
            this.addToTerminal('<span class="error">컨테이너 이름 또는 ID를 입력해주세요</span>');
            return;
        }

        const containerRef = params[0];
        const container = this.findContainer(containerRef);
        
        if (!container) {
            this.addToTerminal(`<span class="error">컨테이너를 찾을 수 없습니다: ${containerRef}</span>`);
            return;
        }

        switch (action) {
            case 'start':
                container.status = 'running';
                break;
            case 'stop':
                container.status = 'stopped';
                break;
            case 'restart':
                container.status = 'running';
                break;
        }

        // 화면 업데이트
        if (container.slotIndex !== null) {
            this.renderContainer(container.id, container.slotIndex);
        }

        this.addToTerminal(`<span class="success">컨테이너 ${action}: ${container.name}</span>`);
    }

    // 유틸리티 함수들
    findContainer(ref) {
        for (let container of this.containers.values()) {
            if (container.id.startsWith(ref) || container.name === ref) {
                return container;
            }
        }
        return null;
    }

    generateId() {
        return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
    }

    addToTerminal(text) {
        const output = document.getElementById('cliOutput');
        const div = document.createElement('div');
        div.innerHTML = text;
        output.appendChild(div);
        output.scrollTop = output.scrollHeight;
    }

    clearTerminal() {
        const output = document.getElementById('cliOutput');
        output.innerHTML = `
            <div class="prompt">docker-sim:~$ </div>
            <div class="text-muted">터미널이 초기화되었습니다.</div>
            <div class="prompt">docker-sim:~$ </div>
        `;
    }

    updateResourceCounts() {
        document.getElementById('imageCount').textContent = this.images.size;
        document.getElementById('volumeCount').textContent = this.volumes.size;
        document.getElementById('networkCount').textContent = this.networks.size;
    }

    // 샘플 데이터 로드
    loadSampleData() {
        // 샘플 이미지
        this.images.set('nginx:latest', {
            id: this.generateId(),
            created: '2일 전',
            size: '133MB'
        });
        this.images.set('redis:alpine', {
            id: this.generateId(),
            created: '1주 전',
            size: '32MB'
        });
        this.images.set('mysql:8.0', {
            id: this.generateId(),
            created: '3일 전',
            size: '521MB'
        });

        this.updateResourceCounts();
    }

    // Docker pull 명령어
    dockerPull(params) {
        if (params.length === 0) {
            this.addToTerminal('<span class="error">이미지 이름을 입력해주세요</span>');
            return;
        }

        const imageName = params[0];
        this.addToTerminal(`<span class="text-muted">Pulling ${imageName}...</span>`);
        
        setTimeout(() => {
            this.images.set(imageName, {
                id: this.generateId(),
                created: '방금 전',
                size: '150MB'
            });
            this.addToTerminal(`<span class="success">Successfully pulled ${imageName}</span>`);
            this.updateResourceCounts();
        }, 1000);
    }

    // Docker volume 명령어
    dockerVolume(params) {
        if (params.length === 0) {
            this.addToTerminal('<span class="error">볼륨 명령어를 입력해주세요 (create, ls, rm)</span>');
            return;
        }

        const [subCmd, ...args] = params;

        switch (subCmd) {
            case 'create':
                if (args.length === 0) {
                    this.addToTerminal('<span class="error">볼륨 이름을 입력해주세요</span>');
                    return;
                }
                const volumeName = args[0];
                this.volumes.set(volumeName, {
                    driver: 'local',
                    mountpoint: `/var/lib/docker/volumes/${volumeName}/_data`
                });
                this.addToTerminal(`<span class="success">볼륨 생성됨: ${volumeName}</span>`);
                this.updateResourceCounts();
                break;
            case 'ls':
                if (this.volumes.size === 0) {
                    this.addToTerminal('<span class="text-muted">생성된 볼륨이 없습니다</span>');
                } else {
                    this.addToTerminal('<span class="success">DRIVER   VOLUME NAME</span>');
                    this.volumes.forEach((volume, name) => {
                        this.addToTerminal(`<span class="text-muted">local    ${name}</span>`);
                    });
                }
                break;
            default:
                this.addToTerminal(`<span class="error">알 수 없는 볼륨 명령어: ${subCmd}</span>`);
        }
    }

    // Docker network 명령어
    dockerNetwork(params) {
        if (params.length === 0) {
            this.addToTerminal('<span class="error">네트워크 명령어를 입력해주세요 (create, ls, rm)</span>');
            return;
        }

        const [subCmd, ...args] = params;

        switch (subCmd) {
            case 'create':
                if (args.length === 0) {
                    this.addToTerminal('<span class="error">네트워크 이름을 입력해주세요</span>');
                    return;
                }
                const networkName = args[0];
                this.networks.set(networkName, {
                    driver: 'bridge'
                });
                this.addToTerminal(`<span class="success">네트워크 생성됨: ${networkName}</span>`);
                this.updateResourceCounts();
                break;
            case 'ls':
                this.addToTerminal('<span class="success">NETWORK ID   NAME      DRIVER   SCOPE</span>');
                this.networks.forEach((network, name) => {
                    const id = this.generateId().substring(0, 12);
                    this.addToTerminal(`<span class="text-muted">${id}   ${name}   ${network.driver}   local</span>`);
                });
                break;
            default:
                this.addToTerminal(`<span class="error">알 수 없는 네트워크 명령어: ${subCmd}</span>`);
        }
    }
}

// 전역 인스턴스 생성
const simulator = new DockerSimulatorEnhanced();

// 전역 함수들 (HTML에서 호출)
function executeCommand(command) {
    simulator.executeCommand(command);
}

function clearTerminal() {
    simulator.clearTerminal();
}

function showImagesList() {
    simulator.showImagesList();
}

function showVolumesList() {
    simulator.showVolumesList();
}

function showNetworksList() {
    simulator.showNetworksList();
}

function createNetwork() {
    const modal = new bootstrap.Modal(document.getElementById('networkModal'));
    modal.show();
}

function executeNetworkCreate() {
    const networkName = document.getElementById('networkName').value.trim();
    if (networkName) {
        simulator.executeCommand(`docker network create ${networkName}`);
        bootstrap.Modal.getInstance(document.getElementById('networkModal')).hide();
        document.getElementById('networkName').value = '';
    }
} 