import { Image, Platform, type ImageRequireSource } from 'react-native';
import type {
  IdentityRow,
  LeadingVisual,
  NativeListSnapshot,
  NativeListTheme,
  ReorderEvent,
  RowModel,
} from '@onekeyfe/react-native-native-list';

// Wallet sidebar captured from the OK-62492 QA recording (iPhone, iOS, light
// theme). Order, names, avatars, the QR badge, and hidden-wallet groups follow
// the video; names the recording truncates are padded so they truncate at the
// same width. Rows are built like app-monorepo's
// AccountSelectorWalletListSideBarV2, so the example replays the production
// snapshot without a wallet database.

// Copied from app-monorepo packages/shared/src/assets/wallet/avatar (923d2f36b8).
const avatarSources = {
  bear: require('./nativeListWalletSidebarAssets/bear.png'),
  classic: require('./nativeListWalletSidebarAssets/classic.png'),
  cow: require('./nativeListWalletSidebarAssets/cow.png'),
  dog: require('./nativeListWalletSidebarAssets/dog.png'),
  fox: require('./nativeListWalletSidebarAssets/fox.png'),
  frog: require('./nativeListWalletSidebarAssets/frog.png'),
  koala: require('./nativeListWalletSidebarAssets/koala.png'),
  lion: require('./nativeListWalletSidebarAssets/lion.png'),
  monkey: require('./nativeListWalletSidebarAssets/monkey.png'),
  othersImported: require('./nativeListWalletSidebarAssets/othersImported.png'),
  othersWatching: require('./nativeListWalletSidebarAssets/othersWatching.png'),
  panda: require('./nativeListWalletSidebarAssets/panda.png'),
  polarBear: require('./nativeListWalletSidebarAssets/polarBear.png'),
  proBlack: require('./nativeListWalletSidebarAssets/proBlack.png'),
  rabbit: require('./nativeListWalletSidebarAssets/rabbit.png'),
} satisfies Record<string, ImageRequireSource>;

export type WalletSidebarAvatar = keyof typeof avatarSources;

export type WalletSidebarWalletKind =
  | 'hd'
  | 'hw'
  | 'hwHidden'
  | 'qr'
  | 'imported'
  | 'watching';

export type WalletSidebarWallet = Readonly<{
  id: string;
  name: string;
  kind: WalletSidebarWalletKind;
  avatar: WalletSidebarAvatar;
  // walletListUtils.ts badges QR wallets with "QR".
  badge?: string;
  hiddenWallets?: readonly WalletSidebarWallet[];
  // Resolved result of shouldShowCreateHiddenWalletSidebarButtonForWallet().
  showAddHiddenWalletButton?: boolean;
}>;

export type WalletSidebarSnapshotInput = Readonly<{
  wallets: readonly WalletSidebarWallet[];
  focusedWalletId?: string;
  theme?: NativeListTheme;
}>;

export const OK_62492_FOCUSED_WALLET_ID = 'hd-ran';

