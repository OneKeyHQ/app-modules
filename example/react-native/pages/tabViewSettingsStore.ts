import type { TabViewSettingsState } from '../route';

type Listener = () => void;

let state: TabViewSettingsState = {
  showBadge: true,
  labeled: true,
  translucent: true,
  tabBarHidden: false,
  sidebarAdaptable: false,
  hapticFeedback: false,
};

const listeners = new Set<Listener>();

export function getTabViewSettings() {
  return state;
}

export function setTabViewSettings(partial: Partial<TabViewSettingsState>) {
  state = { ...state, ...partial };
  listeners.forEach(l => l());
}

export function subscribeTabViewSettings(listener: Listener) {
  listeners.add(listener);
  return () => { listeners.delete(listener); };
}
