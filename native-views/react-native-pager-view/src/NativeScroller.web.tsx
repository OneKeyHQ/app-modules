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
    pagerScrollKey: _pagerScrollKey,
    ...props
  } = inputProps as NativeScrollerWebProps;
  const webDataProps = {
    dataSet: {
      ...dataSet,
      collapsiblePagerScroll: "true",
    },
  };

  return <ScrollView {...props} ref={ref} {...webDataProps} />;
});

NativeScroller.displayName = "NativeScroller";

export type { NativeScrollerProps } from "./NativeScroller";
