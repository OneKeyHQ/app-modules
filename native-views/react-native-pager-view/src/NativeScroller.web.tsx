import * as React from "react";
import { ScrollView } from "react-native";

import type { NativeScrollerProps } from "./NativeScroller";

type NativeScrollerWebProps = NativeScrollerProps & {
  dataSet?: Record<string, string>;
};

export const NativeScroller = React.forwardRef<
  React.ElementRef<typeof ScrollView>,
  NativeScrollerProps
>(function NativeScroller(inputProps, ref) {
  const {
    dataSet,
    pagerScrollKey,
    topOverlay: _topOverlay,
    topScrollEdgeEffect: _topScrollEdgeEffect,
    ...props
  } = inputProps as NativeScrollerWebProps;
  const scrollRef = React.useRef<React.ElementRef<typeof ScrollView>>(null);
  const setRef = React.useCallback(
    (node: React.ElementRef<typeof ScrollView> | null) => {
      scrollRef.current = node;
      if (typeof ref === "function") ref(node);
      else if (ref) ref.current = node;
    },
    [ref]
  );
  React.useLayoutEffect(() => {
    const element = scrollRef.current?.getScrollableNode() as unknown as
      | HTMLElement
      | undefined;
    const EventConstructor = element?.ownerDocument?.defaultView?.Event;
    if (EventConstructor)
      element.dispatchEvent(
        new EventConstructor("ok-collapsible-pager-scroll-key-changed", {
          bubbles: true,
        })
      );
  }, [pagerScrollKey]);
  const webDataProps = {
    dataSet: {
      ...dataSet,
      collapsiblePagerScroll: "true",
      collapsiblePagerScrollKey: pagerScrollKey ?? "",
    },
  };

  return <ScrollView {...props} ref={setRef} {...webDataProps} />;
});

NativeScroller.displayName = "NativeScroller";

export type { NativeScrollerProps } from "./NativeScroller";
