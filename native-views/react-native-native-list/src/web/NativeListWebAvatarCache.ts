// OneKey patch: avatar bytes stay in a worker and IndexedDB, outside list snapshots.
const AVATAR_PREFIX = 'onekey-avatar://blockie/v1/';
const MAX_RETAINED_AVATARS = 128;
export type AvatarLease = (() => void) & {
  setPriority: (priority: number) => void;
};

type AvatarEntry = {
  id: number;
  uri: string;
  url?: string;
  references: number;
  listeners: Set<{
    resolve: (url: string) => void;
    reject: () => void;
    priority: number;
  }>;
};

type AvatarResponse =
  | { type: 'resolved'; id: number; url: string }
  | { type: 'error'; id: number };

export function canonicalNativeListAvatarUri(uri: string): string | undefined {
  if (!uri.startsWith(AVATAR_PREFIX)) return undefined;
  try {
    const seed = decodeURIComponent(uri.slice(AVATAR_PREFIX.length));
    return seed
      ? AVATAR_PREFIX + encodeURIComponent(seed.toLowerCase())
      : undefined;
  } catch {
    return undefined;
  }
}

class NativeListWebAvatarCache {
  private readonly entries = new Map<string, AvatarEntry>();
  private readonly requests = new Map<number, AvatarEntry>();
  private nextId = 0;
  private worker: Worker | undefined;
  // OneKey patch: one cancellable startup serves every pending avatar lease.
  private workerGeneration = 0;
  private startingWorker:
    | { generation: number; controller?: AbortController }
    | undefined;

  acquire(
    uri: string,
    resolve: (url: string) => void,
    reject: () => void,
    priority = 2
  ): AvatarLease {
    let entry = this.entries.get(uri);
    const isNew = !entry;
    if (!entry) {
      entry = { id: ++this.nextId, uri, references: 0, listeners: new Set() };
      this.entries.set(uri, entry);
      this.requests.set(entry.id, entry);
    }
    const current = entry;
    this.entries.delete(uri);
    this.entries.set(uri, current);
    current.references += 1;
    const listener = { resolve, reject, priority };
    if (current.url) resolve(current.url);
    // OneKey patch: keep lease priority after resolution as well as while queued.
    // else current.listeners.add(listener);
    current.listeners.add(listener);
    if (isNew) {
      // OneKey patch: file workers need async asset loading; keep pending requests in this cache.
      // try {
      //   if (!this.worker) {
      //     // Deliberately avoid *.worker.js and its inline Blob-worker loader.
      //     this.worker = new Worker(new URL('./NativeListAvatarWorker.js', import.meta.url), {
      //       name: 'onekey-native-list-avatar',
      //     });
      //     this.worker.addEventListener('message', this.handleMessage);
      //     this.worker.addEventListener('error', this.handleFailure);
      //     this.worker.addEventListener('messageerror', this.handleFailure);
      //   }
      //   this.worker.postMessage({ type: 'acquire', id: current.id, uri, priority });
      // } catch {
      //   queueMicrotask(this.handleFailure);
      // }
      if (this.worker) {
        this.sendAcquire(current);
      } else {
        this.startWorker();
      }
    }
    const updatePriority = () => {
      if (current.url) return;
      const priorities = [...current.listeners].map((value) => value.priority);
      this.worker?.postMessage({
        type: 'priority',
        id: current.id,
        priority: Math.min(2, ...priorities),
      });
    };
    updatePriority();
    this.trim();
    let disposed = false;
    const release = () => {
      if (disposed) return;
      disposed = true;
      current.listeners.delete(listener);
      current.references = Math.max(0, current.references - 1);
      if (
        current.references === 0 &&
        !current.url &&
        this.entries.get(uri) === current
      ) {
        this.entries.delete(uri);
        this.requests.delete(current.id);
        this.worker?.postMessage({ type: 'release', id: current.id });
        if (this.requests.size === 0 && this.startingWorker) {
          const starting = this.startingWorker;
          this.startingWorker = undefined;
          this.workerGeneration += 1;
          starting.controller?.abort();
        }
      }
      updatePriority();
      this.trim();
    };
    return Object.assign(release, {
      setPriority: (next: number) => {
        if (disposed || listener.priority === next) return;
        listener.priority = next;
        updatePriority();
      },
    });
  }

