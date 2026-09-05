import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from 'react';
import {
  ActivityIndicator,
  Image,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View,
  type LayoutChangeEvent,
  type NativeScrollEvent,
  type NativeSyntheticEvent,
} from 'react-native';
import type { NativeListProps, NativeListRef } from './NativeList.types';
import type {
  CheckboxState,
  LeadingVisual,
  NativeListSnapshot,
  NativeListTheme,
  RowModel,
  SelectionTarget,
  TextTone,
  TrailingAccessory,
} from './models';
import {
  checkboxStateForKeys,
  checkboxStateForSection,
  isSelectableRow,
  reduceSelection,
  selectionStateFromSnapshot,
} from './selection';
import { applyRowPatches, validateSnapshot } from './validation';
import {
  calculateAlignedScrollOffset,
  normalizeIndexScroll,
  normalizeKeyScroll,
  normalizePositionScroll,
  resolveLocationIndex,
  scrollFailure,
  validateOffset,
  type NormalizedPositionScroll,
} from './scrolling';

export type { NativeListProps, NativeListRef } from './NativeList.types';
export type {
  ScrollAlignment,
  ScrollPositionOptions,
  ScrollToEndParams,
  ScrollToIndexFailedInfo,
  ScrollToIndexParams,
  ScrollToItemParams,
  ScrollToKeyParams,
  ScrollToLocationParams,
  ScrollToOffsetParams,
} from './NativeList.types';

type WebRowLayout = Readonly<{ offset: number; length: number }>;

type PendingWebScroll =
  | Readonly<{
      kind: 'index';
      index: number;
      scroll: NormalizedPositionScroll;
    }>
  | Readonly<{ kind: 'offset'; offset: number; animated: boolean }>
  | Readonly<{ kind: 'end'; animated: boolean }>;

const defaultTheme: NativeListTheme = {
  background: '#F7F7F7',
  rowBackground: '#FFFFFF',
  rowSelectedBackground: '#EAF2FF',
  rowPressedBackground: '#E8E8E8',
  primaryText: '#111111',
  secondaryText: '#6B7280',
  separator: '#E5E7EB',
  accent: '#2F6BFF',
  positive: '#15803D',
  negative: '#DC2626',
  info: '#0D74CE',
};

// app-monorepo packages/components/svg/outline/image-square-waves.svg using
// the light-theme $iconDisabled token.
const imageSquareWavesIconUri =
  'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyNCIgaGVpZ2h0PSIyNCIgZmlsbD0iIzAwMDAwMDQ0IiB2aWV3Qm94PSIwIDAgMjQgMjQiPgogIDxwYXRoIGQ9Ik0xNC4yNSA3YTIgMiAwIDEgMSAwIDQgMiAyIDAgMCAxIDAtNFoiLz4KICA8cGF0aCBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik0yMSAyMUgzVjNoMTh2MThaTTUgMTYuNDE0VjE5aDEyLjU4NkwxNCAxNS40MTRsLTIgMi00LTQtMyAzWm0wLTIuODI4IDMtMyA0IDQgMi0yIDUgNVY1SDV2OC41ODZaIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiLz4KPC9zdmc+Cg==';

function textToneColor(
  tone: TextTone | undefined,
  theme: NativeListTheme,
  fallback: 'primary' | 'secondary'
): string {
  switch (tone ?? fallback) {
    case 'positive':
      return theme.positive;
    case 'negative':
      return theme.negative;
    case 'secondary':
      return theme.secondaryText;
    default:
      return theme.primaryText;
  }
}

function visualFromRow(row: RowModel): LeadingVisual | undefined {
  if (row.type === 'identity') {
    return row.leading;
  }
  if (row.type === 'rail') {
    return row.visual;
  }
  if (row.type === 'activity') {
    return row.leading;
  }
  if (row.type === 'message' && row.leading) {
    return row.leading;
  }
  if (row.type === 'dataRow' && row.leading) {
    return row.leading;
  }
  if (row.type === 'metricCard' && row.visual) {
    return row.visual;
  }
  return undefined;
}

function imageFromVisual(visual: LeadingVisual | undefined) {
  if (!visual || visual.kind === 'icon') return undefined;
  if (visual.kind === 'stackedImages') return visual.images[0];
  return visual.image;
}

function RowImage({
  row,
  theme,
}: Readonly<{ row: RowModel; theme: NativeListTheme }>) {
  const visual = visualFromRow(row);
  const image = imageFromVisual(visual);
  const networkImage =
    visual?.kind === 'token' ? visual.networkImage : undefined;
  const shape =
    visual && visual.kind !== 'icon' && visual.kind !== 'stackedImages'
      ? visual.shape ?? 'circle'
      : 'circle';
  const imageRadius = shape === 'circle' ? 20 : shape === 'rounded' ? 10 : 0;
  const backgroundColor =
    visual && visual.kind !== 'image' && visual.kind !== 'stackedImages'
      ? visual.backgroundColor
      : undefined;
  return (
    <View
      style={[
        styles.imageFrame,
        { backgroundColor, borderRadius: imageRadius },
      ]}
    >
      {image ? (
        <Image
          source={{ uri: image.uri, width: image.width, height: image.height }}
          resizeMode={
            image.contentFit === 'fill'
              ? 'stretch'
              : image.contentFit ?? 'cover'
          }
          style={[styles.image, { borderRadius: imageRadius }]}
        />
      ) : (
        <View
          style={[styles.imagePlaceholder, { borderRadius: imageRadius }]}
        />
      )}
      {networkImage ? (
        <View
          style={[
            styles.cornerImageRing,
            { backgroundColor: theme.rowBackground },
          ]}
        >
          <Image
            source={{
              uri: networkImage.uri,
              width: networkImage.width,
              height: networkImage.height,
            }}
            resizeMode={
              networkImage.contentFit === 'fill'
                ? 'stretch'
                : networkImage.contentFit ?? 'cover'
            }
            style={styles.cornerImage}
          />
        </View>
      ) : null}
    </View>
  );
}

