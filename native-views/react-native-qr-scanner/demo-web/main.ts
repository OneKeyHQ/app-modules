import {
  configureWebQrScanner,
  createMultiSymbolCoordinator,
  createScanSession,
  createWebQrScanner,
  decodeQrImageSymbols,
  isUrFrameText,
  mapFramePointToCoverView,
  mapImagePointToContainView,
} from '../src/web/index';

import type {
  MultiSymbolCoordinator,
  QrDetectedSymbol,
  WebQrScanner,
} from '../src/web/index';

type DemoCase = 'a' | 'b' | 'c' | 'd' | 'camera';

const viewportEl = document.getElementById('viewport') as HTMLDivElement;
const videoEl = document.getElementById('video') as HTMLVideoElement;
const imageEl = document.getElementById('stillImage') as HTMLImageElement;
const scanLineEl = document.getElementById('scanLine') as HTMLDivElement;
const overlayEl = document.getElementById('selectionOverlay') as HTMLDivElement;
const backdropEl = document.getElementById('dimBackdrop') as HTMLDivElement;
const anchorsEl = document.getElementById('anchorsLayer') as HTMLDivElement;
const progressWrapEl = document.getElementById('urProgressWrap') as HTMLDivElement;
const progressBarEl = document.getElementById('urProgressBar') as HTMLDivElement;
const progressTextEl = document.getElementById('urProgressText') as HTMLSpanElement;
const statusEl = document.getElementById('statusBadge') as HTMLSpanElement;
const footerTitleEl = document.getElementById('footerTitle') as HTMLDivElement;
const footerHintEl = document.getElementById('footerHint') as HTMLDivElement;
const resumeBtn = document.getElementById('resumeBtn') as HTMLButtonElement;
const resultBoxEl = document.getElementById('resultBox') as HTMLPreElement;
const logEl = document.getElementById('eventLog') as HTMLDivElement;

let activeCase: DemoCase = 'b';
let scanner: WebQrScanner | null = null;
let coordinator: MultiSymbolCoordinator | null = null;
let urSession = createScanSession({ mode: 'auto' });
let isStillImageMode = false;

function log(message: string) {
  const time = new Date().toLocaleTimeString('en-GB', { hour12: false });
  const line = document.createElement('div');
  line.textContent = `[${time}] ${message}`;
  logEl.prepend(line);
  while (logEl.children.length > 30) {
    logEl.removeChild(logEl.lastChild!);
  }
}

function fnv1aHex(bytes: Uint8Array): string {
  let hash = 0x811c9dc5;
  for (const byte of bytes) {
    hash = Math.imul(hash ^ byte, 0x01000193) >>> 0;
  }
  return hash.toString(16).padStart(8, '0');
}

function classifyLabel(text: string): string {
  if (isUrFrameText(text)) return 'Hardware UR';
  if (/^wc:/i.test(text)) return 'WalletConnect';
  if (/^ethereum:/i.test(text)) return 'ETH Address';
  if (/^onekey:/i.test(text)) return 'OneKey Link';
  return 'QR Code';
}

function setStatus(label: string, tone: 'idle' | 'select' | 'ur' | 'done' = 'idle') {
  statusEl.textContent = label;
  const tones: Record<typeof tone, string> = {
    idle: 'bg-zinc-800 text-zinc-200 border-zinc-700',
    select: 'bg-emerald-950/80 text-emerald-300 border-emerald-700/60',
    ur: 'bg-sky-950/80 text-sky-300 border-sky-700/60',
    done: 'bg-emerald-600 text-white border-emerald-500',
  };
  statusEl.className = `px-2.5 py-1 rounded-full text-xs font-medium border ${tones[tone]}`;
}

function setUrProgress(progress: number | null) {
  if (progress === null) {
    progressWrapEl.classList.add('hidden');
    return;
  }
  progressWrapEl.classList.remove('hidden');
  const pct = Math.min(100, Math.round(progress * 100));
  progressBarEl.style.width = `${pct}%`;
  progressTextEl.textContent = `BC-UR 组装中 ${pct}%`;
}

