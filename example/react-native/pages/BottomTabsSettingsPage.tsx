import React, { useSyncExternalStore } from 'react';
import { View, Text, StyleSheet, Switch, ScrollView } from 'react-native';
import type { BottomTabsSettingsState } from '../route';
import { getBottomTabsSettings, setBottomTabsSettings, subscribeBottomTabsSettings } from './bottomTabsSettingsStore';

export function BottomTabsSettingsPage() {
  const settings = useSyncExternalStore(subscribeBottomTabsSettings, getBottomTabsSettings);

  const toggle = (key: keyof BottomTabsSettingsState) => () => {
    setBottomTabsSettings({ [key]: !settings[key] });
  };

  const items: { label: string; key: keyof BottomTabsSettingsState }[] = [
    { label: 'Show Badge', key: 'showBadge' },
    { label: 'Labeled', key: 'labeled' },
    { label: 'Translucent', key: 'translucent' },
    { label: 'Tab Bar Hidden', key: 'tabBarHidden' },
    { label: 'Sidebar Adaptable', key: 'sidebarAdaptable' },
    { label: 'Haptic Feedback', key: 'hapticFeedback' },
    { label: 'Android Floating Tab Bar', key: 'androidFloating' },
  ];

  return (
    <ScrollView style={styles.container}>
      <View style={styles.section}>
        {items.map((item, index) => (
          <View
            key={item.label}
            style={[styles.row, index < items.length - 1 && styles.rowBorder]}
          >
            <Text style={styles.label}>{item.label}</Text>
            <Switch value={settings[item.key]} onValueChange={toggle(item.key)} />
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 16,
  },
  section: {
    backgroundColor: '#fff',
    borderRadius: 10,
    overflow: 'hidden',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 16,
  },
  rowBorder: {
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#E5E5EA',
  },
  label: {
    fontSize: 16,
    color: '#000',
  },
});
