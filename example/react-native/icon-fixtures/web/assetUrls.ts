// SDK example and test fixtures only; the App owns the complete catalog.
const urls: Record<string, () => string> = {
  AccountErrorCustom: () =>
    new URL('./assets/AccountErrorCustom.svg', import.meta.url).href,
  AllNetworksSolid: () =>
    new URL('./assets/AllNetworksSolid.svg', import.meta.url).href,
  AppleBrand: () => new URL('./assets/AppleBrand.svg', import.meta.url).href,
  ArrowBottomOutline: () =>
    new URL('./assets/ArrowBottomOutline.svg', import.meta.url).href,
  ArrowTopOutline: () =>
    new URL('./assets/ArrowTopOutline.svg', import.meta.url).href,
  ArrowTopRightIllus: () =>
    new URL('./assets/ArrowTopRightIllus.svg', import.meta.url).href,
  BadgeRecognizedSolid: () =>
    new URL('./assets/BadgeRecognizedSolid.svg', import.meta.url).href,
  BadgeVerifiedSolid: () =>
    new URL('./assets/BadgeVerifiedSolid.svg', import.meta.url).href,
  BotIllus: () => new URL('./assets/BotIllus.svg', import.meta.url).href,
  ChartPositionOutline: () =>
    new URL('./assets/ChartPositionOutline.svg', import.meta.url).href,
  ChartTrendingUpOutline: () =>
    new URL('./assets/ChartTrendingUpOutline.svg', import.meta.url).href,
  CheckboxCheckedCustom: () =>
    new URL('./assets/CheckboxCheckedCustom.svg', import.meta.url).href,
  CheckboxIndeterminateCustom: () =>
    new URL('./assets/CheckboxIndeterminateCustom.svg', import.meta.url).href,
  ChevronBottomOutline: () =>
    new URL('./assets/ChevronBottomOutline.svg', import.meta.url).href,
  ChevronGrabberVerOutline: () =>
    new URL('./assets/ChevronGrabberVerOutline.svg', import.meta.url).href,
  ChevronRightSmallOutline: () =>
    new URL('./assets/ChevronRightSmallOutline.svg', import.meta.url).href,
  ChevronTopOutline: () =>
    new URL('./assets/ChevronTopOutline.svg', import.meta.url).href,
  CoinsIllus: () => new URL('./assets/CoinsIllus.svg', import.meta.url).href,
  CoinsOutline: () =>
    new URL('./assets/CoinsOutline.svg', import.meta.url).href,
  Copy3Outline: () =>
    new URL('./assets/Copy3Outline.svg', import.meta.url).href,
  CrossedSmallSolid: () =>
    new URL('./assets/CrossedSmallSolid.svg', import.meta.url).href,
  CryptoCoinOutline: () =>
    new URL('./assets/CryptoCoinOutline.svg', import.meta.url).href,
  Document2Outline: () =>
    new URL('./assets/Document2Outline.svg', import.meta.url).href,
  DotHorOutline: () =>
    new URL('./assets/DotHorOutline.svg', import.meta.url).href,
  DotVerSolid: () => new URL('./assets/DotVerSolid.svg', import.meta.url).href,
  DragOutline: () => new URL('./assets/DragOutline.svg', import.meta.url).href,
  EditOutline: () => new URL('./assets/EditOutline.svg', import.meta.url).href,
  ErrorSolid: () => new URL('./assets/ErrorSolid.svg', import.meta.url).href,
  GasSolid: () => new URL('./assets/GasSolid.svg', import.meta.url).href,
  GlobusOutline: () =>
    new URL('./assets/GlobusOutline.svg', import.meta.url).href,
  GoogleIllus: () => new URL('./assets/GoogleIllus.svg', import.meta.url).href,
  GooglePayIllus: () =>
    new URL('./assets/GooglePayIllus.svg', import.meta.url).href,
  ImageSquareWavesOutline: () =>
    new URL('./assets/ImageSquareWavesOutline.svg', import.meta.url).href,
  InfoCircleOutline: () =>
    new URL('./assets/InfoCircleOutline.svg', import.meta.url).href,
  LimitOrderOutline: () =>
    new URL('./assets/LimitOrderOutline.svg', import.meta.url).href,
  LockSolid: () => new URL('./assets/LockSolid.svg', import.meta.url).href,
  MaticCircleIllus: () =>
    new URL('./assets/MaticCircleIllus.svg', import.meta.url).href,
  MinusCircleOutline: () =>
    new URL('./assets/MinusCircleOutline.svg', import.meta.url).href,
  MinusCircleSolid: () =>
    new URL('./assets/MinusCircleSolid.svg', import.meta.url).href,
  OnekeyPrimeDarkColored: () =>
    new URL('./assets/OnekeyPrimeDarkColored.svg', import.meta.url).href,
  OnekeyPrimeLightColored: () =>
    new URL('./assets/OnekeyPrimeLightColored.svg', import.meta.url).href,
  OnekeyTextIllus: () =>
    new URL('./assets/OnekeyTextIllus.svg', import.meta.url).href,
  PassKeySolid: () =>
    new URL('./assets/PassKeySolid.svg', import.meta.url).href,
  PencilOutline: () =>
    new URL('./assets/PencilOutline.svg', import.meta.url).href,
  PlusCircleOutline: () =>
    new URL('./assets/PlusCircleOutline.svg', import.meta.url).href,
  PlusLargeOutline: () =>
    new URL('./assets/PlusLargeOutline.svg', import.meta.url).href,
  PlusSmallOutline: () =>
    new URL('./assets/PlusSmallOutline.svg', import.meta.url).href,
  QuestionmarkSolid: () =>
    new URL('./assets/QuestionmarkSolid.svg', import.meta.url).href,
  RefreshCcwOutline: () =>
    new URL('./assets/RefreshCcwOutline.svg', import.meta.url).href,
  ShieldCheckDoneOutline: () =>
    new URL('./assets/ShieldCheckDoneOutline.svg', import.meta.url).href,
  ShieldDeviceDarkIllus: () =>
    new URL('./assets/ShieldDeviceDarkIllus.svg', import.meta.url).href,
  ShieldExclamationOutline: () =>
    new URL('./assets/ShieldExclamationOutline.svg', import.meta.url).href,
  ShortcutsCustom: () =>
    new URL('./assets/ShortcutsCustom.svg', import.meta.url).href,
  SidebarLeftArrowCustom: () =>
    new URL('./assets/SidebarLeftArrowCustom.svg', import.meta.url).href,
  SpeakerPromoteOutline: () =>
    new URL('./assets/SpeakerPromoteOutline.svg', import.meta.url).href,
  StarOutline: () => new URL('./assets/StarOutline.svg', import.meta.url).href,
  StarSolid: () => new URL('./assets/StarSolid.svg', import.meta.url).href,
  SwapHorOutline: () =>
    new URL('./assets/SwapHorOutline.svg', import.meta.url).href,
  SwitchHorOutline: () =>
    new URL('./assets/SwitchHorOutline.svg', import.meta.url).href,
  TicketOutline: () =>
    new URL('./assets/TicketOutline.svg', import.meta.url).href,
  UnlockedOutline: () =>
    new URL('./assets/UnlockedOutline.svg', import.meta.url).href,
  UserAvatarFallbackColored: () =>
    new URL('./assets/UserAvatarFallbackColored.svg', import.meta.url).href,
  WalletOutline: () =>
    new URL('./assets/WalletOutline.svg', import.meta.url).href,
};

export function iconResourceHref(name: string): string | undefined {
  const load = Object.prototype.hasOwnProperty.call(urls, name)
    ? urls[name]
    : undefined;
  return load ? load() + '#onekey-' + name : undefined;
}