function MetricInlineVisual({ visual }: Readonly<{ visual: LeadingVisual }>) {
  const image = imageFromVisual(visual);
  const backgroundColor =
    visual.kind !== 'image' && visual.kind !== 'stackedImages'
      ? visual.backgroundColor
      : undefined;
  return (
    <View style={[styles.metricInlineVisual, { backgroundColor }]}>
      {image ? (
        <Image source={{ uri: image.uri }} style={styles.metricInlineImage} />
      ) : null}
    </View>
  );
}

function withAlpha(color: string, alpha: number): string {
  const match = /^#([\da-f]{2})([\da-f]{2})([\da-f]{2})$/i.exec(color);
  if (!match) return color;
  return `rgba(${Number.parseInt(match[1], 16)}, ${Number.parseInt(
    match[2],
    16
  )}, ${Number.parseInt(match[3], 16)}, ${alpha})`;
}

function Checkbox({
  state,
  disabled,
  loading,
  theme,
}: Readonly<{
  state: CheckboxState;
  disabled?: boolean;
  loading?: boolean;
  theme: NativeListTheme;
}>) {
  if (loading)
    return (
      <ActivityIndicator
        color={theme.primaryText}
        size="small"
        style={disabled && styles.disabled}
      />
    );
  const selected = state !== 'unchecked';
  const checkboxColors = {
    backgroundColor: selected ? theme.primaryText : theme.rowBackground,
    borderColor: selected ? 'transparent' : withAlpha(theme.primaryText, 0.19),
  };
  return (
    <View
      style={[styles.checkbox, checkboxColors, disabled && styles.disabled]}
    >
      {state === 'checked' ? (
        <View
          style={[
            styles.checkboxCheck,
            {
              borderRightColor: theme.rowBackground,
              borderBottomColor: theme.rowBackground,
            },
          ]}
        />
      ) : state === 'indeterminate' ? (
        <View
          style={[
            styles.checkboxIndeterminate,
            { backgroundColor: theme.rowBackground },
          ]}
        />
      ) : null}
    </View>
  );
}

function targetState(
  target: SelectionTarget | undefined,
  fallback: CheckboxState,
  snapshot: NativeListSnapshot,
  selectedKeys: ReadonlySet<string>,
  rowKey: string
): CheckboxState {
  if (!target || target.scope === 'row') {
    return checkboxStateForKeys([rowKey], selectedKeys);
  }
  if (target.scope === 'section') {
    return checkboxStateForSection(
      target.sectionKey,
      snapshot.rows,
      selectedKeys
    );
  }
  if (target.scope === 'list') {
    return checkboxStateForKeys(
      snapshot.rows.filter(isSelectableRow).map((row) => row.key),
      selectedKeys
    );
  }
  return fallback;
}

function Accessory({
  accessory,
  state,
  theme,
}: Readonly<{
  accessory: TrailingAccessory;
  state?: CheckboxState;
  theme: NativeListTheme;
}>) {
  switch (accessory.kind) {
    case 'value':
      return (
        <Text style={accessory.secondary ? styles.secondary : styles.value}>
          {accessory.text}
        </Text>
      );
    case 'valuePair':
      return (
        <View style={styles.amounts}>
          <Text
            style={[
              styles.value,
              { color: textToneColor(accessory.primaryTone, theme, 'primary') },
            ]}
          >
            {accessory.primary}
          </Text>
          <Text
            style={[
              styles.secondary,
              {
                color: textToneColor(
                  accessory.secondaryTone,
                  theme,
                  'secondary'
                ),
              },
            ]}
          >
            {accessory.secondary}
          </Text>
        </View>
      );
    case 'checkbox':
      return (
        <Checkbox
          state={state ?? accessory.state}
          disabled={accessory.disabled}
          loading={accessory.loading}
          theme={theme}
        />
      );
    case 'radio':
      return <Text style={styles.value}>{accessory.checked ? '●' : '○'}</Text>;
    case 'switch':
      return <Text style={styles.value}>{accessory.value ? 'ON' : 'OFF'}</Text>;
    case 'chevron':
      return <Text style={styles.secondary}>›</Text>;
    case 'menu':
      return <Text style={styles.secondary}>•••</Text>;
    case 'drag':
      return <Text style={styles.secondary}>≡</Text>;
    case 'spinner':
      return <ActivityIndicator />;
    case 'progress':
      return (
        <Text style={styles.secondary}>{`${Math.round(
          accessory.value * 100
        )}%`}</Text>
      );
  }
}

