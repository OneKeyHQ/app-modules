import type { RowModel } from '../../models';
import { messageRowRenderer } from './MessageRowRenderer';

// Closed registration: only Message is migrated. Legacy templates keep their
// existing binders, updates and default geometry until their own migration.
export function rowRenderer(row: RowModel) {
  return row.type === 'message'
    ? { renderer: messageRowRenderer, row }
    : undefined;
}
export type RowRendererKey = 'legacy' | typeof messageRowRenderer.key;
export function rowRendererKey(row: RowModel): RowRendererKey {
  return rowRenderer(row)?.renderer.key ?? 'legacy';
}

export function recycleRowBody(body: HTMLElement): boolean {
  if (body.dataset.nlRenderer !== 'message') return false;
  messageRowRenderer.recycle(body);
  return true;
}
