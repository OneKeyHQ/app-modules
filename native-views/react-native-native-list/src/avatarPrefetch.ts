// OneKey patch: share a bounded URI-only window across renderers. No bitmap or
// account-specific data enters list snapshots or the UI runtime.
import type { ImageSource, LeadingVisual, RowModel, RowPatch } from './models';

export type AvatarCandidate = Readonly<{
  source: ImageSource;
  priority: 0 | 1 | 2;
}>;
const PREFIX = 'onekey-avatar://blockie/v1/';
const MAX_CANDIDATES = 64;
const IMAGE_PATCH_FIELDS = [
  'leading',
  'secondaryLeading',
  'image',
  'networkImage',
  'thumbnail',
  'visual',
] as const;

export function avatarPrefetchWindow(
  rows: readonly RowModel[],
  first: number,
  last: number,
  direction: number,
  resolveRow?: (row: RowModel) => RowModel
): AvatarCandidate[] {
  if (first < 0 || last < first || first >= rows.length) return [];
  last = Math.min(last, rows.length - 1);
  const result: AvatarCandidate[] = [];
  const seen = new Set<string>();
  let limit = MAX_CANDIDATES;
  const add = (source: ImageSource | undefined, priority: 0 | 1 | 2) => {
    if (
      !source?.uri.startsWith(PREFIX) ||
      source.cachePolicy === 'none' ||
      seen.has(source.uri) ||
      result.length >= limit
    )
      return;
    seen.add(source.uri);
    result.push({ source, priority });
  };
  const visual = (value: LeadingVisual | undefined, priority: 0 | 1 | 2) => {
    if (!value) return;
    if ('image' in value) add(value.image, priority);
    if ('networkImage' in value) add(value.networkImage, priority);
    if ('images' in value)
      value.images.forEach((image) => add(image, priority));
    if ('overlays' in value)
      value.overlays?.forEach((overlay) => add(overlay.image, priority));
  };
  const row = (value: RowModel, priority: 0 | 1 | 2) => {
    value = resolveRow?.(value) ?? value;
    if ('leading' in value) visual(value.leading, priority);
    if ('secondaryLeading' in value) visual(value.secondaryLeading, priority);
    if ('image' in value) add(value.image, priority);
    if ('networkImage' in value) add(value.networkImage, priority);
    if ('thumbnail' in value) add(value.thumbnail, priority);
    if (value.type === 'walletGroup') {
      visual(value.parent.leading, priority);
      for (const child of value.children.slice(0, MAX_CANDIDATES)) {
        if (result.length >= limit) break;
        visual(child.leading, priority);
      }
    }
  };
  // Bound row examination too: a giant non-avatar group must not scan all data.
  for (let index = first; index <= last && index < first + 64; index += 1)
    row(rows[index], 0);
  const span = Math.min(24, (last - first + 1) * 2);
  const step = direction < 0 ? -1 : 1;
  const aheadStart = step > 0 ? last + 1 : first - 1;
  const aheadLimit = Math.min(MAX_CANDIDATES, result.length + 24);
  limit = aheadLimit;
  for (
    let distance = 0;
    distance < span && result.length < aheadLimit;
    distance += 1
  ) {
    const index = aheadStart + distance * step;
    if (index < 0 || index >= rows.length) break;
    row(rows[index], 1);
  }
  const behindStart = step > 0 ? first - 1 : last + 1;
  const behindLimit = Math.min(MAX_CANDIDATES, result.length + 8);
  limit = behindLimit;
  for (
    let distance = 0;
    distance < Math.min(8, span) && result.length < behindLimit;
    distance += 1
  ) {
    const index = behindStart - distance * step;
    if (index < 0 || index >= rows.length) break;
    row(rows[index], 2);
  }
  return result;
}

// OneKey patch: imperative image patches update a sparse prefetch overlay, not
// the readonly prop or a full cloned snapshot. The key index is rebuilt only for
// a complete snapshot; ordinary balance patches retain no extra row copies.
export class NativeAvatarPrefetchModel {
  private rows: readonly RowModel[];
  private rowByKey: Map<string, RowModel>;
  private readonly imageRows = new Map<string, RowModel>();