function NativeRow({
  row,
  snapshot,
  itemIndex,
  selectedKeys,
  onPress,
  onAction,
}: Readonly<{
  row: RowModel;
  snapshot: NativeListSnapshot;
  itemIndex?: number;
  selectedKeys: ReadonlySet<string>;
  onPress: () => void;
  onAction: (actionKey: string) => void;
}>) {
  const theme = snapshot.theme ?? defaultTheme;
  const pressedBackground = theme.rowPressedBackground ?? '#E8E8E8';
  const selected = selectedKeys.has(row.key);
  const rowBackground = selected
    ? theme.rowSelectedBackground
    : snapshot.layout.kind === 'table' &&
      row.type === 'dataRow' &&
      (row.index ?? itemIndex ?? 0) % 2 === 0
    ? theme.background
    : theme.rowBackground;
  const backgroundForPress = (pressed: boolean, resting: string) =>
    pressed && !row.disabled ? pressedBackground : resting;
  const separatorStyle = row.separator
    ? {
        borderBottomColor: theme.separator,
        borderBottomWidth: StyleSheet.hairlineWidth,
      }
    : undefined;
  if (row.type === 'system' && row.variant === 'spacer') {
    return <View style={{ height: row.height }} />;
  }
  if (row.type === 'sectionHeader') {
    const isSummary = row.variant === 'summary';
    const isGallery = row.variant === 'gallery';
    const isHistory = row.variant === 'history';
    const state = row.checkbox
      ? targetState(
          row.checkbox.target,
          row.checkbox.state,
          snapshot,
          selectedKeys,
          row.key
        )
      : undefined;
    return (
      <Pressable
        disabled={row.disabled || (!row.checkbox && !row.valueActionKey)}
        onPress={() => {
          if (row.checkbox) {
            onAction(row.checkbox.actionKey ?? 'selection');
          } else if (row.valueActionKey) {
            onAction(row.valueActionKey);
          }
        }}
        style={({ pressed }) => [
          styles.sectionHeader,
          isSummary && styles.summaryHeader,
          isGallery && styles.galleryHeader,
          isHistory && styles.historyHeader,
          { backgroundColor: backgroundForPress(pressed, theme.background) },
        ]}
      >
        <View style={styles.flex}>
          <Text
            style={[
              styles.sectionTitle,
              isSummary && styles.summaryTitle,
              isGallery && styles.galleryTitle,
              isHistory && styles.historyTitle,
              {
                color:
                  isSummary || isGallery
                    ? theme.primaryText
                    : theme.secondaryText,
              },
            ]}
          >
            {row.title}
          </Text>
          {row.subtitle ? (
            <Text style={[styles.secondary, { color: theme.secondaryText }]}>
              {row.subtitle}
            </Text>
          ) : null}
        </View>
        {row.value ? (
          <Text
            style={[
              styles.value,
              isSummary && styles.summaryValue,
              {
                color: isSummary ? theme.secondaryText : theme.primaryText,
              },
            ]}
          >
            {row.value}
          </Text>
        ) : null}
        {row.checkbox && state ? (
          <Checkbox
            state={state}
            disabled={row.checkbox.disabled}
            loading={row.checkbox.loading}
            theme={theme}
          />
        ) : null}
      </Pressable>
    );
  }
  if (row.type === 'action') {
    const checkboxState = row.checkbox
      ? targetState(
          row.checkbox.target,
          row.checkbox.state,
          snapshot,
          selectedKeys,
          row.key
        )
      : undefined;
    return (
      <Pressable
        disabled={row.disabled}
        onPress={() => onAction(row.actionKey)}
        style={({ pressed }) => [
          styles.action,
          {
            backgroundColor: backgroundForPress(pressed, theme.rowBackground),
          },
          separatorStyle,
        ]}
      >
        <Text
          style={[
            styles.title,
            { color: row.tone === 'danger' ? theme.negative : theme.accent },
          ]}
        >
          {row.title}
        </Text>
        {row.checkbox && checkboxState ? (
          <Checkbox
            state={checkboxState}
            disabled={row.checkbox.disabled}
            loading={row.checkbox.loading}
            theme={theme}
          />
        ) : null}
        {row.trailing?.map((accessory, index) => (
          <Accessory
            key={`${accessory.kind}-${index}`}
            accessory={accessory}
            theme={theme}
          />
        ))}
      </Pressable>
    );
  }
  if (row.type === 'system') {
    return (
      <Pressable
        disabled={row.disabled}
        onPress={() => row.variant === 'retry' && onAction(row.actionKey)}
        style={({ pressed }) => [
          styles.system,
          { backgroundColor: backgroundForPress(pressed, theme.background) },
        ]}
      >
        {row.variant === 'loading' ? <ActivityIndicator /> : null}
        <Text
          style={[
            styles.secondary,
            row.variant === 'noMatch' && styles.noMatchText,
            { color: theme.secondaryText },
          ]}
        >
          {row.message ?? (row.variant === 'end' ? 'End' : '')}
        </Text>
      </Pressable>
    );
  }
  if (row.type === 'rail') {
    return (
      <Pressable
        disabled={row.disabled}
        onPress={onPress}
        style={({ pressed }) => [
          styles.rail,
          { backgroundColor: backgroundForPress(pressed, rowBackground) },
        ]}
      >
        <RowImage row={row} theme={theme} />
        <Text
          numberOfLines={2}
          style={[styles.railTitle, { color: theme.primaryText }]}
        >
          {row.title}
        </Text>
        {row.badge ? <Text style={styles.badge}>{row.badge.text}</Text> : null}
      </Pressable>
    );
  }
  if (row.type === 'mediaTile') {
    const closeActionKey = row.closeActionKey;
    return (
      <Pressable disabled={row.disabled} onPress={onPress} style={styles.tile}>
        {({ pressed }) => (
          <>
            {row.imageState === 'empty' ? (
              <View
                style={[
                  styles.tileImage,
                  styles.tileImageEmpty,
                  pressed && styles.tileImagePressed,
                ]}
              />
            ) : row.imageState === 'error' ? (
              <View
                style={[
                  styles.tileImage,
                  styles.tileImageError,
                  pressed && styles.tileImagePressed,
                ]}
              >
                <Image
                  source={{ uri: imageSquareWavesIconUri }}
                  style={styles.tileImageErrorIcon}
                />
              </View>
            ) : row.image ? (
              <Image
                source={{ uri: row.image.uri }}
                resizeMode={
                  row.image.contentFit === 'fill'
                    ? 'stretch'
                    : row.image.contentFit ?? 'cover'
                }
                style={[styles.tileImage, pressed && styles.tileImagePressed]}
              />
            ) : null}
            <View style={styles.tileMetadata}>
              <View style={styles.tileSubtitleRow}>
                <Text
                  numberOfLines={1}
                  style={[styles.tileSubtitle, { color: theme.secondaryText }]}
                >
                  {row.subtitle || '-'}
                </Text>
                {row.networkImage ? (
                  <Image
                    source={{ uri: row.networkImage.uri }}
                    style={styles.tileNetworkImage}
                  />
                ) : null}
              </View>
              <Text
                numberOfLines={1}
                style={[styles.tileTitle, { color: theme.primaryText }]}
              >
                {row.title}
              </Text>
            </View>
            {closeActionKey ? (
              <Pressable onPress={() => onAction(closeActionKey)}>
                <Text style={styles.close}>×</Text>
              </Pressable>
            ) : null}
          </>
        )}
      </Pressable>
    );
  }
  if (row.type === 'metricCard') {
    if (row.variant === 'activity' || row.variant === 'performance') {
      const metrics = row.metrics ?? [];
      const renderMetric = (
        metric: (typeof metrics)[number],
        shaded = false
      ) => (
        <View
          key={metric.key}
          style={[styles.compositeMetric, shaded && styles.compositeSubcard]}
        >
          <Text style={[styles.secondary, { color: theme.secondaryText }]}>
            {metric.label}
          </Text>
          <View style={styles.metricValueRow}>
            {metric.visual ? (
              <MetricInlineVisual visual={metric.visual} />
            ) : null}
            <Text
              style={[
                styles.compositeValue,
                { color: textToneColor(metric.tone, theme, 'primary') },
              ]}
            >
              {metric.value}
            </Text>
          </View>
        </View>
      );
      return (
        <Pressable
          disabled={row.disabled}
          onPress={onPress}
          style={({ pressed }) => [
            styles.compositeCard,
            {
              backgroundColor: backgroundForPress(
                pressed,
                theme.subduedBackground ?? '#F9F9F9'
              ),
            },
          ]}
        >
          <Text
            style={[styles.compositeHeading, { color: theme.secondaryText }]}
          >
            {row.title}
          </Text>
          <View style={styles.compositeRow}>
            {metrics.slice(0, 2).map((metric) => renderMetric(metric))}
          </View>
          {row.variant === 'activity' ? (
            <View
              style={[
                styles.compositeDivider,
                { backgroundColor: theme.separator },
              ]}
            />
          ) : (
            <View
              style={[
                styles.progressTrack,
                { backgroundColor: theme.negative },
              ]}
            >
              <View
                style={[
                  styles.progressFill,
                  {
                    width: `${Math.round((row.progress ?? 0) * 100)}%`,
                    backgroundColor: theme.positive,
                  },
                ]}
              />
            </View>
          )}
          <View style={styles.compositeRow}>
            {metrics
              .slice(2)
              .map((metric) =>
                renderMetric(metric, row.variant === 'performance')
              )}
          </View>
        </Pressable>
      );
    }
    const trendColor =
      row.trendTone === 'positive'
        ? theme.positive
        : row.trendTone === 'negative'
        ? theme.negative
        : theme.secondaryText;
    return (
      <Pressable
        disabled={row.disabled}
        onPress={onPress}
        style={({ pressed }) => [
          styles.metricCard,
          { backgroundColor: backgroundForPress(pressed, rowBackground) },
        ]}
      >
        {row.visual ? <RowImage row={row} theme={theme} /> : null}
        <Text
          numberOfLines={1}
          style={[styles.secondary, { color: theme.secondaryText }]}
        >
          {row.title}
        </Text>
        <Text
          numberOfLines={1}
          style={[styles.metricValue, { color: theme.primaryText }]}
        >
          {row.value}
        </Text>
        {row.trend ? (
          <Text
            numberOfLines={1}
            style={[styles.secondary, { color: trendColor }]}
          >
            {row.trend}
          </Text>
        ) : null}
        {row.subtitle ? (
          <Text
            numberOfLines={1}
            style={[styles.secondary, { color: theme.secondaryText }]}
          >
            {row.subtitle}
          </Text>
        ) : null}
        {row.badge ? <Text style={styles.badge}>{row.badge.text}</Text> : null}
      </Pressable>
    );
  }
  if (row.type === 'dataRow') {
    const checkboxState = row.checkbox
      ? targetState(
          row.checkbox.target,
          row.checkbox.state,
          snapshot,
          selectedKeys,
          row.key
        )
      : undefined;
    return (
      <Pressable
        disabled={row.disabled}
        onPress={onPress}
        style={({ pressed }) => [
          styles.row,
          { backgroundColor: backgroundForPress(pressed, rowBackground) },
          separatorStyle,
        ]}
      >
        {row.checkbox && checkboxState ? (
          <Checkbox
            state={checkboxState}
            disabled={row.checkbox.disabled}
            loading={row.checkbox.loading}
            theme={theme}
          />
        ) : null}
        {row.index !== undefined ? (
          <Text style={styles.index}>{row.index}</Text>
        ) : null}
        {row.favorite ? <Text style={styles.favorite}>☆</Text> : null}
        {row.leading ? <RowImage row={row} theme={theme} /> : null}
        {row.columns.map((column) => (
          <View
            key={column.key}
            style={[
              styles.dataCell,
              column.alignment === 'end'
                ? styles.dataCellEnd
                : column.alignment === 'center'
                ? styles.dataCellCenter
                : styles.dataCellStart,
              {
                flex: column.weight ?? 1,
              },
            ]}
          >
            <View style={styles.dataPrimaryLine}>
              <Text
                numberOfLines={1}
                style={[
                  styles.dataPrimary,
                  { color: textToneColor(column.tone, theme, 'primary') },
                ]}
              >
                {column.text}
              </Text>
              {column.key === 'asset'
                ? row.badges?.map((badge) => (
                    <Text key={badge.key} style={styles.dataBadge}>
                      {badge.text}
                    </Text>
                  ))
                : null}
            </View>
            {column.secondaryText ? (
              <Text
                numberOfLines={1}
                style={[
                  styles.secondary,
                  {
                    color: textToneColor(
                      column.secondaryTone,
                      theme,
                      'secondary'
                    ),
                  },
                ]}
              >
                {column.secondaryText}
              </Text>
            ) : null}
          </View>
        ))}
      </Pressable>
    );
  }

  const title = row.title;
  const subtitle =
    row.type === 'identity'
      ? row.subtitle
      : row.type === 'activity'
      ? row.description
      : row.body;
  const trailing = row.type === 'identity' ? row.trailing ?? [] : [];
  return (
    <Pressable
      disabled={row.disabled}
      onPress={onPress}
      style={({ pressed }) => [
        styles.row,
        row.type === 'identity' && row.tertiary ? styles.tertiaryRow : null,
        { backgroundColor: backgroundForPress(pressed, rowBackground) },
        separatorStyle,
      ]}
    >
      <RowImage row={row} theme={theme} />
      {row.type === 'message' && row.unread ? (
        <View style={[styles.unread, { backgroundColor: theme.accent }]} />
      ) : null}
      <View style={styles.flex}>
        <Text
          numberOfLines={row.type === 'identity' ? row.titleLines ?? 1 : 1}
          style={[styles.title, { color: theme.primaryText }]}
        >
          {title}
        </Text>
        {subtitle ? (
          <Text
            numberOfLines={row.type === 'message' ? row.bodyLines ?? 3 : 2}
            style={[styles.secondary, { color: theme.secondaryText }]}
          >
            {subtitle}
          </Text>
        ) : null}
        {row.type === 'identity' && row.tertiary ? (
          <Text
            numberOfLines={1}
            style={[
              styles.secondary,
              {
                color:
                  row.tertiaryTone === 'info'
                    ? theme.info ?? '#0D74CE'
                    : theme.secondaryText,
              },
            ]}
          >
            {row.tertiary}
          </Text>
        ) : null}
        {row.type === 'activity' && row.footerActions ? (
          <View style={styles.actions}>
            {row.footerActions.map((action) => (
              <Pressable
                key={action.key}
                disabled={action.disabled}
                onPress={() => onAction(action.key)}
              >
                <Text
                  style={[
                    styles.actionLabel,
                    {
                      color:
                        action.tone === 'danger'
                          ? theme.negative
                          : theme.accent,
                    },
                  ]}
                >
                  {action.label}
                </Text>
              </Pressable>
            ))}
          </View>
        ) : null}
      </View>
      {row.type === 'activity' ? (
        <View style={styles.amounts}>
          <Text style={[styles.value, { color: theme.primaryText }]}>
            {row.primaryAmount}
          </Text>
          <Text style={[styles.secondary, { color: theme.secondaryText }]}>
            {row.secondaryAmount}
          </Text>
        </View>
      ) : null}
      {row.type === 'message' ? (
        <Text style={[styles.time, { color: theme.secondaryText }]}>
          {row.time}
        </Text>
      ) : null}
      {trailing.map((accessory, index) => (
        <Accessory
          key={`${accessory.kind}-${index}`}
          accessory={accessory}
          theme={theme}
          state={
            accessory.kind === 'checkbox'
              ? targetState(
                  accessory.target,
                  accessory.state,
                  snapshot,
                  selectedKeys,
                  row.key
                )
              : undefined
          }
        />
      ))}
    </Pressable>
  );
}

