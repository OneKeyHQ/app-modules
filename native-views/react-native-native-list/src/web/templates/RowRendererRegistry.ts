import type { MetricCardRow } from '../../models';
import { metricCardRowRenderer } from './MetricCardRowRenderer';
import type { DataRow } from '../../models';
import { dataRowRenderer } from './DataRowRenderer';
import type { ActivityRow } from '../../models';
import { activityRowRenderer } from './ActivityRowRenderer';
import type { SystemRow } from '../../models';
import { systemRowRenderer } from './SystemRowRenderer';
import type { ActionRow } from '../../models';
import { actionRowRenderer } from './ActionRowRenderer';
import { mediaTileRowRenderer } from './MediaTileRowRenderer';
import type { MessageRow, RailRow, MediaTileRow, RowModel } from '../../models';
import { messageRowRenderer } from './MessageRowRenderer';
import type { RowPrimitives } from './RowVisual';
import { railRowRenderer } from './RailRowRenderer';

type Renderer<R extends RowModel> = {
  key: string;
  appliesSizePreset?: (row: R) => boolean;
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
    appliesSizePreset: renderer.appliesSizePreset?.(row) ?? true,
    create: (document: Document, primitives: RowPrimitives) =>
      renderer.create(document, row, primitives),
    bind: (body: HTMLElement, primitives: RowPrimitives) =>
      renderer.bind(body, row, primitives),
    measure: (width: number) => renderer.measure(row, width),
    measureRendered: (body: HTMLElement) => renderer.measureRendered(body, row),
  };
}
const factories = {
  metricCard: (row: RowModel) =>
    registration(metricCardRowRenderer, row as MetricCardRow),
  dataRow: (row: RowModel) => registration(dataRowRenderer, row as DataRow),
  activity: (row: RowModel) =>
    registration(activityRowRenderer, row as ActivityRow),
  system: (row: RowModel) => registration(systemRowRenderer, row as SystemRow),
  action: (row: RowModel) => registration(actionRowRenderer, row as ActionRow),
  message: (row: RowModel) =>
    registration(messageRowRenderer, row as MessageRow),
  rail: (row: RowModel) => registration(railRowRenderer, row as RailRow),
  mediaTile: (row: RowModel) =>
    registration(mediaTileRowRenderer, row as MediaTileRow),
};
const renderers = {
  metricCard: metricCardRowRenderer,
  dataRow: dataRowRenderer,
  activity: activityRowRenderer,
  system: systemRowRenderer,
  action: actionRowRenderer,
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