  constructor(rows: readonly RowModel[]) {
    this.rows = rows;
    this.rowByKey = new Map(rows.map((row) => [row.key, row]));
  }

  replaceSnapshot(rows: readonly RowModel[]) {
    this.rows = rows;
    this.rowByKey = new Map(rows.map((row) => [row.key, row]));
    this.imageRows.clear();
  }

  applyPatches(patches: readonly RowPatch[], dispatch?: () => void): boolean {
    // Native methods have no acknowledgement. Mirror only dispatched batches;
    // a missing host or a synchronous serialization/dispatch failure changes nothing.
    if (!dispatch) return false;
    dispatch();
    const seen = new Set<string>();
    for (const patch of patches) {
      const row = this.rowByKey.get(patch.key);
      // Both native renderers reject the whole batch for an unknown key/type.
      if (!row || row.type !== patch.type || seen.has(patch.key)) return false;
      seen.add(patch.key);
    }
    let changed = false;
    for (const patch of patches) {
      const changes: Readonly<Record<string, unknown>> = patch.changes;
      let imageChanges: Record<string, unknown> | undefined;
      for (const key of IMAGE_PATCH_FIELDS) {
        if (changes[key] !== undefined) {
          imageChanges ??= {};
          imageChanges[key] = changes[key];
        }
      }
      // JSON.stringify omits undefined top-level fields. Native therefore keeps
      // them unchanged; an explicit replacement visual without image clears it.
      if (!imageChanges) continue;
      const row =
        this.imageRows.get(patch.key) ?? this.rowByKey.get(patch.key)!;
      // Preserve the validated row discriminant, matching applyRowPatches' shallow merge.
      this.imageRows.set(patch.key, {
        ...row,
        ...imageChanges,
        key: row.key,
        type: row.type,
      } as RowModel);
      changed = true;
    }
    return changed;
  }

  window(first: number, last: number, direction: number): AvatarCandidate[] {
    return avatarPrefetchWindow(
      this.rows,
      first,
      last,
      direction,
      (row) => this.imageRows.get(row.key) ?? row
    );
  }
}

// Native preload has no cancellation token. One in-flight source is the bound;
// direction changes replace all not-yet-started work rather than appending it.
export class NativeAvatarPrefetchQueue {
  private pending: ImageSource[] = [];
  private activeUri: string | undefined;
  private timer: ReturnType<typeof setTimeout> | undefined;
  private disposed = false;
  private readonly recent = new Map<string, number>();

  constructor(
    private readonly preload: (source: ImageSource) => Promise<boolean>
  ) {}

  update(candidates: readonly AvatarCandidate[]) {
    if (this.disposed) return;
    const now = Date.now();
    this.pending = candidates
      .filter(
        ({ source, priority }) =>
          priority !== 0 &&
          source.uri !== this.activeUri &&
          now - (this.recent.get(source.uri) ?? 0) > 30_000
      )
      .map(({ source }) => source);
    this.schedule();
  }

  dispose() {
    this.disposed = true;
    this.pending = [];
    this.recent.clear();
    if (this.timer !== undefined) clearTimeout(this.timer);
    this.timer = undefined;
  }

  private schedule() {
    if (
      this.disposed ||
      this.activeUri ||
      this.timer !== undefined ||
      !this.pending.length
    )
      return;
    // Yield between short batches without imposing a frame-per-image throughput cap.
    this.timer = setTimeout(() => {
      this.timer = undefined;
      const source = this.pending.shift();
      if (!source || this.disposed) return;
      this.activeUri = source.uri;
      Promise.resolve()
        .then(() => this.preload(source))
        .then((success) => {
          if (success && !this.disposed) {
            this.recent.delete(source.uri);
            this.recent.set(source.uri, Date.now());
            if (this.recent.size > 128)
              this.recent.delete(this.recent.keys().next().value as string);
          }
        })
        .catch(() => {
          /* Visible loading retains its own failure/retry behavior. */
        })
        .finally(() => {
          this.activeUri = undefined;
          this.schedule();
        });
    }, 0);
  }
}
