import * as React from "react";
import { ScrollView, type ScrollViewProps } from "react-native";

export const NATIVE_SCROLLER_NATIVE_ID_PREFIX =
  "rnc-collapsible-pager-native-scroller";

export interface NativeScrollerProps extends ScrollViewProps {
  /** Stable identity used when a page replaces or remounts its primary scroller. */
  pagerScrollKey?: string;
}

/**
 * Primary non-virtualized vertical scroller for a CollapsiblePagerView page.
 */
export const NativeScroller = React.forwardRef<
  React.ElementRef<typeof ScrollView>,
  NativeScrollerProps
>(function NativeScroller(
  { nativeID, nestedScrollEnabled = true, pagerScrollKey, ...props },
  ref
) {
  const generatedId = React.useId();
  const scrollKey = pagerScrollKey ?? nativeID ?? generatedId;

  return (
    <ScrollView
      {...props}
      ref={ref}
      nativeID={`${NATIVE_SCROLLER_NATIVE_ID_PREFIX}:${scrollKey}`}
      nestedScrollEnabled={nestedScrollEnabled}
    />
  );
});

NativeScroller.displayName = "NativeScroller";
