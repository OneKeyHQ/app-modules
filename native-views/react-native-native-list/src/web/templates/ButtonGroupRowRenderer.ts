import type { ButtonGroupRow } from '../../models';
import { markActionAnchorSource } from './RowElements';
import type { RowPrimitives } from './RowVisual';

function bind(body: HTMLElement, row: ButtonGroupRow) {
  body.replaceChildren();
  body.className = 'ok-native-list-row ok-native-list-button-group';
  body.removeAttribute('data-nl-container-background');
  body.removeAttribute('data-nl-container-border');
  row.buttons.forEach((descriptor, index) => {
    const button = body.ownerDocument.createElement('button');
    button.type = 'button';
    button.className = 'ok-native-list-block-button';
    button.dataset.nativeListAction = descriptor.actionKey;
    button.dataset.nlButtonKey = descriptor.key;
    button.dataset.nlSlot = `button-${index}`;
    if (descriptor.testID) button.dataset.testid = descriptor.testID;
    button.setAttribute(
      'aria-label',
      descriptor.accessibilityLabel ?? descriptor.label
    );
    button.disabled = Boolean(
      row.disabled || descriptor.disabled || descriptor.loading
    );
    button.setAttribute('aria-busy', descriptor.loading ? 'true' : 'false');
    markActionAnchorSource(button, 'buttonGroup', index);
    const label = body.ownerDocument.createElement('span');
    label.textContent = descriptor.label;
    if (descriptor.loading) {
      const spinner = body.ownerDocument.createElement('span');
      spinner.className = 'ok-native-list-spinner';
      spinner.setAttribute('role', 'progressbar');
      button.appendChild(spinner);
    }
    button.appendChild(label);
    body.appendChild(button);
  });
}

function create(
  document: Document,
  row: ButtonGroupRow,
  _primitives: RowPrimitives
) {
  const body = document.createElement('div');
  body.dataset.nlRenderer = 'buttonGroup';
  bind(body, row);
  return body;
}

export const buttonGroupRowRenderer = {
  key: 'buttonGroup' as const,
  create,
  bind,
  recycle: (body: HTMLElement) => body.replaceChildren(),
  measure: (_row: ButtonGroupRow, _width: number) => 64,
  measureRendered: (body: HTMLElement) => body.scrollHeight,
};