function setFooter(mode: 'scanning' | 'selecting' | 'ur' | 'resolved') {
  if (mode === 'selecting') {
    footerTitleEl.textContent = '轻触绿点，选择要识别的二维码';
    footerHintEl.textContent = '桌面端悬停可放大查看类型 · 点击空白处或按 Esc 恢复';
    resumeBtn.classList.remove('hidden');
    resumeBtn.textContent = isStillImageMode ? '重新识别图片' : '重新扫描 (Esc)';
  } else if (mode === 'ur') {
    footerTitleEl.textContent = '已自动锁态至 BC-UR 高速单码通道';
    footerHintEl.textContent = 'multiSymbol=false（maxNumberOfSymbols=1），忽略旁路静态码';
    resumeBtn.classList.remove('hidden');
    resumeBtn.textContent = '重置会话';
  } else if (mode === 'resolved') {
    footerTitleEl.textContent = '识别完成';
    footerHintEl.textContent = '点击下方按钮可再次运行当前测试用例';
    resumeBtn.classList.remove('hidden');
    resumeBtn.textContent = '再测一次';
  } else {
    footerTitleEl.textContent = '正在扫描画面…';
    footerHintEl.textContent = '支持静态单码、多码定格选点与动态 BC-UR 自动识别';
    resumeBtn.classList.add('hidden');
  }
}

/** Light pairwise repulsion so adjacent dots stay clickable (>= 44px apart). */
function resolveAnchorPositions(
  symbols: QrDetectedSymbol[],
  viewWidth: number,
  viewHeight: number
) {
  const margin = 28;
  const minDist = 44;
  const pts = symbols.map((sym) => ({
    sym,
    x: Math.max(margin, Math.min(viewWidth - margin, sym.centerX * viewWidth)),
    y: Math.max(margin, Math.min(viewHeight - margin, sym.centerY * viewHeight)),
  }));
  for (let iter = 0; iter < 3; iter += 1) {
    for (let i = 0; i < pts.length; i += 1) {
      for (let j = i + 1; j < pts.length; j += 1) {
        const dx = pts[j]!.x - pts[i]!.x;
        const dy = pts[j]!.y - pts[i]!.y;
        const d = Math.hypot(dx, dy) || 0.01;
        if (d < minDist) {
          const push = (minDist - d) / 2;
          const ux = dx / d;
          const uy = dy / d;
          pts[i]!.x = Math.max(margin, Math.min(viewWidth - margin, pts[i]!.x - ux * push));
          pts[i]!.y = Math.max(margin, Math.min(viewHeight - margin, pts[i]!.y - uy * push));
          pts[j]!.x = Math.max(margin, Math.min(viewWidth - margin, pts[j]!.x + ux * push));
          pts[j]!.y = Math.max(margin, Math.min(viewHeight - margin, pts[j]!.y + uy * push));
        }
      }
    }
  }
  return pts;
}

function renderAnchors(symbols: QrDetectedSymbol[]) {
  anchorsEl.innerHTML = '';
  if (symbols.length === 0) {
    overlayEl.classList.add('hidden');
    return;
  }
  overlayEl.classList.remove('hidden');
  scanLineEl.classList.add('hidden');
  const rect = viewportEl.getBoundingClientRect();
  const placed = resolveAnchorPositions(symbols, rect.width || 360, rect.height || 360);

  placed.forEach(({ sym, x, y }, idx) => {
    const btn = document.createElement('button');
    btn.className = 'qr-dot group absolute pointer-events-auto cursor-pointer focus:outline-none';
    btn.style.left = `${x}px`;
    btn.style.top = `${y}px`;
    btn.style.width = '44px';
    btn.style.height = '44px';
    btn.style.animationDelay = `${idx * 45}ms`;
    btn.title = `${classifyLabel(sym.text)}: ${sym.text}`;
    btn.innerHTML = `
      <span class="pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity absolute -top-7 left-1/2 -translate-x-1/2 whitespace-nowrap px-2 py-0.5 rounded-md bg-black/85 text-white text-[11px] border border-white/15 shadow-lg">
        ${classifyLabel(sym.text)}
      </span>
      <span class="relative w-full h-full flex items-center justify-center">
        <span class="qr-dot-ring absolute w-9 h-9 rounded-full bg-[#00B812]/50"></span>
        <span class="qr-dot-core relative w-9 h-9 rounded-full bg-[#00B812] border-2 border-white shadow-[0_3px_12px_rgba(0,0,0,0.45)] flex items-center justify-center transition-transform duration-150">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14"/>
            <path d="M13 6l6 6-6 6"/>
          </svg>
        </span>
      </span>
    `;
    btn.addEventListener('click', (event) => {
      event.stopPropagation();
      log(`点击圆点: [${classifyLabel(sym.text)}] ${sym.text.slice(0, 48)}`);
      coordinator?.selectSymbol(sym);
    });
    anchorsEl.appendChild(btn);
  });
}