export const NativeList = forwardRef<NativeListRef, NativeListProps>(
  function NativeList(
    {
      snapshot: snapshotProp,
      onRowAction,
      onSelectionDelta,
      onEndReached,
      onVisibleRangeChanged,
      onRefresh,
      onScrollToIndexFailed,
      initialScrollIndex,
      initialScrollKey,
      initialScrollViewPosition,
      initialScrollViewOffset,
      style,
      ...viewProps
    },
    ref
  ) {
    const [snapshot, setSnapshot] = useState(() =>
      validateSnapshot(snapshotProp)
    );
    const [selectedKeys, setSelectedKeys] = useState<ReadonlySet<string>>(
      () => selectionStateFromSnapshot(snapshotProp).selectedKeys
    );
    const scrollRef = useRef<React.ElementRef<typeof ScrollView>>(null);
    const reachedGeneration = useRef<number | undefined>(undefined);
    const snapshotRef = useRef(snapshot);
    snapshotRef.current = snapshot;
    const rowLayouts = useRef(new Map<string, WebRowLayout>());
    const viewportLength = useRef(0);
    const contentLength = useRef(0);
    const currentOffset = useRef(0);
    const pendingScroll = useRef<PendingWebScroll | undefined>(undefined);
    const didApplyInitialScroll = useRef(false);

    useEffect(() => {
      const next = validateSnapshot(snapshotProp);
      snapshotRef.current = next;
      setSnapshot(next);
      setSelectedKeys(selectionStateFromSnapshot(snapshotProp).selectedKeys);
      reachedGeneration.current = undefined;
    }, [snapshotProp]);

    const horizontal = snapshot.layout.orientation === 'horizontal';

    const averageItemLength = () => {
      const layouts = Array.from(rowLayouts.current.values());
      return layouts.length
        ? layouts.reduce((sum, layout) => sum + layout.length, 0) /
            layouts.length
        : 0;
    };

    const emitIndexFailure = (
      index: number,
      reason: Parameters<typeof scrollFailure>[2]
    ) => {
      onScrollToIndexFailed?.(
        scrollFailure(
          snapshotRef.current.rows,
          index,
          reason,
          averageItemLength()
        )
      );
    };

    const scrollToAbsoluteOffset = (offset: number, animated: boolean) => {
      const resolvedOffset = Math.min(
        Math.max(0, offset),
        Math.max(0, contentLength.current - viewportLength.current)
      );
      currentOffset.current = resolvedOffset;
      scrollRef.current?.scrollTo(
        horizontal
          ? { x: resolvedOffset, y: 0, animated }
          : { x: 0, y: resolvedOffset, animated }
      );
    };

    const performIndexScroll = (
      index: number,
      scroll: NormalizedPositionScroll
    ) => {
      const row = snapshotRef.current.rows[index];
      if (!row) {
        emitIndexFailure(index, 'index-out-of-range');
        return;
      }
      const layout = rowLayouts.current.get(row.key);
      if (
        !layout ||
        viewportLength.current <= 0 ||
        contentLength.current <= 0
      ) {
        pendingScroll.current = { kind: 'index', index, scroll };
        return;
      }
      pendingScroll.current = undefined;
      scrollToAbsoluteOffset(
        calculateAlignedScrollOffset({
          itemOffset: layout.offset,
          itemLength: layout.length,
          viewportLength: viewportLength.current,
          contentLength: contentLength.current,
          currentOffset: currentOffset.current,
          alignment: scroll.alignment,
          viewPosition: scroll.viewPosition,
          viewOffset: scroll.viewOffset,
        }),
        scroll.animated
      );
    };

    const performPendingScroll = () => {
      const pending = pendingScroll.current;
      if (!pending) return;
      if (pending.kind === 'index') {
        performIndexScroll(pending.index, pending.scroll);
      } else if (pending.kind === 'offset') {
        if (viewportLength.current <= 0 || contentLength.current <= 0) return;
        pendingScroll.current = undefined;
        scrollToAbsoluteOffset(pending.offset, pending.animated);
      } else {
        if (viewportLength.current <= 0 || contentLength.current <= 0) return;
        pendingScroll.current = undefined;
        scrollToAbsoluteOffset(
          contentLength.current - viewportLength.current,
          pending.animated
        );
      }
    };

    const applyInitialScroll = () => {
      if (
        didApplyInitialScroll.current ||
        viewportLength.current <= 0 ||
        contentLength.current <= 0
      )
        return;
      didApplyInitialScroll.current = true;
      if (initialScrollIndex !== undefined) {
        const { index, scroll } = normalizeIndexScroll({
          index: initialScrollIndex,
          animated: false,
          viewPosition: initialScrollViewPosition,
          viewOffset: initialScrollViewOffset,
        });
        performIndexScroll(index, scroll);
      } else if (initialScrollKey !== undefined) {
        const index = snapshotRef.current.rows.findIndex(
          (row) => row.key === initialScrollKey
        );
        if (index >= 0) {
          performIndexScroll(
            index,
            normalizePositionScroll(
              {
                animated: false,
                viewPosition: initialScrollViewPosition,
                viewOffset: initialScrollViewOffset,
              },
              'start'
            )
          );
        }
      }
    };

    useImperativeHandle(ref, () => ({
      applySnapshot(nextSnapshot) {
        const next = validateSnapshot(nextSnapshot);
        snapshotRef.current = next;
        setSnapshot(next);
        setSelectedKeys(selectionStateFromSnapshot(nextSnapshot).selectedKeys);
      },
      applyPatches(patches) {
        setSnapshot((current) => {
          const next = applyRowPatches(current, patches);
          snapshotRef.current = next;
          return next;
        });
      },
      reconcileSelection(keys) {
        setSelectedKeys(new Set(keys));
      },
      scrollToKey(paramsOrKey, animated, alignment) {
        const { key, scroll } = normalizeKeyScroll(
          paramsOrKey,
          animated,
          alignment
        );
        const index = snapshotRef.current.rows.findIndex(
          (row) => row.key === key
        );
        if (index >= 0) performIndexScroll(index, scroll);
      },
      scrollToIndex(paramsOrIndex, animated, alignment) {
        const { index, scroll } = normalizeIndexScroll(
          paramsOrIndex,
          animated,
          alignment
        );
        performIndexScroll(index, scroll);
      },
      scrollToItem(params) {
        const index = snapshotRef.current.rows.findIndex(
          (row) => row.key === params.item.key
        );
        if (index < 0) {
          emitIndexFailure(-1, 'item-not-found');
          return;
        }
        performIndexScroll(index, normalizePositionScroll(params, 'start'));
      },
      scrollToOffset({ offset, animated = true }) {
        validateOffset(offset);
        if (viewportLength.current <= 0 || contentLength.current <= 0) {
          pendingScroll.current = { kind: 'offset', offset, animated };
          return;
        }
        scrollToAbsoluteOffset(offset, animated);
      },
      scrollToEnd({ animated = true } = {}) {
        if (viewportLength.current <= 0 || contentLength.current <= 0) {
          pendingScroll.current = { kind: 'end', animated };
          return;
        }
        scrollToAbsoluteOffset(
          contentLength.current - viewportLength.current,
          animated
        );
      },
      scrollToLocation(params) {
        const index = resolveLocationIndex(snapshotRef.current.rows, params);
        if (index === undefined) {
          const sectionCount = snapshotRef.current.rows.filter(
            (row) => row.type === 'sectionHeader' && row.variant !== 'summary'
          ).length;
          emitIndexFailure(
            params.itemIndex,
            params.sectionIndex >= sectionCount
              ? 'section-out-of-range'
              : 'item-out-of-range'
          );
          return;
        }
        performIndexScroll(index, normalizePositionScroll(params, 'start'));
      },
      setRefreshing(refreshing) {
        setSnapshot((current) => {
          const next = {
            ...current,
            capabilities: { ...current.capabilities, refreshing },
          };
          snapshotRef.current = next;
          return next;
        });
      },
    }));

    const activateSelection = (row: RowModel) => {
      if (!snapshot.selection?.rowPressToggles || !isSelectableRow(row)) {
        onRowAction?.({
          rowKey: row.key,
          actionKey: 'press',
          sectionKey: row.sectionKey,
        });
        return;
      }
      const result = reduceSelection(
        { mode: snapshot.selection.mode, selectedKeys },
        { scope: 'row', key: row.key },
        snapshot.rows
      );
      setSelectedKeys(result.state.selectedKeys);
      if (result.delta.addedKeys.length || result.delta.removedKeys.length) {
        onSelectionDelta?.(result.delta);
      }
    };

    const activateAction = (row: RowModel, actionKey: string) => {
      const checkbox =
        row.type === 'sectionHeader' ||
        row.type === 'action' ||
        row.type === 'dataRow'
          ? row.checkbox
          : undefined;
      const target = checkbox?.target;
      if (target && snapshot.selection) {
        const action =
          target.scope === 'row'
            ? { scope: 'row' as const, key: row.key }
            : target;
        const result = reduceSelection(
          { mode: snapshot.selection.mode, selectedKeys },
          action,
          snapshot.rows
        );
        setSelectedKeys(result.state.selectedKeys);
        onSelectionDelta?.(result.delta);
      } else {
        onRowAction?.({
          rowKey: row.key,
          actionKey,
          sectionKey: row.sectionKey,
        });
      }
    };

    const handleScroll = (event: NativeSyntheticEvent<NativeScrollEvent>) => {
      const { contentOffset, contentSize, layoutMeasurement } =
        event.nativeEvent;
      currentOffset.current = horizontal ? contentOffset.x : contentOffset.y;
      if (
        !snapshot.capabilities?.loadMore ||
        reachedGeneration.current === snapshot.generation
      )
        return;
      const threshold = snapshot.capabilities.endReachedThreshold ?? 0.2;
      const offset = horizontal ? contentOffset.x : contentOffset.y;
      const size = horizontal ? contentSize.width : contentSize.height;
      const viewport = horizontal
        ? layoutMeasurement.width
        : layoutMeasurement.height;
      if (offset + viewport >= size - viewport * threshold) {
        reachedGeneration.current = snapshot.generation;
        onEndReached?.({
          generation: snapshot.generation,
          lastKey: snapshot.rows.at(-1)?.key,
        });
      }
    };

    const content =
      snapshot.rows.length > 0
        ? snapshot.rows
        : snapshot.emptyState
        ? [snapshot.emptyState]
        : [];
    const theme = snapshot.theme ?? defaultTheme;
    return (
      <View
        {...viewProps}
        style={[styles.container, { backgroundColor: theme.background }, style]}
      >
        <ScrollView
          ref={scrollRef}
          horizontal={horizontal}
          onLayout={(event: LayoutChangeEvent) => {
            viewportLength.current = horizontal
              ? event.nativeEvent.layout.width
              : event.nativeEvent.layout.height;
            applyInitialScroll();
            performPendingScroll();
          }}
          onScroll={handleScroll}
          scrollEventThrottle={32}
          refreshControl={
            snapshot.capabilities?.pullToRefresh ? (
              <RefreshControl
                refreshing={snapshot.capabilities.refreshing ?? false}
                onRefresh={onRefresh}
              />
            ) : undefined
          }
          contentContainerStyle={[
            snapshot.layout.kind === 'grid'
              ? styles.grid
              : horizontal
              ? styles.horizontal
              : undefined,
            {
              paddingHorizontal:
                snapshot.layout.contentPaddingHorizontal ??
                snapshot.layout.contentPadding ??
                0,
              paddingTop:
                snapshot.layout.contentPaddingTop ??
                snapshot.layout.contentPadding ??
                0,
              paddingBottom:
                snapshot.layout.contentPaddingBottom ??
                snapshot.layout.contentPadding ??
                0,
              gap: snapshot.layout.itemSpacing ?? 0,
            },
          ]}
          onContentSizeChange={(width, height) => {
            contentLength.current = horizontal ? width : height;
            applyInitialScroll();
            performPendingScroll();
            const first = content[0];
            const last = content.at(-1);
            onVisibleRangeChanged?.({
              firstKey: first?.key,
              lastKey: last?.key,
              firstIndex: first ? 0 : -1,
              lastIndex: last ? content.length - 1 : -1,
            });
          }}
        >
          {content.map((row, itemIndex) => (
            <View
              key={row.key}
              onLayout={(event: LayoutChangeEvent) => {
                const layout = event.nativeEvent.layout;
                rowLayouts.current.set(row.key, {
                  offset: horizontal ? layout.x : layout.y,
                  length: horizontal ? layout.width : layout.height,
                });
                applyInitialScroll();
                performPendingScroll();
              }}
              style={
                snapshot.layout.kind === 'grid'
                  ? { width: `${100 / (snapshot.layout.gridColumns ?? 2)}%` }
                  : snapshot.layout.kind === 'sectioned' &&
                    row.type === 'sectionHeader'
                  ? styles.sectionBreak
                  : snapshot.layout.kind === 'table' && row.type === 'dataRow'
                  ? styles.tableRow
                  : undefined
              }
            >
              <NativeRow
                row={row}
                snapshot={snapshot}
                itemIndex={itemIndex}
                selectedKeys={selectedKeys}
                onPress={() => activateSelection(row)}
                onAction={(key) => activateAction(row, key)}
              />
            </View>
          ))}
        </ScrollView>
        {snapshot.fixedFooter ? (
          <NativeRow
            row={snapshot.fixedFooter}
            snapshot={snapshot}
            selectedKeys={selectedKeys}
            onPress={() => activateSelection(snapshot.fixedFooter as RowModel)}
            onAction={(key) =>
              activateAction(snapshot.fixedFooter as RowModel, key)
            }
          />
        ) : null}
      </View>
    );
  }
);

