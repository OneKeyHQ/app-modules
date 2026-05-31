// Format download timing / average speed for the demo pipeline pages.
// bytes: total downloaded size (from Content-Length); elapsedMs: download wall time.
export function formatDownloadStats(bytes: number, elapsedMs: number): string {
  const secs = elapsedMs / 1000;
  const mb = bytes / (1024 * 1024);
  const speed = secs > 0 && bytes > 0 ? mb / secs : 0;
  const sizeStr = bytes > 0 ? `${mb.toFixed(1)} MB` : 'size n/a';
  const speedStr = speed > 0 ? ` · ${speed.toFixed(1)} MB/s avg` : '';
  return `${sizeStr} in ${secs.toFixed(1)}s${speedStr}`;
}

// Best-effort Content-Length via a HEAD request (used only for the speed readout;
// never gates the pipeline).
export async function fetchContentLength(url: string): Promise<number> {
  try {
    const resp = await fetch(url, { method: 'HEAD' });
    return Number(resp.headers.get('content-length')) || 0;
  } catch {
    return 0;
  }
}
