/**
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Adapted for the OneKey Android native Text component.
 */

/* eslint-disable @react-native/no-deep-imports */
import * as ReactNativeFeatureFlags from 'react-native/src/private/featureflags/ReactNativeFeatureFlags';
import * as PressabilityDebug from 'react-native/Libraries/Pressability/PressabilityDebug';
import {
  Platform,
  StyleSheet,
  Text as ReactNativeText,
  processColor,
  unstable_TextAncestorContext as TextAncestorContext,
  usePressability,
  type GestureResponderEvent,
  type HostInstance,
  type TextStyle,
} from 'react-native';
import { forwardRef, useContext, useMemo, useState, type Ref } from 'react';

import OneKeyTextNativeComponent from './OneKeyTextNativeHost';
import type { NativeProps } from './OneKeyTextNativeComponent';
import type { TextProps } from './types';

type TextForwardRef = HostInstance;

type TextPressabilityProps = Readonly<{
  onLongPress?: (event: GestureResponderEvent) => unknown;
  onPress?: (event: GestureResponderEvent) => unknown;
  onPressIn?: (event: GestureResponderEvent) => unknown;
  onPressOut?: (event: GestureResponderEvent) => unknown;
  onResponderGrant?: (event: GestureResponderEvent) => void;
  onResponderMove?: (event: GestureResponderEvent) => void;
  onResponderRelease?: (event: GestureResponderEvent) => void;
  onResponderTerminate?: (event: GestureResponderEvent) => void;
  onResponderTerminationRequest?: () => boolean;
  onStartShouldSetResponder?: () => boolean;
  pressRetentionOffset?: TextProps['pressRetentionOffset'];
  suppressHighlighting?: boolean;
}>;

type Mutable<T> = {
  -readonly [Property in keyof T]: T[Property];
};

type NativeTextProps = Mutable<TextProps> &
  Readonly<{
    isHighlighted?: boolean;
    isPressable?: boolean;
    onClick?: (event: GestureResponderEvent) => unknown;
  }>;

function useTextPressability({
  onLongPress,
  onPress,
  onPressIn,
  onPressOut,
  onResponderGrant,
  onResponderMove,
  onResponderRelease,
  onResponderTerminate,
  onResponderTerminationRequest,
  onStartShouldSetResponder,
  pressRetentionOffset,
  suppressHighlighting,
}: TextPressabilityProps) {
  const [isHighlighted, setHighlighted] = useState(false);

  const config = useMemo(() => {
    let processedOnPressIn = onPressIn;
    let processedOnPressOut = onPressOut;

    if (Platform.OS === 'ios') {
      processedOnPressIn = (event: GestureResponderEvent) => {
        setHighlighted(suppressHighlighting == null || !suppressHighlighting);
        onPressIn?.(event);
      };

      processedOnPressOut = (event: GestureResponderEvent) => {
        setHighlighted(false);
        onPressOut?.(event);
      };
    }

    return {
      disabled: false,
      pressRectOffset: pressRetentionOffset,
      onLongPress,
      onPress,
      onPressIn: processedOnPressIn,
      onPressOut: processedOnPressOut,
    };
  }, [
    onLongPress,
    onPress,
    onPressIn,
    onPressOut,
    pressRetentionOffset,
    suppressHighlighting,
  ]);

  const eventHandlers = usePressability(config);

  const eventHandlersForText = useMemo(
    () =>
      eventHandlers == null
        ? null
        : {
            onResponderGrant(event: GestureResponderEvent) {
              eventHandlers.onResponderGrant(event);
              onResponderGrant?.(event);
            },
            onResponderMove(event: GestureResponderEvent) {
              eventHandlers.onResponderMove(event);
              onResponderMove?.(event);
            },
            onResponderRelease(event: GestureResponderEvent) {
              eventHandlers.onResponderRelease(event);
              onResponderRelease?.(event);
            },
            onResponderTerminate(event: GestureResponderEvent) {
              eventHandlers.onResponderTerminate(event);
              onResponderTerminate?.(event);
            },
            onClick: eventHandlers.onClick,
            onResponderTerminationRequest:
              onResponderTerminationRequest ??
              eventHandlers.onResponderTerminationRequest,
            onStartShouldSetResponder:
              onStartShouldSetResponder ??
              eventHandlers.onStartShouldSetResponder,
          },
    [
      eventHandlers,
      onResponderGrant,
      onResponderMove,
      onResponderRelease,
      onResponderTerminate,
      onResponderTerminationRequest,
      onStartShouldSetResponder,
    ]
  );

  return useMemo(
    () => [isHighlighted, eventHandlersForText] as const,
    [isHighlighted, eventHandlersForText]
  );
}

