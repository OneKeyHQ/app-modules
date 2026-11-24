import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';

const HEX64_RE = /^[a-f0-9]{64}$/u;
const ALLOWED_APP_TYPES = new Set(['electron', 'ios', 'android']);

/**
 * Validate a release artifact basename against the shared rule supported by
 * OneKey's downstream Desktop/iOS/Android SHA256SUMS verifiers (no path
 * separators, no traversal, no whitespace, no control characters).
 * @param {string} fileName
 */
export function assertSafeReleaseFileName(fileName) {
  if (
    typeof fileName !== 'string' ||
    !fileName ||
    fileName === '.' ||
    fileName === '..' ||
    fileName.includes('..') ||
    fileName.includes('/') ||
    fileName.includes('\\') ||
    /\s/u.test(fileName) ||
    /[\x00-\x1f\x7f]/u.test(fileName)
  ) {
    throw new Error(
      `Unsupported release artifact filename "${String(fileName)}": filenames must be single-segment basenames without spaces, path separators, or control characters`,
    );
  }
}

/**
 * Compute lowercase hex SHA-256 digest of a file using streaming reads.
 * @param {string} filePath
 * @returns {string}
 */
export function sha256File(filePath) {
  const stat = fs.statSync(filePath);
  if (!stat.isFile()) {
    throw new Error(`Artifact path is not a regular file: ${filePath}`);
  }
  if (stat.size === 0) {
    throw new Error(`Artifact file is empty (0 bytes): ${filePath}`);
  }
  const hash = crypto.createHash('sha256');
  const fd = fs.openSync(filePath, 'r');
  try {
    const chunk = Buffer.allocUnsafe(64 * 1024);
    let bytesRead = 0;
    do {
      bytesRead = fs.readSync(fd, chunk, 0, chunk.length, null);
      if (bytesRead > 0) {
        hash.update(chunk.subarray(0, bytesRead));
      }
    } while (bytesRead > 0);
  } finally {
    fs.closeSync(fd);
  }
  return hash.digest('hex');
}

/**
 * Generate a canonical SHA256SUMS payload for one or more full-release artifacts.
 * @param {string[]} artifactPaths
 * @returns {{ content: string, entries: Array<{ fileName: string, sha256: string, path: string }> }}
 */
export function createSha256SumsPayload(artifactPaths) {
  if (!Array.isArray(artifactPaths) || artifactPaths.length === 0) {
    throw new Error('At least one artifact path is required');
  }
  const entries = [];
  const seenNames = new Set();
  for (const artifactPath of artifactPaths) {
    const fileName = path.basename(artifactPath);
    assertSafeReleaseFileName(fileName);
    if (seenNames.has(fileName)) {
      throw new Error(`Duplicate artifact filename in checksum list: ${fileName}`);
    }
    seenNames.add(fileName);
    const sha256 = sha256File(artifactPath);
    entries.push({ fileName, sha256, path: artifactPath });
  }
  const content = `${entries.map((e) => `${e.sha256}  ${e.fileName}`).join('\n')}\n`;
  return { content, entries };
}

function normalizeArtifactPaths({ artifactPath, artifactPaths } = {}) {
  const paths = [];
  if (Array.isArray(artifactPaths)) {
    paths.push(...artifactPaths);
  }
  if (artifactPath) {
    paths.push(artifactPath);
  }
  return paths;
}

/**
 * Parse and validate an unsigned OneKey release payload (`metadata.json.info`
 * JSON or `SHA256SUMS` text) and verify every supplied local artifact path.
 *
 * @param {string} plainBody
 * @param {{ artifactPath?: string, artifactPaths?: string[] }} [options]
 */
