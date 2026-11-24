import crypto from 'node:crypto';
import fs from 'node:fs';

export const MAX_ARCHIVE_BYTES = 2 * 1024 * 1024 * 1024;

export class ArchiveSizeError extends Error {}

export async function hashArchive(
  filePath: string,
  algorithm: 'sha256' | 'sha512',
  signal?: AbortSignal
): Promise<{ digest: string; size: number }> {
  signal?.throwIfAborted();
  const stat = await fs.promises.stat(filePath);
  signal?.throwIfAborted();
  if (stat.size > MAX_ARCHIVE_BYTES) throw new ArchiveSizeError();
  const hash = crypto.createHash(algorithm);
  const stream = fs.createReadStream(filePath, {
    highWaterMark: 64 * 1024,
    signal,
  });
  let size = 0;
  for await (const chunk of stream) {
    signal?.throwIfAborted();
    size += chunk.length;
    if (size > MAX_ARCHIVE_BYTES) throw new ArchiveSizeError();
    hash.update(chunk);
  }
  signal?.throwIfAborted();
  return { digest: hash.digest('hex'), size };
}