type PressableTextProps = Readonly<{
  textPressabilityProps: TextPressabilityProps;
  textProps: NativeTextProps;
}>;

const PressableText = forwardRef<TextForwardRef, PressableTextProps>(
  function PressableText(
    { textPressabilityProps, textProps },
    ref: Ref<TextForwardRef>
  ) {
    const [isHighlighted, eventHandlersForText] = useTextPressability(
      textPressabilityProps
    );

    return (
      <OneKeyTextNativeComponent
        {...(textProps as NativeProps)}
        {...eventHandlersForText}
        isHighlighted={isHighlighted}
        isPressable
        ref={ref}
      />
    );
  }
);

function TextImpl(
  {
    accessible,
    accessibilityLabel,
    accessibilityRole,
    accessibilityState,
    allowFontScaling,
    'aria-busy': ariaBusy,
    'aria-checked': ariaChecked,
    'aria-disabled': ariaDisabled,
    'aria-expanded': ariaExpanded,
    'aria-hidden': ariaHidden,
    'aria-label': ariaLabel,
    'aria-selected': ariaSelected,
    children,
    ellipsizeMode,
    disabled,
    id,
    nativeID,
    numberOfLines,
    onLongPress,
    onPress,
    onPressIn,
    onPressOut,
    onResponderGrant,
    onResponderMove,
    onResponderRelease,
    onResponderTerminate,
    onResponderTerminationRequest,
    onStartShouldSetResponder,
    pressRetentionOffset,
    role,
    selectable,
    selectionColor,
    suppressHighlighting,
    style,
    ...restProps
  }: TextProps,
  forwardedRef: Ref<TextForwardRef>
) {
  const hasTextAncestor = useContext(TextAncestorContext);
  if (hasTextAncestor) {
    return (
      <ReactNativeText
        {...{
          accessible,
          accessibilityLabel,
          accessibilityRole,
          accessibilityState,
          allowFontScaling,
          'aria-busy': ariaBusy,
          'aria-checked': ariaChecked,
          'aria-disabled': ariaDisabled,
          'aria-expanded': ariaExpanded,
          'aria-hidden': ariaHidden,
          'aria-label': ariaLabel,
          'aria-selected': ariaSelected,
          children,
          disabled,
          ellipsizeMode,
          id,
          nativeID,
          numberOfLines,
          onLongPress,
          onPress,
          onPressIn,
          onPressOut,
          onResponderGrant,
          onResponderMove,
          onResponderRelease,
          onResponderTerminate,
          onResponderTerminationRequest,
          onStartShouldSetResponder,
          pressRetentionOffset,
          role,
          selectable,
          selectionColor,
          style,
          suppressHighlighting,
          ...restProps,
        }}
        ref={forwardedRef}
      />
    );
  }

  const processedProps = { ...restProps } as NativeTextProps;
  const processedAccessibilityLabel = ariaLabel ?? accessibilityLabel;
  let processedAccessibilityState = accessibilityState;

  if (
    ariaBusy != null ||
    ariaChecked != null ||
    ariaDisabled != null ||
    ariaExpanded != null ||
    ariaSelected != null
  ) {
    processedAccessibilityState = {
      busy: ariaBusy ?? accessibilityState?.busy,
      checked: ariaChecked ?? accessibilityState?.checked,
      disabled: ariaDisabled ?? accessibilityState?.disabled,
      expanded: ariaExpanded ?? accessibilityState?.expanded,
      selected: ariaSelected ?? accessibilityState?.selected,
    };
  }

  const accessibilityStateDisabled = processedAccessibilityState?.disabled;
  const processedDisabled = disabled ?? accessibilityStateDisabled;

  if (
    processedDisabled !== accessibilityStateDisabled &&
    ((processedDisabled != null && processedDisabled !== false) ||
      (accessibilityStateDisabled != null &&
        accessibilityStateDisabled !== false))
  ) {
    processedAccessibilityState = {
      ...processedAccessibilityState,
      disabled: processedDisabled,
    };
  }

  if (ariaHidden !== undefined) {
    processedProps.accessibilityElementsHidden = ariaHidden;
    if (ariaHidden) {
      processedProps.importantForAccessibility = 'no-hide-descendants';
    }
  }

  const processedAccessible =
    accessible == null ? onPress != null || onLongPress != null : accessible;
  const isPressable =
    (onPress != null ||
      onLongPress != null ||
      onStartShouldSetResponder != null) &&
    processedDisabled !== true;
  const shouldUseLinkRole =
    isPressable && accessibilityRole == null && role == null;
  const processedAccessibilityRole =
    accessibilityRole ?? (shouldUseLinkRole ? 'link' : undefined);
  const processedRole = shouldUseLinkRole ? undefined : role;
  const processedSelectionColor =
    selectionColor != null ? processColor(selectionColor) : undefined;

  let processedStyle = style;
  if (__DEV__ && PressabilityDebug.isEnabled() && onPress != null) {
    processedStyle = [style, { color: 'magenta' }];
  }

  let processedNumberOfLines = numberOfLines;
  if (processedNumberOfLines != null && !(processedNumberOfLines >= 0)) {
    if (__DEV__) {
      console.error(
        `'numberOfLines' in <Text> must be a non-negative number, received: ${processedNumberOfLines}. The value will be set to 0.`
      );
    }
    processedNumberOfLines = 0;
  }

  let processedSelectable = selectable;
  const flattenedStyle = StyleSheet.flatten(processedStyle);
  if (flattenedStyle != null) {
    let overrides: TextStyle | undefined;
    if (typeof flattenedStyle.fontWeight === 'number') {
      overrides = {
        ...overrides,
        fontWeight: String(
          flattenedStyle.fontWeight
        ) as TextStyle['fontWeight'],
      };
    }

    if (flattenedStyle.userSelect != null) {
      processedSelectable =
        userSelectToSelectableMap[flattenedStyle.userSelect];
      overrides = { ...overrides, userSelect: undefined };
    }

    if (flattenedStyle.verticalAlign != null) {
      overrides = {
        ...overrides,
        textAlignVertical:
          verticalAlignToTextAlignVerticalMap[flattenedStyle.verticalAlign],
        verticalAlign: undefined,
      };
    }

    if (overrides != null) {
      processedStyle = [processedStyle, overrides];
    }
  }

  if (ReactNativeFeatureFlags.defaultTextToOverflowHidden()) {
    processedStyle = [styles.default, processedStyle];
  }

  const processedNativeID = id ?? nativeID;

  if (processedAccessibilityLabel !== undefined) {
    processedProps.accessibilityLabel = processedAccessibilityLabel;
  }
  if (processedAccessibilityRole !== undefined) {
    processedProps.accessibilityRole = processedAccessibilityRole;
  }
  if (processedAccessibilityState !== undefined) {
    processedProps.accessibilityState = processedAccessibilityState;
  }
  if (processedNativeID !== undefined) {
    processedProps.nativeID = processedNativeID;
  }
  if (processedNumberOfLines !== undefined) {
    processedProps.numberOfLines = processedNumberOfLines;
  }
  if (processedSelectable !== undefined) {
    processedProps.selectable = processedSelectable;
  }
  if (processedStyle !== undefined) {
    processedProps.style = processedStyle;
  }
  if (processedSelectionColor !== undefined) {
    processedProps.selectionColor = processedSelectionColor;
  }
  if (processedRole !== undefined) {
    processedProps.role = processedRole;
  }

  processedProps.accessible = processedAccessible;
  processedProps.allowFontScaling = allowFontScaling !== false;
  processedProps.disabled = processedDisabled;
  processedProps.ellipsizeMode = ellipsizeMode ?? 'tail';
  processedProps.children =
    children == null ? (
      children
    ) : (
      <TextAncestorContext.Provider value>
        <ReactNativeText>{children}</ReactNativeText>
      </TextAncestorContext.Provider>
    );

  let nativeText;
  if (isPressable) {
    nativeText = (
      <PressableText
        ref={forwardedRef}
        textProps={processedProps}
        textPressabilityProps={{
          onLongPress,
          onPress,
          onPressIn,
          onPressOut,
          onResponderGrant,
          onResponderMove,
          onResponderRelease,
          onResponderTerminate,
          onResponderTerminationRequest,
          onStartShouldSetResponder,
          pressRetentionOffset,
          suppressHighlighting,
        }}
      />
    );
  } else {
    nativeText = (
      <OneKeyTextNativeComponent
        {...(processedProps as NativeProps)}
        ref={forwardedRef}
      />
    );
  }

  return nativeText;
}

export const Text = forwardRef<TextForwardRef, TextProps>(TextImpl);
Text.displayName = 'Text';

const userSelectToSelectableMap = {
  all: true,
  auto: true,
  contain: true,
  none: false,
  text: true,
} as const;

const verticalAlignToTextAlignVerticalMap = {
  auto: 'auto',
  bottom: 'bottom',
  middle: 'center',
  top: 'top',
} as const;

const styles = StyleSheet.create({
  default: {
    overflow: 'hidden',
  },
});