export function inspectReleasePayload(plainBody, options = {}) {
  const trimmed = plainBody.trim();
  if (!trimmed) {
    throw new Error('Release payload body is empty');
  }

  const allArtifactPaths = normalizeArtifactPaths(options);

  if (trimmed.startsWith('{')) {
    let parsed;
    try {
      parsed = JSON.parse(trimmed);
    } catch (error) {
      throw new Error(`Payload starts with '{' but is not valid JSON: ${error.message}`);
    }

    if (typeof parsed !== 'object' || parsed === null || Array.isArray(parsed)) {
      throw new Error('metadata.json.info payload must be a JSON object');
    }
    if (parsed.fileName !== 'metadata.json') {
      throw new Error(
        `Invalid metadata.json.info fileName: expected "metadata.json", got "${String(parsed.fileName)}"`,
      );
    }
    const sha256 = typeof parsed.sha256 === 'string' ? parsed.sha256.toLowerCase() : '';
    if (!HEX64_RE.test(sha256)) {
      throw new Error('Invalid or missing 64-char hex sha256 in metadata.json.info');
    }
    if (typeof parsed.appType !== 'string' || !ALLOWED_APP_TYPES.has(parsed.appType)) {
      throw new Error(
        `Invalid appType in metadata.json.info: expected electron|ios|android, got "${String(parsed.appType)}"`,
      );
    }
    if (typeof parsed.appVersion !== 'string' || !parsed.appVersion.trim()) {
      throw new Error('Missing appVersion in metadata.json.info');
    }
    if (typeof parsed.bundleVersion !== 'string' || !parsed.bundleVersion.trim()) {
      throw new Error('Missing bundleVersion in metadata.json.info');
    }

    if (allArtifactPaths.length > 1) {
      throw new Error(
        `metadata.json.info binds a single metadata.json artifact, but ${allArtifactPaths.length} --artifact flags were provided`,
      );
    }

    const artifactChecks = [];
    if (allArtifactPaths.length === 1) {
      const targetPath = allArtifactPaths[0];
      const actualSha256 = sha256File(targetPath);
      if (actualSha256 !== sha256) {
        throw new Error(
          `Artifact SHA-256 mismatch for ${targetPath}: expected ${sha256}, actual ${actualSha256}`,
        );
      }
      artifactChecks.push({
        path: targetPath,
        fileName: path.basename(targetPath),
        sha256: actualSha256,
        matched: true,
      });
    }

    return {
      kind: 'metadata-info',
      fileName: parsed.fileName,
      sha256,
      appVersion: parsed.appVersion,
      bundleVersion: parsed.bundleVersion,
      buildNumber: parsed.buildNumber ?? null,
      appType: parsed.appType,
      size: parsed.size ?? null,
      generatedAt: parsed.generatedAt ?? null,
      artifactCheck: artifactChecks[0] ?? null,
      artifactChecks,
    };
  }

  // Otherwise parse as SHA256SUMS format: "<64-hex>  [*]<filename>"
  const lines = trimmed
    .split(/\r?\n/u)
    .map((line) => line.trim())
    .filter(Boolean);
  const entries = [];
  for (const line of lines) {
    const match = /^([a-fA-F0-9]{64})[ \t]+\*?(.+)$/u.exec(line);
    if (!match) {
      throw new Error(`Invalid SHA256SUMS line (expected "<64-hex-sha256>  <filename>"): ${line}`);
    }
    const digest = match[1].toLowerCase();
    const rawName = match[2];
    assertSafeReleaseFileName(rawName);
    entries.push({ sha256: digest, fileName: rawName });
  }
  if (entries.length === 0) {
    throw new Error('No checksum entries found in SHA256SUMS payload');
  }

  const artifactChecks = [];
  const seenTargets = new Set();
  for (const targetPath of allArtifactPaths) {
    const targetName = path.basename(targetPath);
    assertSafeReleaseFileName(targetName);
    if (seenTargets.has(targetName)) {
      throw new Error(`Duplicate --artifact filename provided: ${targetName}`);
    }
    seenTargets.add(targetName);

    const matching = entries.filter((entry) => entry.fileName === targetName);
    if (matching.length === 0) {
      throw new Error(
        `Artifact "${targetName}" not found in SHA256SUMS entries (${entries.map((e) => e.fileName).join(', ')})`,
      );
    }
    const uniqueHashes = new Set(matching.map((entry) => entry.sha256));
    if (uniqueHashes.size !== 1) {
      throw new Error(`Conflicting SHA-256 entries for "${targetName}" in SHA256SUMS`);
    }
    const expectedSha256 = matching[0].sha256;
    const actualSha256 = sha256File(targetPath);
    if (actualSha256 !== expectedSha256) {
      throw new Error(
        `Artifact SHA-256 mismatch for ${targetName}: expected ${expectedSha256}, actual ${actualSha256}`,
      );
    }
    artifactChecks.push({
      path: targetPath,
      fileName: targetName,
      sha256: actualSha256,
      matched: true,
    });
  }

  return {
    kind: 'sha256sums',
    entries,
    artifactCheck: artifactChecks[0] ?? null,
    artifactChecks,
  };
}