function teardown() {
  coordinator?.dispose();
  coordinator = null;
  scanner?.dispose();
  scanner = null;
  videoEl.pause();
  videoEl.removeAttribute('src');
  videoEl.srcObject = null;
  videoEl.load();
  videoEl.classList.add('hidden');
  imageEl.classList.add('hidden');
  imageEl.removeAttribute('src');
  overlayEl.classList.add('hidden');
  anchorsEl.innerHTML = '';
  scanLineEl.classList.add('hidden');
  setUrProgress(null);
  isStillImageMode = false;
}

function createCoordinator() {
  urSession = createScanSession({ mode: 'auto' });
  return createMultiSymbolCoordinator({
    aggregationWindowMs: 140,
    onFrozenChange(frozen) {
      scanner?.setFrozen(frozen);
      if (frozen) {
        scanLineEl.classList.add('hidden');
      } else if (!isStillImageMode) {
        scanLineEl.classList.remove('hidden');
      }
      log(`setFrozen(${frozen})`);
    },
    onMultiSymbolChange(enabled) {
      scanner?.setMultiSymbol(enabled);
      log(`setMultiSymbol(${enabled})`);
    },
    onSelectingChange(symbols) {
      renderAnchors(symbols);
      if (symbols.length > 0) {
        setStatus(`多码定格 (${symbols.length} 个)`, 'select');
        setFooter('selecting');
      }
    },
    onCommitText(text, mode) {
      if (mode === 'static') {
        scanner?.setFrozen(true);
        scanLineEl.classList.add('hidden');
        setStatus('静态码完成', 'done');
        setFooter('resolved');
        resultBoxEl.textContent = JSON.stringify(
          { kind: 'text', label: classifyLabel(text), text },
          null,
          2
        );
        log(`提交静态码: ${text.slice(0, 56)}`);
        return;
      }

      setStatus('BC-UR 高速组装中', 'ur');
      setFooter('ur');
      const outcome = urSession.receive(text);
      if (outcome.status === 'progress') {
        setUrProgress(outcome.progress);
        log(
          `UR 进度 ${Math.round(outcome.progress * 100)}% (${outcome.receivedParts}/${outcome.expectedParts})`
        );
      } else if (outcome.status === 'complete') {
        setUrProgress(1);
        scanner?.setFrozen(true);
        scanLineEl.classList.add('hidden');
        setStatus('BC-UR 组装完成', 'done');
        setFooter('resolved');
        if (outcome.result.kind === 'ur') {
          const checksum = fnv1aHex(outcome.result.cbor);
          resultBoxEl.textContent = JSON.stringify(
            {
              kind: 'ur',
              type: outcome.result.type,
              bytes: outcome.result.cbor.length,
              fnv1a: checksum,
              expectedFnv1a: '07a5e3d7',
              checksumMatch: checksum === '07a5e3d7',
            },
            null,
            2
          );
          log(`UR 完成: ${outcome.result.type} (${outcome.result.cbor.length} B, fnv1a=${checksum})`);
        }
      } else if (outcome.status === 'rejected') {
        log(`UR 拒绝: ${outcome.error.code} - ${outcome.error.message}`);
      }
    },
  });
}

async function runImageCase(fixturePath: string, title: string) {
  teardown();
  isStillImageMode = true;
  setStatus(`解码图片中…`, 'idle');
  setFooter('scanning');
  resultBoxEl.textContent = `加载 ${title}…`;

  const response = await fetch(fixturePath);
  const blob = await response.blob();
  const objectUrl = URL.createObjectURL(blob);
  imageEl.src = objectUrl;
  imageEl.classList.remove('hidden');

  const decoded = await decodeQrImageSymbols(blob);
  log(`decodeQrImageSymbols(${title}): 识别到 ${decoded.symbols.length} 个二维码 (${decoded.width}x${decoded.height})`);

  const rect = viewportEl.getBoundingClientRect();
  const viewSize = { width: rect.width || 360, height: rect.height || 360 };
  const mapped: QrDetectedSymbol[] = decoded.symbols.map((sym) => {
    const pt = mapImagePointToContainView(
      { x: sym.centerX, y: sym.centerY },
      { width: decoded.width, height: decoded.height },
      viewSize
    );
    return { ...sym, centerX: pt.x, centerY: pt.y };
  });

  coordinator = createCoordinator();
  if (mapped.length === 0) {
    setStatus('未识别到二维码', 'idle');
    resultBoxEl.textContent = 'null';
    return;
  }
  if (mapped.length === 1) {
    coordinator.receiveFrame(mapped);
    return;
  }
  coordinator.receiveFrame(mapped);
}

