import { useRef, useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import {
  NativeList,
  type IdentityRow,
  type NativeListRef,
  type NativeListSnapshot,
  type RowPatch,
} from '@onekeyfe/react-native-native-list';

function now(): number {
  return Date.now();
}

function buildRows(count: number): IdentityRow[] {
  return Array.from({ length: count }, (_, index) => ({
    type: 'identity',
    key: `bench-${index}`,
    revision: 0,
    leading: {
      kind: 'token',
      fallbackText: String(index % 100),
      backgroundColor: '#475569',
    },
    title: `Benchmark asset ${index}`,
    subtitle: `Stable key bench-${index}`,
    trailing: [
      { kind: 'value', text: `$${(index * 17.41).toFixed(2)}` },
      { kind: 'checkbox', state: 'unchecked', target: { scope: 'row' } },
    ],
  }));
}

function makeSnapshot(
  rows: readonly IdentityRow[],
  generation: number,
): NativeListSnapshot {
  return {
    schemaVersion: 1,
    generation,
    layout: { kind: 'linear', itemSpacing: 1 },
    rows,
    selection: { mode: 'multiple', selectedKeys: [], rowPressToggles: true },
  };
}

export function NativeListBenchmarkPage() {
  const listRef = useRef<NativeListRef>(null);
  const [snapshot, setSnapshot] = useState(() =>
    makeSnapshot(buildRows(1_000), 1),
  );
  const [log, setLog] = useState<readonly string[]>(['Ready with 1,000 rows']);
  const firstVisibleStarted = useRef<number | undefined>(undefined);
  const fastScrollStarted = useRef<number | undefined>(undefined);
  const fastScrollTarget = useRef<number | undefined>(undefined);

  const record = (value: string) =>
    setLog(current => [value, ...current].slice(0, 8));

  const load = (count: number) => {
    const buildStarted = now();
    const rows = buildRows(count);
    const buildMs = now() - buildStarted;
    firstVisibleStarted.current = now();
    setSnapshot(current => makeSnapshot(rows, current.generation + 1));
    record(
      `Built ${count.toLocaleString()} plain rows in ${buildMs.toFixed(
        1,
      )}ms; awaiting first visible event`,
    );
  };

  const fastScroll = () => {
    fastScrollStarted.current = now();
    fastScrollTarget.current = snapshot.rows.length - 1;
    listRef.current?.scrollToIndex(snapshot.rows.length - 1, true, 'end');
  };

  const batchPatch = () => {
    const count = Math.min(500, snapshot.rows.length);
    const patches: RowPatch[] = Array.from({ length: count }, (_, index) => ({
      type: 'identity',
      key: `bench-${index}`,
      changes: { revision: 1, subtitle: `Batch-updated ${index}` },
    }));
    const started = now();
    listRef.current?.applyPatches(patches);
    requestAnimationFrame(() =>
      record(
        `500-row patch: one native command, next-frame dispatch ${(
          now() - started
        ).toFixed(1)}ms`,
      ),
    );
  };

  const selectAll = () => {
    const keys = snapshot.rows.map(row => row.key);
    const started = now();
    listRef.current?.reconcileSelection(keys);
    requestAnimationFrame(() =>
      record(
        `${keys.length.toLocaleString()}-row selection snapshot: one native command, next-frame dispatch ${(
          now() - started
        ).toFixed(1)}ms`,
      ),
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.controls}>
        <Pressable style={styles.button} onPress={() => load(1_000)}>
          <Text style={styles.buttonText}>1,000</Text>
        </Pressable>
        <Pressable style={styles.button} onPress={() => load(5_000)}>
          <Text style={styles.buttonText}>5,000</Text>
        </Pressable>
        <Pressable style={styles.button} onPress={fastScroll}>
          <Text style={styles.buttonText}>Fast scroll</Text>
        </Pressable>
        <Pressable style={styles.button} onPress={batchPatch}>
          <Text style={styles.buttonText}>Patch 500</Text>
        </Pressable>
        <Pressable style={styles.button} onPress={selectAll}>
          <Text style={styles.buttonText}>Select all</Text>
        </Pressable>
      </View>
      <View style={styles.log}>
        {log.map((line, index) => (
          <Text key={`${index}-${line}`} style={styles.logText}>
            {line}
          </Text>
        ))}
      </View>
      <NativeList
        ref={listRef}
        style={styles.list}
        snapshot={snapshot}
        onVisibleRangeChanged={event => {
          if (firstVisibleStarted.current !== undefined) {
            record(
              `First visible callback: ${(
                now() - firstVisibleStarted.current
              ).toFixed(1)}ms (${event.firstIndex}–${event.lastIndex})`,
            );
            firstVisibleStarted.current = undefined;
          }
          if (
            fastScrollStarted.current !== undefined &&
            event.lastIndex >= (fastScrollTarget.current ?? 0) - 2
          ) {
            record(
              `Fast scroll reached end: ${(
                now() - fastScrollStarted.current
              ).toFixed(1)}ms`,
            );
            fastScrollStarted.current = undefined;
          }
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F7F7F7' },
  controls: { flexDirection: 'row', flexWrap: 'wrap', gap: 6, padding: 10 },
  button: {
    backgroundColor: '#1F2937',
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 8,
  },
  buttonText: { color: '#FFFFFF', fontSize: 12, fontWeight: '600' },
  log: { paddingHorizontal: 10, paddingBottom: 8 },
  logText: { color: '#4B5563', fontSize: 11 },
  list: { flex: 1 },
});
