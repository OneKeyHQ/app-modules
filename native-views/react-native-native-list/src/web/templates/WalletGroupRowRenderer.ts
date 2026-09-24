import type { IdentityRow, WalletGroupRow } from '../../models';
import { identityRowRenderer } from './IdentityRowRenderer';
import {
  applyRowContainerStyle,
  resetRowContainerStyle,
} from './RowContainerStyle';
import { setData } from './RowElements';
import type { RowPrimitives } from './RowVisual';

type Member = { wrapper: HTMLElement; body: HTMLElement };
const membersByBody = new WeakMap<HTMLElement, Map<string, Member>>();
// Members are walletSidebar identities; like the legacy engine, their preset
// height (68, +24 with badges) ignores the `size` preset adjustment.
const memberHeight = (member: IdentityRow, width: number) =>
  member.style?.container?.height ??
  member.height ??
  identityRowRenderer.measure(member, width);

function bind(
  body: HTMLElement,
  row: WalletGroupRow,
  primitives: RowPrimitives
) {
  const members = membersByBody.get(body)!;
  const nextKeys = new Set(
    [row.parent, ...row.children].map((member) => member.key)
  );
  for (const [key, member] of members) {
    if (!nextKeys.has(key)) {
      identityRowRenderer.recycle(member.body);
      member.wrapper.remove();
      members.delete(key);
    }
  }
  body.style.cssText = '';
  for (const name of body.getAttributeNames())
    if (name.startsWith('data-') && name !== 'data-nl-renderer')
      body.removeAttribute(name);
  // The group's legacy 1px border is its default one-unit inset (native: a
  // 1-unit padding when the parent has `height`). A style padding replaces that
  // inset, so the border supplies its first unit and CSS padding the rest; the
  // edge-to-content distance is then exactly the style value, as on native and
  // as `measure` budgets it.
  if (row.style?.horizontalPadding !== undefined)
    body.style.paddingInline = `${Math.max(
      0,
      row.style.horizontalPadding - 1
    )}px`;
  if (row.style?.verticalPadding !== undefined)
    body.style.paddingBlock = `${Math.max(0, row.style.verticalPadding - 1)}px`;
  [row.parent, ...row.children].forEach((memberRow, index) => {
    let member = members.get(memberRow.key);
    if (member) {
      resetRowContainerStyle(member.body);
      identityRowRenderer.bind(member.body, memberRow, primitives);
    } else {
      const wrapper = body.ownerDocument.createElement('div');
      wrapper.className = 'ok-native-list-wallet-member';
      const memberBody = identityRowRenderer.create(
        body.ownerDocument,
        memberRow,
        primitives
      );
      wrapper.appendChild(memberBody);
      member = { wrapper, body: memberBody };
      members.set(memberRow.key, member);
    }
    const { wrapper, body: memberBody } = member;
    setData(wrapper, 'nativeListGroupMemberKey', memberRow.key);
    setData(wrapper, 'testid', memberRow.testID);
    setData(wrapper, 'nativeListGroupParent', index === 0);
    setData(wrapper, 'nativeListSelected', memberRow.selected);
    wrapper.style.cssText = '';
    wrapper.style.flexBasis = `${memberHeight(memberRow, 0)}px`;
    wrapper.style.height = wrapper.style.flexBasis;
    wrapper.style.opacity = String(
      (memberRow.style?.container?.opacity ?? memberRow.opacity ?? 1) *
        (memberRow.disabled ? 0.5 : 1)
    );
    if (memberRow.style?.container?.cornerRadius !== undefined)
      wrapper.style.borderRadius = `${memberRow.style.container.cornerRadius}px`;
    if (memberRow.backgroundColor)
      memberBody.style.backgroundColor = memberRow.backgroundColor;
    applyRowContainerStyle(memberBody, memberRow);
    body.appendChild(wrapper);
  });
}
function create(
  document: Document,
  row: WalletGroupRow,
  primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.className = 'ok-native-list-wallet-group';
  body.dataset.nlRenderer = 'walletGroup';
  membersByBody.set(body, new Map());
  bind(body, row, primitives);
  return body;
}
function recycle(body: HTMLElement) {
  const members = membersByBody.get(body);
  members?.forEach((member) => identityRowRenderer.recycle(member.body));
  members?.clear();
  body.replaceChildren();
}
export const walletGroupRowRenderer = {
  key: 'walletGroup' as const,
  reorderPreview: (body: HTMLElement, row: WalletGroupRow) => {
    const parent = membersByBody.get(body)?.get(row.parent.key);
    return parent ? { element: parent.body, height: 68 } : undefined;
  },
  create,
  bind,
  recycle,
  appliesSizePreset: () => false,
  // Legacy group height: members, 12-unit gaps and the 1px border pair, which
  // (like native's 1-unit inset) is budgeted only when the parent carries
  // `height`. `style.verticalPadding` replaces that inset, as on native.
  measure: (row: WalletGroupRow, width: number) =>
    [row.parent, ...row.children].reduce(
      (total, member) => total + memberHeight(member, width),
      0
    ) +
    row.children.length * 12 +
    (row.style?.verticalPadding ?? (row.parent.height !== undefined ? 1 : 0)) *
      2,
  measureRendered: (
    _body: HTMLElement,
    _row: WalletGroupRow
  ): number | undefined => undefined,
};
