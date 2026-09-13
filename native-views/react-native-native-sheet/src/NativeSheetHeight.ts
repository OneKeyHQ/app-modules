export interface ResolveNativeSheetHeightOptions {
  currentHeight?: number;
  explicitHeight?: number;
  measuredHeight?: number;
  maxHeight: number;
  shouldAutoMeasure: boolean;
}

export interface ResolveNativeSheetLayoutConstraintsOptions {
  lockedHeight?: number;
  maxHeight: number;
  shouldAutoMeasure: boolean;
}

export interface NativeSheetLayoutConstraints {
  hostHeight?: number;
  hostMaxHeight?: number;
  shouldFillHost: boolean;
}

export function resolveNativeSheetLayoutConstraints({
  lockedHeight,
  maxHeight,
  shouldAutoMeasure,
}: ResolveNativeSheetLayoutConstraintsOptions): NativeSheetLayoutConstraints {
  if (shouldAutoMeasure) {
    return {
      hostMaxHeight: maxHeight,
      shouldFillHost: false,
    };
  }
  if (lockedHeight !== undefined) {
    return {
      hostHeight: lockedHeight,
      shouldFillHost: true,
    };
  }
  return {
    hostMaxHeight: maxHeight,
    shouldFillHost: false,
  };
}

export function resolveNativeSheetHeight({
  currentHeight,
  explicitHeight,
  measuredHeight,
  maxHeight,
  shouldAutoMeasure,
}: ResolveNativeSheetHeightOptions): number | undefined {
  if (explicitHeight !== undefined && explicitHeight > 0) {
    return Math.min(explicitHeight, maxHeight);
  }
  if (
    explicitHeight === undefined &&
    shouldAutoMeasure &&
    measuredHeight !== undefined &&
    measuredHeight > 0
  ) {
    return Math.min(measuredHeight, maxHeight);
  }
  if (currentHeight !== undefined && currentHeight > maxHeight) {
    return maxHeight;
  }
  return currentHeight;
}
