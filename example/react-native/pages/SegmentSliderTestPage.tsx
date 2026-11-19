import { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { SegmentSlider } from '@onekeyfe/react-native-segment-slider';

import { TestPageBase } from './TestPageBase';

// Real OneKey light-theme token values (resolved from the Tamagui theme). In
// the app these come live from `useTheme()` in the composite wrapper and adapt
// to light/dark; here we hardcode the light-theme values so the example matches
// the real app 1:1. OneKey's primary is MONOCHROME (near-black in light /
// near-white in dark), NOT green.
//   bgPrimary    = grayA12  #000000df  (fill / active mark / bubble bg)
//   neutral5     = grayA5   #0000001f  (track / inactive mark border)
//   bg           = #FFFFFF             (thumb / inactive mark fill / bubble text)
//   borderStrong = neutral7 #00000031  (thumb border)
const COLORS = {
  fillColor: '#000000df',
  trackColor: '#0000001f',
  thumbColor: '#FFFFFF',
  thumbBorderColor: '#00000031',
  markActiveColor: '#000000df',
  markInactiveColor: '#FFFFFF',
  markBorderColor: '#0000001f',
  bubbleColor: '#000000df',
  bubbleTextColor: '#FFFFFF',
};

function Demo({
  title,
  initial = 0,
  min = 0,
  max = 100,
  segments = 0,
  showBubble = false,
  centerOrigin = false,
  disabled = false,
  hapticStep = 0,
}: {
  title: string;
  initial?: number;
  min?: number;
  max?: number;
  segments?: number;
  showBubble?: boolean;
  centerOrigin?: boolean;
  disabled?: boolean;
  hapticStep?: number;
}) {
  // Controlled: the slider follows `value`, drags report through onChange,
  // and the "Set to …" buttons update the same state (no onChange echo).
  const [value, setValue] = useState(initial);
  const mid = Math.round((min + max) / 2);

  return (
    <View style={styles.demo}>
      <View style={styles.demoHeader}>
        <Text style={styles.demoTitle}>{title}</Text>
        <Text style={styles.demoValue}>{value}</Text>
      </View>
      <View style={styles.sliderBox}>
        <SegmentSlider
          aria-label={title}
          value={value}
          min={min}
          max={max}
          segments={segments}
          sliderHeight={4}
          disabled={disabled}
          showBubble={showBubble}
          centerOrigin={centerOrigin}
          snapTapToSegment={segments > 0}
          hapticStep={hapticStep}
          onChange={setValue}
          onSlideStart={() => console.log(`[${title}] start`)}
          onSlideComplete={() => console.log(`[${title}] complete`)}
          {...COLORS}
        />
      </View>
      <View style={styles.buttonRow}>
        {[min, mid, max].map(v => (
          <Pressable
            key={v}
            style={styles.button}
            onPress={() => setValue(v)}
            disabled={disabled}
          >
            <Text style={styles.buttonText}>{`Set to ${v}`}</Text>
          </Pressable>
        ))}
      </View>
    </View>
  );
}

export function SegmentSliderTestPage() {
  return (
    <TestPageBase title="Segment Slider Test">
      <Demo title="Continuous (no marks)" initial={30} />
      <Demo title="4 segments" segments={4} initial={50} />
      <Demo
        title="4 segments, haptic every 1%"
        segments={4}
        initial={0}
        hapticStep={1}
      />
      <Demo title="10 segments" segments={10} initial={70} />
      <Demo title="With bubble" segments={4} initial={25} showBubble />
      <Demo
        title="Center origin (-100..100)"
        min={-100}
        max={100}
        segments={8}
        initial={-50}
        centerOrigin
      />
      <Demo title="Disabled" segments={4} initial={60} disabled />
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  demo: {
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 16,
  },
  demoHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 18,
  },
  demoTitle: {
    fontSize: 15,
    fontWeight: '600',
    color: '#000',
  },
  demoValue: {
    fontSize: 15,
    fontWeight: '700',
    color: '#000',
  },
  sliderBox: {
    width: '100%',
    height: 24,
  },
  buttonRow: {
    flexDirection: 'row',
    gap: 8,
    marginTop: 16,
  },
  button: {
    flex: 1,
    paddingVertical: 8,
    borderRadius: 8,
    backgroundColor: '#0000000a',
    alignItems: 'center',
  },
  buttonText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#000',
  },
});