  // OneKey patch: HTTP/extension keeps the bundler's original Worker entry. Electron's
  // file interceptor can serve the same self-contained source as an asset for a Blob worker.
  private startWorker() {
    if (this.worker || this.startingWorker) return;
    const starting: NonNullable<NativeListWebAvatarCache['startingWorker']> = {
      generation: ++this.workerGeneration,
    };
    this.startingWorker = starting;
    const assetURL = new URL('./NativeListAvatarWorker.js', import.meta.url);
    if (assetURL.protocol === 'file:') {
      starting.controller = new AbortController();
      void (async () => {
        const response = await fetch(assetURL, {
          signal: starting.controller?.signal,
        });
        if (!response.ok)
          throw new Error('NativeList avatar worker asset failed to load');
        const source = await response.blob();
        if (this.startingWorker !== starting) return;
        const sourceURL = URL.createObjectURL(source);
        try {
          this.activateWorker(
            new Worker(sourceURL, { name: 'onekey-native-list-avatar' }),
            starting
          );
        } finally {
          // Worker construction captures the script URL; the document must not retain its bytes.
          URL.revokeObjectURL(sourceURL);
        }
      })().catch(() => {
        if (
          this.startingWorker === starting ||
          this.workerGeneration === starting.generation
        ) {
          this.handleFailure();
        }
      });
      return;
    }
    try {
      this.activateWorker(
        new Worker(new URL('./NativeListAvatarWorker.js', import.meta.url), {
          name: 'onekey-native-list-avatar',
        }),
        starting
      );
    } catch {
      queueMicrotask(() => {
        if (this.workerGeneration === starting.generation) this.handleFailure();
      });
    }
  }

  private activateWorker(
    worker: Worker,
    starting: NonNullable<NativeListWebAvatarCache['startingWorker']>
  ) {
    if (this.startingWorker !== starting) {
      worker.terminate();
      return;
    }
    this.startingWorker = undefined;
    this.worker = worker;
    // OneKey patch: queued events from a failed instance cannot affect its replacement.
    worker.addEventListener(
      'message',
      (event: MessageEvent<AvatarResponse>) => {
        if (this.worker === worker) this.handleMessage(event);
      }
    );
    const failed = () => {
      if (this.worker === worker) this.handleFailure();
    };
    worker.addEventListener('error', failed);
    worker.addEventListener('messageerror', failed);
    this.requests.forEach((entry) => {
      if (entry.references > 0 && !entry.url) this.sendAcquire(entry);
    });
  }

  private sendAcquire(entry: AvatarEntry) {
    const worker = this.worker;
    try {
      worker?.postMessage({
        type: 'acquire',
        id: entry.id,
        uri: entry.uri,
        priority: Math.min(
          2,
          ...[...entry.listeners].map((listener) => listener.priority)
        ),
      });
    } catch {
      queueMicrotask(() => {
        if (this.worker === worker) this.handleFailure();
      });
    }
  }

  private readonly handleMessage = (event: MessageEvent<AvatarResponse>) => {
    const response = event.data;
    if (!response || !Number.isSafeInteger(response.id)) return;
    const entry = this.requests.get(response.id);
    if (!entry) {
      this.worker?.postMessage({ type: 'release', id: response.id });
      return;
    }
    if (
      response.type === 'resolved' &&
      typeof response.url === 'string' &&
      response.url.startsWith('blob:')
    ) {
      entry.url = response.url;
      entry.listeners.forEach((listener) => listener.resolve(response.url));
    } else {
      this.entries.delete(entry.uri);
      this.requests.delete(entry.id);
      this.worker?.postMessage({ type: 'release', id: entry.id });
      entry.listeners.forEach((listener) => listener.reject());
    }
    // OneKey patch: retain resolved lease priorities until their explicit release.
    // entry.listeners.clear();
    if (!entry.url) entry.listeners.clear();
    this.trim();
  };

  private readonly handleFailure = () => {
    // OneKey patch: detach the failed generation before callbacks can acquire a replacement.
    // this.worker?.terminate();
    // this.worker = undefined;
    // this.requests.forEach((entry) => {
    //   if (!entry.url) entry.listeners.forEach((listener) => listener.reject());
    //   entry.listeners.clear();
    // });
    // this.requests.clear();
    // this.entries.clear();
    const failedEntries = [...this.requests.values()];
    const starting = this.startingWorker;
    this.startingWorker = undefined;
    this.workerGeneration += 1;
    starting?.controller?.abort();
    this.worker?.terminate();
    this.worker = undefined;
    this.requests.clear();
    this.entries.clear();
    failedEntries.forEach((entry) => {
      const listeners = [...entry.listeners];
      entry.listeners.clear();
      if (!entry.url) listeners.forEach((listener) => listener.reject());
    });
  };

  private trim() {
    for (const [uri, entry] of this.entries) {
      if (this.entries.size <= MAX_RETAINED_AVATARS) break;
      if (entry.references === 0) {
        this.entries.delete(uri);
        this.requests.delete(entry.id);
        this.worker?.postMessage({ type: 'release', id: entry.id });
      }
    }
  }
}

const documentCaches = new WeakMap<Document, NativeListWebAvatarCache>();

export function acquireNativeListAvatar(
  document: Document,
  uri: string,
  resolve: (url: string) => void,
  reject: () => void,
  priority = 2
): AvatarLease {
  let cache = documentCaches.get(document);
  if (!cache) {
    cache = new NativeListWebAvatarCache();
    documentCaches.set(document, cache);
  }
  return cache.acquire(uri, resolve, reject, priority);
}
