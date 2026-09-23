import { mediaTileRowRenderer } from './MediaTileRowRenderer';
import type { MessageRow, RailRow, MediaTileRow, RowModel } from '../../models';
import { messageRowRenderer } from './MessageRowRenderer';
import type { RowPrimitives } from './RowVisual';
import { railRowRenderer } from './RailRowRenderer';

type Renderer<R extends RowModel> = {
  key: string;
  create: (
    document: Document,
    row: R,
    primitives: RowPrimitives
  ) => HTMLElement;
  bind: (body: HTMLElement, row: R, primitives: RowPrimitives) => void;
  measure: (row: R, width: number) => number;
  measureRendered: (body: HTMLElement, row: R) => number | undefined;
  recycle: (body: HTMLElement) => void;
};
function registration<R extends RowModel>(renderer: Renderer<R>, row: R) {
  return {
    key: renderer.key,
    create: (document: Document, primitives: RowPrimitives) =>
      renderer.create(document, row, primitives),
    bind: (body: HTMLElement, primitives: RowPrimitives) =>
      renderer.bind(body, row, primitives),
    measure: (width: number) => renderer.measure(row, width),
    measureRendered: (body: HTMLElement) => renderer.measureRendered(body, row),
  };
}
const factories = {
  message: (row: RowModel) =>
    registration(messageRowRenderer, row as MessageRow),
  rail: (row: RowModel) => registration(railRowRenderer, row as RailRow),
  mediaTile: (row: RowModel) =>
    registration(mediaTileRowRenderer, row as MediaTileRow),
};
const renderers = {
  message: messageRowRenderer,
  rail: railRowRenderer,
  mediaTile: mediaTileRowRenderer,
};
export type RowRendererKey = 'legacy' | keyof typeof factories;
const isRegistered = (key: string): key is keyof typeof factories =>
  Object.prototype.hasOwnProperty.call(factories, key);

// Closed registration; a row's data key, styles and placement never affect reuse.
export function rowRenderer(row: RowModel) {
  return isRegistered(row.type) ? factories[row.type](row) : undefined;
}
export function rowRendererKey(row: RowModel): RowRendererKey {
  return isRegistered(row.type) ? row.type : 'legacy';
}
export function recycleRowBody(body: HTMLElement): boolean {
  const key = body.dataset.nlRenderer ?? '';
  if (!isRegistered(key)) return false;
  renderers[key].recycle(body);
  return true;
}
