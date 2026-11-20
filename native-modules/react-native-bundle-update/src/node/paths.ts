import path from 'node:path';

export function assertSafeBundleVersion(
  version: unknown
): asserts version is string {
  if (
    typeof version !== 'string' ||
    !/^[A-Za-z0-9._-]+$/.test(version) ||
    version.includes('..')
  ) {
    throw new Error('Invalid bundle version');
  }
}

export function resolveBundlePath(root: string, name: string): string {
  const resolvedRoot = path.resolve(root);
  const destination = path.resolve(resolvedRoot, name);
  const relative = path.relative(resolvedRoot, destination);
  if (
    !relative ||
    relative === '..' ||
    relative.startsWith(`..${path.sep}`) ||
    path.isAbsolute(relative)
  ) {
    throw new Error('Bundle path is outside its directory');
  }
  return destination;
}
