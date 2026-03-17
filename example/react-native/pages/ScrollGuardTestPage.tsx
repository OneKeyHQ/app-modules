import React, { useState } from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import PagerView, { type PagerViewOnPageSelectedEvent } from '@onekeyfe/react-native-pager-view';
import { ScrollGuardView } from '@onekeyfe/react-native-scroll-guard';
import { TestButton, TestPageBase } from './TestPageBase';

const PAGER_PAGES = [
  { key: 'market', title: 'Market', color: '#E8F1FF' },
  { key: 'earn', title: 'Earn', color: '#EAF9F1' },
  { key: 'browser', title: 'Browser', color: '#FFF6E5' },
] as const;

const BANNER_ITEMS = Array.from({ length: 8 }, (_, i) => ({
  key: `banner-${i}`,
  title: `Category ${i + 1}`,
  value: `+${(Math.random() * 10).toFixed(2)}%`,
  color: `hsl(${i * 45}, 60%, 92%)`,
}));

function BannerCard({ title, value, color }: { title: string; value: string; color: string }) {
  return (
    <View style={[styles.bannerCard, { backgroundColor: color }]}>
      <Text style={styles.bannerTitle}>{title}</Text>
      <Text style={styles.bannerValue}>{value}</Text>
    </View>
  );
}

function HorizontalBanner({ guarded }: { guarded: boolean }) {
  const content = (
    <ScrollView
      horizontal
      showsHorizontalScrollIndicator={false}
      bounces={false}
      contentContainerStyle={styles.bannerContainer}
    >
      {BANNER_ITEMS.map((item) => (
        <BannerCard key={item.key} title={item.title} value={item.value} color={item.color} />
      ))}
    </ScrollView>
  );

  if (guarded) {
    return (
      <ScrollGuardView direction="horizontal" style={styles.guardWrapper}>
        {content}
      </ScrollGuardView>
    );
  }

  return content;
}

export function ScrollGuardTestPage() {
  const [currentPage, setCurrentPage] = useState(0);
  const [guardEnabled, setGuardEnabled] = useState(true);

  const handlePageSelected = (event: PagerViewOnPageSelectedEvent) => {
    setCurrentPage(event.nativeEvent.position);
  };

  return (
    <TestPageBase title="Scroll Guard Test">
      <View style={styles.headerCard}>
        <Text style={styles.headerTitle}>Horizontal ScrollView inside PagerView</Text>
        <Text style={styles.headerSubtitle}>
          The banner area contains a horizontal ScrollView. Without ScrollGuard,
          scrolling to the edge of the banner triggers the outer PagerView to swipe.
        </Text>
      </View>

      <View style={styles.statusRow}>
        <View style={styles.statusItem}>
          <Text style={styles.statusLabel}>Current Page</Text>
          <Text style={styles.statusValue}>{PAGER_PAGES[currentPage]?.title}</Text>
        </View>
        <View style={styles.statusItem}>
          <Text style={styles.statusLabel}>ScrollGuard</Text>
          <Text style={[styles.statusValue, { color: guardEnabled ? '#34C759' : '#FF3B30' }]}>
            {guardEnabled ? 'ON' : 'OFF'}
          </Text>
        </View>
      </View>

      <TestButton
        title={guardEnabled ? 'Disable ScrollGuard' : 'Enable ScrollGuard'}
        onPress={() => setGuardEnabled((prev) => !prev)}
      />

      <PagerView
        style={styles.pagerView}
        initialPage={0}
        overdrag
        overScrollMode="always"
        onPageSelected={handlePageSelected}
      >
        {PAGER_PAGES.map((page) => (
          <View key={page.key} style={[styles.page, { backgroundColor: page.color }]}>
            <Text style={styles.pageTitle}>{page.title}</Text>

            <View style={styles.bannerSection}>
              <Text style={styles.sectionLabel}>
                {guardEnabled ? 'Guarded Banner (scroll freely)' : 'Unguarded Banner (triggers pager at edge)'}
              </Text>
              <HorizontalBanner guarded={guardEnabled} />
            </View>

            <Text style={styles.pageHint}>
              Scroll the banner to the edge, then keep swiping.{'\n'}
              {guardEnabled
                ? 'With ScrollGuard: pager stays on this page.'
                : 'Without ScrollGuard: pager swipes to next page.'}
            </Text>
          </View>
        ))}
      </PagerView>

      <View style={styles.instructionCard}>
        <Text style={styles.instructionTitle}>How to test</Text>
        <Text style={styles.instructionText}>
          1. Scroll the banner cards horizontally to the right edge{'\n'}
          2. Keep swiping left — observe if the outer pager switches{'\n'}
          3. Toggle ScrollGuard OFF and repeat to see the difference{'\n'}
          4. Verify vertical scrolling still works (scroll this page)
        </Text>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  headerCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    gap: 8,
  },
  headerTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#111111',
  },
  headerSubtitle: {
    fontSize: 13,
    color: '#666666',
    lineHeight: 18,
  },
  statusRow: {
    flexDirection: 'row',
    gap: 12,
  },
  statusItem: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    alignItems: 'center',
    gap: 4,
  },
  statusLabel: {
    fontSize: 12,
    color: '#8E8E93',
    fontWeight: '500',
  },
  statusValue: {
    fontSize: 18,
    fontWeight: '700',
    color: '#111111',
  },
  pagerView: {
    height: 360,
    borderRadius: 12,
    overflow: 'hidden',
  },
  page: {
    flex: 1,
    paddingHorizontal: 16,
    paddingVertical: 20,
    gap: 12,
  },
  pageTitle: {
    fontSize: 22,
    fontWeight: '700',
    color: '#111111',
    textAlign: 'center',
  },
  bannerSection: {
    gap: 8,
  },
  sectionLabel: {
    fontSize: 13,
    fontWeight: '600',
    color: '#555555',
  },
  guardWrapper: {
    // ScrollGuardView needs no special styling — it's a transparent wrapper
  },
  bannerContainer: {
    paddingVertical: 8,
    paddingHorizontal: 4,
    gap: 10,
  },
  bannerCard: {
    width: 120,
    height: 80,
    borderRadius: 12,
    padding: 12,
    justifyContent: 'space-between',
  },
  bannerTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333333',
  },
  bannerValue: {
    fontSize: 16,
    fontWeight: '700',
    color: '#34C759',
  },
  pageHint: {
    fontSize: 12,
    color: '#444444',
    textAlign: 'center',
    lineHeight: 18,
    marginTop: 4,
  },
  instructionCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    gap: 8,
  },
  instructionTitle: {
    fontSize: 15,
    fontWeight: '700',
    color: '#111111',
  },
  instructionText: {
    fontSize: 13,
    color: '#555555',
    lineHeight: 20,
  },
});