async function runVideoCase(fixturePath: string | null, title: string) {
  teardown();
  setStatus(fixturePath ? `播放测试视频: ${title}` : '打开摄像头…', 'idle');
  setFooter('scanning');
  resultBoxEl.textContent = '等待识别结果…';

  videoEl.classList.remove('hidden');
  scanLineEl.classList.remove('hidden');
  coordinator = createCoordinator();

  if (fixturePath) {
    videoEl.src = fixturePath;
    videoEl.loop = true;
    videoEl.muted = true;
    await videoEl.play();
  }

  scanner = createWebQrScanner({
    video: videoEl,
    source: fixturePath ? { kind: 'element' } : { kind: 'camera' },
    mode: 'static',
    multiSymbol: true,
    onReady: () => {
      log(`scanner ready (${title})`);
    },
    onFrameSymbols: (event) => {
      const rect = viewportEl.getBoundingClientRect();
      const viewSize = { width: rect.width || 360, height: rect.height || 360 };
      const frameSize = {
        width: event.frameWidth ?? (videoEl.videoWidth || viewSize.width),
        height: event.frameHeight ?? (videoEl.videoHeight || viewSize.height),
      };
      const mapped = event.symbols.map((sym) => {
        const pt = mapFramePointToCoverView(
          { x: sym.centerX, y: sym.centerY },
          frameSize,
          viewSize,
          false
        );
        return { ...sym, centerX: pt.x, centerY: pt.y };
      });
      coordinator?.receiveFrame(mapped);
    },
    onError: (err) => {
      log(`scanner error: ${err.code} - ${err.message}`);
      setStatus(`错误: ${err.code}`, 'idle');
    },
  });
  await scanner.start();
}

function updateTabStyles() {
  document.querySelectorAll<HTMLButtonElement>('[data-case]').forEach((btn) => {
    const active = btn.dataset.case === activeCase;
    btn.className = active
      ? 'px-3 py-2 rounded-lg text-xs font-medium bg-[#00B812] text-white cursor-pointer transition'
      : 'px-3 py-2 rounded-lg text-xs font-medium bg-zinc-900 text-zinc-300 border border-zinc-800 hover:border-zinc-600 cursor-pointer transition';
  });
}

async function startCase(next: DemoCase) {
  activeCase = next;
  updateTabStyles();
  if (next === 'a') {
    await runImageCase('/fixtures/a-single-static.png', 'a. 单码图片');
  } else if (next === 'b') {
    await runImageCase('/fixtures/b-multi-static.png', 'b. 多码图片');
  } else if (next === 'c') {
    await runVideoCase('/fixtures/c-animated-ur.mp4', 'c. 动态码视频');
  } else if (next === 'd') {
    await runVideoCase('/fixtures/d-animated-ur-plus-multi.mp4', 'd. 动态码 + 多码混合视频');
  } else {
    await runVideoCase(null, 'e. 真实摄像头');
  }
}

// Configure local bundled zxing-wasm reader so Chrome/Safari/Firefox all work offline
configureWebQrScanner({
  loadZXingWasm: async () => {
    const res = await fetch('/wasm/zxing_reader.wasm');
    if (!res.ok) throw new Error(`Failed to load zxing_reader.wasm (${res.status})`);
    return res.arrayBuffer();
  },
});

document.querySelectorAll<HTMLButtonElement>('[data-case]').forEach((btn) => {
  btn.addEventListener('click', () => {
    void startCase(btn.dataset.case as DemoCase);
  });
});

const handleResume = () => {
  if (coordinator?.phase === 'selecting' && !isStillImageMode) {
    log('取消选码 → 恢复扫描');
    coordinator.cancelSelection();
    setStatus('正在扫描', 'idle');
    setFooter('scanning');
    return;
  }
  void startCase(activeCase);
};

backdropEl.addEventListener('click', handleResume);
resumeBtn.addEventListener('click', handleResume);
window.addEventListener('keydown', (event) => {
  if (event.key === 'Escape') {
    handleResume();
  }
});

// Start on case b (multi-QR image) by default
void startCase('b');