// Order at 00:00. The QA first drags "然" below "Private key", then from 00:13
// drags the "abandon" group up past "然 1s" and the QR wallet, where "然 1s"
// loses its name and "OneKey Pro" separates from its avatar.
export const OK_62492_WALLETS: readonly WalletSidebarWallet[] = [
  { id: 'hd-research', name: '研发', kind: 'hd', avatar: 'bear' },
  { id: 'hd-ran', name: '然', kind: 'hd', avatar: 'lion' },
  {
    id: 'watching',
    name: 'Watch-Only',
    kind: 'watching',
    avatar: 'othersWatching',
  },
  { id: 'hd-product', name: '产品', kind: 'hd', avatar: 'rabbit' },
  { id: 'hd-12-words', name: '12 助记词', kind: 'hd', avatar: 'panda' },
  { id: 'hd-wallet-25', name: 'Wallet 25', kind: 'hd', avatar: 'polarBear' },
  { id: 'hd-wallet-12', name: 'Wallet 12', kind: 'hd', avatar: 'fox' },
  { id: 'hd-wallet-10', name: 'Wallet 10', kind: 'hd', avatar: 'fox' },
  { id: 'hd-wallet-18', name: 'Wallet 18', kind: 'hd', avatar: 'monkey' },
  { id: 'hd-wallet-19', name: 'Wallet 19', kind: 'hd', avatar: 'cow' },
  { id: 'hd-wallet-26', name: 'Wallet 26', kind: 'hd', avatar: 'dog' },
  { id: 'hd-haohaohao', name: 'haohaohaohao', kind: 'hd', avatar: 'frog' },
  { id: 'hd-wallet-13', name: 'Wallet 13', kind: 'hd', avatar: 'lion' },
  { id: 'hd-wallet-2', name: 'Wallet 2', kind: 'hd', avatar: 'polarBear' },
  { id: 'hw-qa', name: 'qa', kind: 'hw', avatar: 'proBlack' },
  {
    id: 'qr-onekey-pro',
    name: 'OneKey Pro QR',
    kind: 'qr',
    avatar: 'proBlack',
    badge: 'QR',
  },
  {
    id: 'hw-ran-1s',
    name: '然 1s',
    kind: 'hw',
    avatar: 'classic',
    showAddHiddenWalletButton: true,
  },
  {
    id: 'hw-abandon',
    name: 'abandon',
    kind: 'hw',
    avatar: 'proBlack',
    showAddHiddenWalletButton: true,
  },
  {
    id: 'imported',
    name: 'Private key',
    kind: 'imported',
    avatar: 'othersImported',
  },
  { id: 'hd-wallet-20', name: 'Wallet 20', kind: 'hd', avatar: 'koala' },
];

// useAccountSelectorNativeListThemeV2(true) resolved with app-monorepo light tokens.
export const WALLET_SIDEBAR_LIGHT_THEME: NativeListTheme = {
  background: '#f9f9f9',
  rowBackground: '#f9f9f9',
  rowSelectedBackground: '#00000017',
  rowPressedBackground: '#00000017',
  subduedBackground: '#f9f9f9',
  strongBackground: '#0000000f',
  primaryText: '#000000df',
  secondaryText: '#0000009b',
  disabledText: '#00000072',
  icon: '#0000009b',
  iconSubdued: '#00000072',
  separator: '#0000001f',
  accent: '#000000df',
  positive: '#00713fde',
  negative: '#c40006d3',
  inverseBackground: '#000000df',
  inverseText: '#ffffffed',
  info: '#006dcbf2',
  caution: '#9e6c00',
  cautionBackground: '#f4dd0016',
};

const WALLET_ROW_HEIGHT = 68;
const HIDDEN_WALLET_TITLE = 'Hidden wallet';
const ADD_HIDDEN_WALLET_KEY_PREFIX = 'add-hidden:';

type WalletOverlay = NonNullable<
  Extract<LeadingVisual, { kind: 'wallet' }>['overlays']
>[number];

export function walletSidebarAvatarUri(avatar: WalletSidebarAvatar): string {
  const uri = Image.resolveAssetSource(avatarSources[avatar])?.uri ?? '';
  // Release Android builds resolve bundled images to drawable resource names.
  if (Platform.OS === 'android' && uri && !uri.includes(':')) {
    return `android.resource://com.example/drawable/${uri}`;
  }
  return uri;
}

function walletVisual(
  wallet: WalletSidebarWallet,
  theme: NativeListTheme,
  badge: string | number | undefined,
): LeadingVisual {
  const overlays: WalletOverlay[] = [];
  if (badge !== undefined) {
    overlays.push({
      position: 'bottomRight',
      height: 16,
      offsetX: 1,
      offsetY: 2,
      text: String(badge),
      tintColor: theme.primaryText,
      backgroundColor: theme.subduedBackground,
    });
  }
  if (wallet.kind === 'hwHidden') {
    return {
      kind: 'wallet',
      backgroundColor: '#00000000',
      fallbackIcon: { name: 'LockSolid', tintColor: theme.icon },
      overlays,
    };
  }
  return {
    kind: 'wallet',
    shape: 'square',
    backgroundColor: '#00000000',
    image: {
      uri: walletSidebarAvatarUri(wallet.avatar),
      width: 40,
      height: 40,
      contentFit: 'cover',
      retryTimes: 1,
    },
    fallbackText: '',
    overlays,
  };
}

