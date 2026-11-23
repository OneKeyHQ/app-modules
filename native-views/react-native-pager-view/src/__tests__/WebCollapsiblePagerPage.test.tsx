/**
 * @jest-environment jsdom
 */
import * as React from "react";
import { act } from "react";
import { createRoot, type Root } from "react-dom/client";
import {
  WebCollapsiblePagerPage,
  useWebCollapsiblePagerPage,
  type WebCollapsiblePagerPageBridge,
} from "../WebCollapsiblePagerPage";

Object.assign(globalThis, { IS_REACT_ACT_ENVIRONMENT: true });

class ResizeObserverStub {
  observe() {}
  unobserve() {}
  disconnect() {}
}

function WidthProbe({ onWidth }: { onWidth: (width: number) => void }) {
  const page = useWebCollapsiblePagerPage();
  onWidth(page?.width ?? -1);
  return null;
}

describe("WebCollapsiblePagerPage", () => {
  let root: Root | null = null;
  let container: HTMLElement | null = null;
  let rafSpy: jest.SpyInstance | null = null;

  beforeEach(() => {
    Object.assign(globalThis, { ResizeObserver: ResizeObserverStub });
    // Never run a frame: the first measurement must not depend on one.
    rafSpy = jest
      .spyOn(window, "requestAnimationFrame")
      .mockImplementation(() => 1);
    container = document.createElement("div");
    document.body.appendChild(container);
    root = createRoot(container);
  });

  afterEach(() => {
    act(() => root?.unmount());
    container?.remove();
    rafSpy?.mockRestore();
  });

  // OK-66299: metrics start at width 0; deferring the first measurement to
  // requestAnimationFrame let consumers paint once at width 0.
  it("reports the owner width synchronously once the owner is attached", () => {
    const owner = document.createElement("div");
    Object.defineProperty(owner, "clientWidth", { value: 390 });
    document.body.appendChild(owner);
    const widths: number[] = [];
    let bridge: WebCollapsiblePagerPageBridge | null = null;

    act(() => {
      root?.render(
        <WebCollapsiblePagerPage
          pageKey="page"
          isFocused
          isRouteActive
          headerHeight={0}
          stickyHeaderHeight={0}
          onBridge={(_key, next) => {
            bridge = next;
          }}
          onGeometryChange={() => {}}
          onScroll={() => {}}
          onUserInput={() => {}}
        >
          <WidthProbe onWidth={(width) => widths.push(width)} />
        </WebCollapsiblePagerPage>,
      );
    });
    expect(widths.at(-1)).toBe(0);

    act(() => {
      bridge?.setOwner(owner);
    });

    expect(widths.at(-1)).toBe(390);
    owner.remove();
  });
});
