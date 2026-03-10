import React, { useCallback, useRef, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import PagerView, { type PagerViewOnPageSelectedEvent } from '@onekeyfe/react-native-pager-view';
import { TestButton, TestPageBase } from './TestPageBase';

const PAGES = [
  { key: 'welcome', title: 'Welcome', subtitle: 'Swipe or use buttons to switch pages', color: '#E8F1FF' },
  { key: 'native', title: 'Native Pager', subtitle: 'This page is rendered by native ViewPager/UIPageViewController', color: '#EAF9F1' },
  { key: 'release', title: 'Release Ready', subtitle: 'Use this page to verify compile and runtime behaviors', color: '#FFF6E5' },
] as const;

function clampPage(index: number) {
  return Math.max(0, Math.min(index, PAGES.length - 1));
}

export function PagerViewTestPage() {
  const pagerRef = useRef<PagerView>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [scrollEnabled, setScrollEnabled] = useState(true);

  const goToPage = useCallback((nextPage: number) => {
    const safePage = clampPage(nextPage);
    pagerRef.current?.setPage(safePage);
    setCurrentPage(safePage);
  }, []);

  const handlePageSelected = useCallback((event: PagerViewOnPageSelectedEvent) => {
    setCurrentPage(event.nativeEvent.position);
  }, []);

  const handleToggleScroll = useCallback(() => {
    const next = !scrollEnabled;
    setScrollEnabled(next);
    pagerRef.current?.setScrollEnabled(next);
  }, [scrollEnabled]);

  return (
    <TestPageBase title="Pager View Test">
      <View style={styles.headerCard}>
        <Text style={styles.headerTitle}>Current Page: {currentPage + 1}/{PAGES.length}</Text>
        <Text style={styles.headerSubtitle}>Scroll Enabled: {scrollEnabled ? 'Yes' : 'No'}</Text>
      </View>

      <PagerView
        ref={pagerRef}
        style={styles.pagerView}
        initialPage={0}
        scrollEnabled={scrollEnabled}
        overScrollMode="never"
        onPageSelected={handlePageSelected}
      >
        {PAGES.map((page) => (
          <View key={page.key} style={[styles.page, { backgroundColor: page.color }]}>
            <Text style={styles.pageTitle}>{page.title}</Text>
            <Text style={styles.pageSubtitle}>{page.subtitle}</Text>
          </View>
        ))}
      </PagerView>

      <View style={styles.buttonsRow}>
        <TestButton title="Prev" onPress={() => goToPage(currentPage - 1)} disabled={currentPage === 0} style={styles.flexButton} />
        <TestButton title="Next" onPress={() => goToPage(currentPage + 1)} disabled={currentPage === PAGES.length - 1} style={styles.flexButton} />
      </View>

      <TestButton
        title={scrollEnabled ? 'Disable Swipe' : 'Enable Swipe'}
        onPress={handleToggleScroll}
      />
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
    fontWeight: '600',
    color: '#111111',
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#666666',
  },
  pagerView: {
    height: 280,
    borderRadius: 12,
    overflow: 'hidden',
  },
  page: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  pageTitle: {
    fontSize: 24,
    fontWeight: '700',
    color: '#111111',
  },
  pageSubtitle: {
    fontSize: 14,
    color: '#333333',
    marginTop: 10,
    textAlign: 'center',
    lineHeight: 20,
  },
  buttonsRow: {
    flexDirection: 'row',
    gap: 12,
  },
  flexButton: {
    flex: 1,
  },
});
