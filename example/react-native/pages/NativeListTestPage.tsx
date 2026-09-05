import { useNavigation } from '@react-navigation/native';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

import type { RootStackNavigationProp } from '../route';

import { nativeListExamples } from './NativeListExamplePage';

const groups = [
  { key: 'container', title: 'Containers' },
  { key: 'row', title: 'Row models' },
  { key: 'selection', title: 'Selection' },
] as const;

export function NativeListTestPage() {
  const navigation = useNavigation<RootStackNavigationProp>();

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <Text style={styles.intro}>
        Source-aligned native list scenarios. Every item opens an independent
        page backed by one serializable snapshot.
      </Text>
      {groups.map(group => (
        <View key={group.key} style={styles.section}>
          <Text style={styles.sectionTitle}>{group.title}</Text>
          <View style={styles.card}>
            {nativeListExamples
              .filter(example => example.group === group.key)
              .map((example, index, examples) => (
                <Pressable
                  key={example.key}
                  testID={`native-list-entry-${example.key}`}
                  accessibilityRole="button"
                  onPress={() =>
                    navigation.navigate('NativeListExample', {
                      example: example.key,
                      title: example.title,
                    })
                  }
                  style={({ pressed }) => [
                    styles.item,
                    index < examples.length - 1 && styles.itemBorder,
                    pressed && styles.itemPressed,
                  ]}
                >
                  <View style={styles.itemText}>
                    <Text style={styles.itemTitle}>{example.title}</Text>
                    <Text style={styles.itemDescription} numberOfLines={2}>
                      {example.description}
                    </Text>
                  </View>
                  <View style={styles.chevron} />
                </Pressable>
              ))}
          </View>
        </View>
      ))}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Performance</Text>
        <View style={styles.card}>
          <Pressable
            testID="native-list-entry-account-selector"
            accessibilityRole="button"
            onPress={() => navigation.navigate('NativeListAccountSelector')}
            style={({ pressed }) => [
              styles.item,
              styles.itemBorder,
              pressed && styles.itemPressed,
            ]}
          >
            <View style={styles.itemText}>
              <Text style={styles.itemTitle}>Account selector</Text>
              <Text style={styles.itemDescription}>
                Two independent native lists · 1,000 wallets × 1,000 accounts
              </Text>
            </View>
            <View style={styles.chevron} />
          </Pressable>
          <Pressable
            testID="native-list-entry-benchmark"
            accessibilityRole="button"
            onPress={() => navigation.navigate('NativeListBenchmark')}
            style={({ pressed }) => [
              styles.item,
              pressed && styles.itemPressed,
            ]}
          >
            <View style={styles.itemText}>
              <Text style={styles.itemTitle}>Benchmark</Text>
              <Text style={styles.itemDescription}>
                1,000 / 5,000 rows, fast scroll, batch patch and select all
              </Text>
            </View>
            <View style={styles.chevron} />
          </Pressable>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#FFFFFF' },
  content: { paddingHorizontal: 16, paddingTop: 16, paddingBottom: 32 },
  intro: { color: '#646464', fontSize: 14, lineHeight: 20, marginBottom: 20 },
  section: { marginBottom: 20 },
  sectionTitle: {
    color: '#646464',
    fontSize: 14,
    fontWeight: '600',
    marginBottom: 8,
    paddingHorizontal: 4,
    textTransform: 'uppercase',
  },
  card: {
    backgroundColor: '#FFFFFF',
    borderColor: '#E0E0E0',
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    overflow: 'hidden',
  },
  item: {
    alignItems: 'center',
    flexDirection: 'row',
    minHeight: 68,
    paddingHorizontal: 16,
    paddingVertical: 11,
  },
  itemBorder: { borderBottomColor: '#E0E0E0', borderBottomWidth: 1 },
  itemPressed: { backgroundColor: '#E8E8E8' },
  itemText: { flex: 1, paddingRight: 12 },
  itemTitle: { color: '#202020', fontSize: 16, fontWeight: '500' },
  itemDescription: {
    color: '#646464',
    fontSize: 14,
    lineHeight: 20,
    marginTop: 3,
  },
  chevron: {
    borderBottomColor: '#8D8D8D',
    borderBottomWidth: 1.5,
    borderRightColor: '#8D8D8D',
    borderRightWidth: 1.5,
    height: 8,
    marginRight: 3,
    transform: [{ rotate: '-45deg' }],
    width: 8,
  },
});
