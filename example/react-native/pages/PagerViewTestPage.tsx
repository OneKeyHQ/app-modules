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
  const [nestedLevel1Index, setNestedLevel1Index] = useState(0);
  const [nestedLevel2LeftIndex, setNestedLevel2LeftIndex] = useState(0);
  const [nestedLevel2RightIndex, setNestedLevel2RightIndex] = useState(0);
  const [nestedLevel3Index, setNestedLevel3Index] = useState(0);

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

      <View style={styles.nestedHeaderCard}>
        <Text style={styles.nestedHeaderTitle}>Nested PagerView (3 Levels)</Text>
        <Text style={styles.nestedHeaderSubtitle}>
          Level 1: horizontal, Level 2: horizontal, Level 3: horizontal
        </Text>
        <Text style={styles.nestedStateText}>
          L1 {nestedLevel1Index + 1}/2 · L2A {nestedLevel2LeftIndex + 1}/2 · L2B {nestedLevel2RightIndex + 1}/2 · L3 {nestedLevel3Index + 1}/2
        </Text>
      </View>

      <PagerView
        style={styles.nestedLevel1Pager}
        initialPage={0}
        overScrollMode="never"
        scrollSensitivity={4}
        onPageSelected={(event) => setNestedLevel1Index(event.nativeEvent.position)}
      >
        <View key="nested-level1-left" style={[styles.nestedLevel1Page, styles.nestedLevel1LeftPage]}>
          <Text style={styles.nestedLevel1Title}>Level 1 / Page A</Text>
          <Text style={styles.nestedHintText}>Swipe left/right to switch this level.</Text>

          <PagerView
            style={styles.nestedLevel2Pager}
            initialPage={0}
            overScrollMode="never"
            nestedScrollEnabled
            scrollSensitivity={4}
            onPageSelected={(event) => setNestedLevel2LeftIndex(event.nativeEvent.position)}
          >
            <View key="nested-level2-a-top" style={[styles.nestedLevel2Page, styles.nestedLevel2ATopPage]}>
              <Text style={styles.nestedLevel2Title}>Level 2A / Page 1</Text>
              <Text style={styles.nestedHintText}>Still horizontal here, then enter level 3.</Text>

              <PagerView
                style={styles.nestedLevel3Pager}
                initialPage={0}
                overScrollMode="never"
                nestedScrollEnabled
                scrollSensitivity={4}
                onPageSelected={(event) => setNestedLevel3Index(event.nativeEvent.position)}
              >
                <View key="nested-level3-1" style={[styles.nestedLevel3Page, styles.nestedLevel3PageOne]}>
                  <Text style={styles.nestedLevel3Title}>Level 3 / Card 1</Text>
                  <Text style={styles.nestedHintText}>Innermost pager: swipe left/right.</Text>
                </View>
                <View key="nested-level3-2" style={[styles.nestedLevel3Page, styles.nestedLevel3PageTwo]}>
                  <Text style={styles.nestedLevel3Title}>Level 3 / Card 2</Text>
                  <Text style={styles.nestedHintText}>Three-level nesting works in one screen.</Text>
                </View>
              </PagerView>
            </View>

            <View key="nested-level2-a-bottom" style={[styles.nestedLevel2Page, styles.nestedLevel2ABottomPage]}>
              <Text style={styles.nestedLevel2Title}>Level 2A / Page 2</Text>
              <Text style={styles.nestedHintText}>All three levels are horizontal in this demo.</Text>
            </View>
          </PagerView>
        </View>

        <View key="nested-level1-right" style={[styles.nestedLevel1Page, styles.nestedLevel1RightPage]}>
          <Text style={styles.nestedLevel1Title}>Level 1 / Page B</Text>
          <Text style={styles.nestedHintText}>This page keeps another horizontal level 2 pager.</Text>

          <PagerView
            style={styles.nestedLevel2Pager}
            initialPage={0}
            overScrollMode="never"
            nestedScrollEnabled
            scrollSensitivity={4}
            onPageSelected={(event) => setNestedLevel2RightIndex(event.nativeEvent.position)}
          >
            <View key="nested-level2-b-top" style={[styles.nestedLevel2Page, styles.nestedLevel2BTopPage]}>
              <Text style={styles.nestedLevel2Title}>Level 2B / Page 1</Text>
              <Text style={styles.nestedHintText}>Nested gestures remain isolated by direction.</Text>
            </View>
            <View key="nested-level2-b-bottom" style={[styles.nestedLevel2Page, styles.nestedLevel2BBottomPage]}>
              <Text style={styles.nestedLevel2Title}>Level 2B / Page 2</Text>
              <Text style={styles.nestedHintText}>Use this as a regression test for nested pagers.</Text>
            </View>
          </PagerView>
        </View>
      </PagerView>
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
  nestedHeaderCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
    padding: 16,
    gap: 6,
  },
  nestedHeaderTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#111111',
  },
  nestedHeaderSubtitle: {
    fontSize: 13,
    color: '#555555',
  },
  nestedStateText: {
    fontSize: 12,
    color: '#333333',
  },
  nestedLevel1Pager: {
    height: 380,
    borderRadius: 12,
    overflow: 'hidden',
  },
  nestedLevel1Page: {
    flex: 1,
    padding: 12,
    gap: 10,
  },
  nestedLevel1LeftPage: {
    backgroundColor: '#EAF5FF',
  },
  nestedLevel1RightPage: {
    backgroundColor: '#FFF6E8',
  },
  nestedLevel1Title: {
    fontSize: 18,
    fontWeight: '700',
    color: '#111111',
  },
  nestedLevel2Pager: {
    flex: 1,
    borderRadius: 10,
    overflow: 'hidden',
  },
  nestedLevel2Page: {
    flex: 1,
    padding: 12,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 8,
  },
  nestedLevel2ATopPage: {
    backgroundColor: '#F4FAFF',
  },
  nestedLevel2ABottomPage: {
    backgroundColor: '#F4FFF6',
  },
  nestedLevel2BTopPage: {
    backgroundColor: '#FFF9EF',
  },
  nestedLevel2BBottomPage: {
    backgroundColor: '#FFEFF4',
  },
  nestedLevel2Title: {
    fontSize: 16,
    fontWeight: '700',
    color: '#111111',
  },
  nestedLevel3Pager: {
    width: '100%',
    height: 140,
    borderRadius: 10,
    overflow: 'hidden',
    marginTop: 6,
  },
  nestedLevel3Page: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 12,
    gap: 6,
  },
  nestedLevel3PageOne: {
    backgroundColor: '#DDF0FF',
  },
  nestedLevel3PageTwo: {
    backgroundColor: '#D6FFE9',
  },
  nestedLevel3Title: {
    fontSize: 15,
    fontWeight: '700',
    color: '#111111',
  },
  nestedHintText: {
    fontSize: 12,
    color: '#444444',
    textAlign: 'center',
    lineHeight: 18,
  },
});