function walletIdentityRow(
  wallet: WalletSidebarWallet,
  focusedWalletId: string | undefined,
  theme: NativeListTheme,
  badge?: number,
): IdentityRow {
  return {
    type: 'identity',
    key: wallet.id,
    testID: `wallet-${wallet.id}`,
    presentation: 'walletSidebar',
    height: WALLET_ROW_HEIGHT,
    title: wallet.name,
    leading: walletVisual(wallet, theme, badge ?? wallet.badge),
    selected: focusedWalletId === wallet.id,
    opacity: 1,
    badges: [],
    draggable: true,
    accessibilityLabel: wallet.name,
  };
}

function addHiddenWalletRow(
  wallet: WalletSidebarWallet,
  theme: NativeListTheme,
): IdentityRow {
  return {
    type: 'identity',
    key: `${ADD_HIDDEN_WALLET_KEY_PREFIX}${wallet.id}`,
    presentation: 'walletSidebar',
    height: WALLET_ROW_HEIGHT,
    title: HIDDEN_WALLET_TITLE,
    leading: {
      kind: 'wallet',
      backgroundColor: '#00000000',
      shape: 'circle',
      borderStyle: 'dashed',
      borderColor: theme.separator,
      fallbackIcon: { name: 'PlusSmallOutline', tintColor: theme.iconSubdued },
    },
    draggable: false,
  };
}

export function buildWalletSidebarRows({
  wallets,
  focusedWalletId,
  theme = WALLET_SIDEBAR_LIGHT_THEME,
}: WalletSidebarSnapshotInput): RowModel[] {
  return wallets.map((wallet): RowModel => {
    const parent = walletIdentityRow(wallet, focusedWalletId, theme);
    const isHwOrQrWallet = wallet.kind === 'hw' || wallet.kind === 'qr';
    const childWallets = isHwOrQrWallet ? wallet.hiddenWallets ?? [] : [];
    const hasGroup =
      wallet.kind !== 'hwHidden' &&
      (childWallets.length > 0 || wallet.showAddHiddenWalletButton === true);
    if (!hasGroup) return parent;
    // Phones use the md layout, which numbers hidden wallets inside a device group.
    const children = childWallets.map((child, index) =>
      walletIdentityRow(child, focusedWalletId, theme, index + 1),
    );
    if (wallet.showAddHiddenWalletButton) {
      children.push(addHiddenWalletRow(wallet, theme));
    }
    return {
      type: 'walletGroup',
      key: wallet.id,
      parent,
      children,
      draggable: true,
    };
  });
}

export function buildWalletSidebarSnapshot(
  input: WalletSidebarSnapshotInput,
): NativeListSnapshot {
  return {
    schemaVersion: 1,
    generation: 1,
    theme: input.theme ?? WALLET_SIDEBAR_LIGHT_THEME,
    layout: {
      kind: 'linear',
      contentPaddingHorizontal: 8,
      contentPaddingTop: 8,
      contentPaddingBottom: 8,
      itemSpacing: 12,
    },
    rows: buildWalletSidebarRows(input),
    capabilities: { reorderable: true },
  };
}

export function parseHiddenWalletActionKey(rowKey: string): string | undefined {
  return rowKey.startsWith(ADD_HIDDEN_WALLET_KEY_PREFIX)
    ? rowKey.slice(ADD_HIDDEN_WALLET_KEY_PREFIX.length)
    : undefined;
}

// Same move as the production sidebar's onReorder handler.
export function reorderWalletSidebarWallets(
  wallets: readonly WalletSidebarWallet[],
  event: ReorderEvent,
): readonly WalletSidebarWallet[] {
  const fromIndex = wallets.findIndex(wallet => wallet.id === event.key);
  if (fromIndex < 0) return wallets;
  const reordered = [...wallets];
  const [moved] = reordered.splice(fromIndex, 1);
  if (!moved) return wallets;
  const toIndex = Math.max(0, Math.min(event.toIndex, reordered.length));
  reordered.splice(toIndex, 0, moved);
  return reordered;
}
