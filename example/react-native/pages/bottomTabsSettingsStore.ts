import type { BottomTabsSettingsState } from '../route';

type Listener = () => void;

let state: BottomTabsSettingsState = {
  showBadge: true,
  labeled: true,
  translucent: true,
  tabBarHidden: false,
  sidebarAdaptable: false,
  hapticFeedback: false,
  androidFloating: true,
};

const listeners = new Set<Listener>();

export function getBottomTabsSettings() {
  return state;
}

export function setBottomTabsSettings(partial: Partial<BottomTabsSettingsState>) {
  state = { ...state, ...partial };
  listeners.forEach(l => l());
}

export function subscribeBottomTabsSettings(listener: Listener) {
  listeners.add(listener);
  return () => { listeners.delete(listener); };
}