const styles = StyleSheet.create({
  container: { flex: 1 },
  horizontal: { flexDirection: 'row' },
  grid: { flexDirection: 'row', flexWrap: 'wrap' },
  sectionBreak: { marginTop: 12 },
  tableRow: { minHeight: 52 },
  row: {
    minHeight: 56,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  sectionHeader: {
    minHeight: 44,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingHorizontal: 16,
    paddingVertical: 8,
  },
  summaryHeader: { minHeight: 56, paddingHorizontal: 20 },
  galleryHeader: { minHeight: 44, paddingHorizontal: 12 },
  historyHeader: { minHeight: 16, paddingHorizontal: 8, paddingVertical: 0 },
  galleryTitle: { fontSize: 16, fontWeight: '600' },
  historyTitle: { fontSize: 12, lineHeight: 16, fontWeight: '600' },
  action: {
    minHeight: 52,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
  },
  system: {
    minHeight: 52,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    padding: 12,
  },
  noMatchText: { textAlign: 'center' },
  rail: { width: 76, minHeight: 88, alignItems: 'center', gap: 6, padding: 8 },
  tile: { padding: 10, borderRadius: 16 },
  metricCard: {
    minHeight: 128,
    margin: 6,
    padding: 12,
    borderRadius: 12,
    gap: 5,
  },
  compositeCard: { padding: 14, borderRadius: 12, gap: 14 },
  compositeHeading: { fontSize: 14, letterSpacing: 1 },
  compositeRow: { flexDirection: 'row', gap: 12 },
  compositeMetric: { flex: 1, gap: 4 },
  metricValueRow: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  metricInlineVisual: {
    width: 16,
    height: 16,
    borderRadius: 8,
    overflow: 'hidden',
  },
  metricInlineImage: { width: 16, height: 16, borderRadius: 8 },
  compositeSubcard: {
    backgroundColor: '#F0F0F0',
    borderRadius: 10,
    padding: 10,
  },
  compositeValue: { fontSize: 18, fontWeight: '600' },
  compositeDivider: { height: StyleSheet.hairlineWidth },
  progressTrack: { height: 4, borderRadius: 2, overflow: 'hidden' },
  progressFill: { height: 4, borderRadius: 2 },
  metricValue: { fontSize: 22, fontWeight: '700' },
  tileImage: {
    width: '100%',
    aspectRatio: 1,
    borderRadius: 10,
    backgroundColor: '#0000000F',
  },
  tileImageEmpty: { backgroundColor: 'transparent' },
  tileImagePressed: { opacity: 0.8 },
  tileImageError: { alignItems: 'center', justifyContent: 'center' },
  tileImageErrorIcon: { width: 24, height: 24 },
  tileMetadata: { marginTop: 8 },
  tileSubtitleRow: { flexDirection: 'row', alignItems: 'center' },
  tileSubtitle: { flex: 1, minWidth: 0, paddingRight: 8, fontSize: 12 },
  tileNetworkImage: { width: 14, height: 14, borderRadius: 7 },
  tileTitle: { fontSize: 16, fontWeight: '500' },
  image: { width: 40, height: 40, borderRadius: 20 },
  imageFrame: { width: 40, height: 40 },
  imagePlaceholder: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: '#E5E7EB',
  },
  cornerImageRing: {
    position: 'absolute',
    right: -4,
    bottom: -4,
    width: 20,
    height: 20,
    padding: 2,
    borderRadius: 10,
  },
  cornerImage: { width: 16, height: 16, borderRadius: 8 },
  flex: { flex: 1, minWidth: 0 },
  tertiaryRow: { minHeight: 72 },
  title: { fontSize: 15, fontWeight: '600' },
  sectionTitle: { fontSize: 13, fontWeight: '700' },
  summaryTitle: { fontSize: 16, fontWeight: '500' },
  railTitle: { fontSize: 12, fontWeight: '600', textAlign: 'center' },
  secondary: { fontSize: 13, color: '#6B7280' },
  value: { fontSize: 14, fontWeight: '500' },
  summaryValue: { fontSize: 16, fontWeight: '500' },
  time: { fontSize: 11, alignSelf: 'flex-start' },
  amounts: { alignItems: 'flex-end' },
  actions: { flexDirection: 'row', gap: 16, marginTop: 8 },
  actionLabel: { fontSize: 12, fontWeight: '600' },
  badge: { fontSize: 10, color: '#2F6BFF' },
  close: {
    position: 'absolute',
    right: 4,
    top: -164,
    fontSize: 22,
    color: '#111111',
  },
  unread: { width: 7, height: 7, borderRadius: 4 },
  checkbox: {
    width: 20,
    height: 20,
    borderWidth: 2,
    borderRadius: 4,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkboxCheck: {
    width: 7,
    height: 11,
    marginTop: -2,
    borderRightWidth: 2,
    borderBottomWidth: 2,
    transform: [{ rotate: '45deg' }],
  },
  checkboxIndeterminate: { width: 8, height: 2, borderRadius: 1 },
  disabled: { opacity: 0.5 },
  index: { width: 28, color: '#6B7280' },
  dataCell: { minWidth: 0 },
  dataCellStart: { alignItems: 'flex-start' },
  dataCellCenter: { alignItems: 'center' },
  dataCellEnd: { alignItems: 'flex-end' },
  dataPrimaryLine: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  dataPrimary: { fontSize: 16, fontWeight: '500' },
  dataBadge: {
    color: '#0D74CE',
    backgroundColor: '#E6F4FE',
    borderRadius: 4,
    paddingHorizontal: 5,
    fontSize: 13,
  },
  favorite: { width: 24, fontSize: 24, color: '#8D8D8D' },
});
