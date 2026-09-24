import type { WalletGroupRow } from '../../models';
import { walletGroupRowRenderer } from './WalletGroupRowRenderer';
import type { IdentityRow, SectionHeaderRow } from '../../models';
import { identityRowRenderer } from './IdentityRowRenderer';
import { sectionHeaderRowRenderer } from './SectionHeaderRowRenderer';
import type { MarketRow } from '../../models';
import { marketRowRenderer } from './MarketRowRenderer';
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
  measure: (row: R, width: number, layout?: string) => number;
  measureRendered: (body: HTMLElement, row: R) => number | undefined;
  recycle: (body: HTMLElement) => void;
  reorderPreview?: (
    body: HTMLElement,
    row: R
  ) => { element: HTMLElement; height: number } | undefined;
};
function registration<R extends RowModel>(renderer: Renderer<R>, row: R) {
  return {
    key: renderer.key,
    appliesSizePreset: renderer.appliesSizePreset?.(row) ?? true,
    create: (document: Document, primitives: RowPrimitives) =>
      renderer.create(document, row, primitives),
    bind: (body: HTMLElement, primitives: RowPrimitives) =>
      renderer.bind(body, row, primitives),
    measure: (width: number, layout?: string) =>
      renderer.measure(row, width, layout),
    measureRendered: (body: HTMLElement) => renderer.measureRendered(body, row),
    reorderPreview: (body: HTMLElement) => renderer.reorderPreview?.(body, row),
  };
}
const factories = {
  walletGroup: (row: RowModel) =>
    registration(walletGroupRowRenderer, row as WalletGroupRow),
  identity: (row: RowModel) =>
    registration(identityRowRenderer, row as IdentityRow),
  sectionHeader: (row: RowModel) =>
    registration(sectionHeaderRowRenderer, row as SectionHeaderRow),
  market: (row: RowModel) => registration(marketRowRenderer, row as MarketRow),
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
} satisfies Record<
  RowModel['type'],
  (row: RowModel) => ReturnType<typeof registration>
>;
const renderers = {
  walletGroup: walletGroupRowRenderer,
  identity: identityRowRenderer,
  sectionHeader: sectionHeaderRowRenderer,
  market: marketRowRenderer,
  metricCard: metricCardRowRenderer,
  dataRow: dataRowRenderer,
  activity: activityRowRenderer,
  system: systemRowRenderer,
  action: actionRowRenderer,
  message: messageRowRenderer,
  rail: railRowRenderer,
  mediaTile: mediaTileRowRenderer,
};
export type RowRendererKey = RowModel['type'];
const isRegistered = (key: string): key is keyof typeof factories =>
  Object.prototype.hasOwnProperty.call(factories, key);

// Closed registration; a row's data key, styles and placement never affect reuse.
export function rowRenderer(row: RowModel) {
  if (!isRegistered(row.type))
    throw new Error('No renderer registered for ' + row.type);
  return factories[row.type](row);
}
export function rowRendererKey(row: RowModel): RowRendererKey {
  return row.type;
}
export function recycleRowBody(body: HTMLElement): boolean {
  const key = body.dataset.nlRenderer ?? '';
  if (!isRegistered(key)) return false;
  renderers[key].recycle(body);
  return true;
}
