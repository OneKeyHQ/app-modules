import { View, StyleSheet } from 'react-native';
import { TestPageBase } from './TestPageBase';
import { SkeletonView } from '@onekeyfe/react-native-skeleton';

interface SkeletonTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}


const baseColors = {
  dark: {
    primary: '#111111',
    secondary: '#333333',
  },
  light: {
    primary: '#fafafa',
    secondary: '#cdcdcd',
  },
};

export function SkeletonTestPage({ onGoHome, safeAreaInsets }: SkeletonTestPageProps) {

  return (
    <TestPageBase
      title="Skeleton Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      <View style={styles.section}>
         <SkeletonView style={styles.skeleton} shimmerSpeed={3.0}  shimmerGradientColors={[baseColors.light.primary,baseColors.light.secondary]} />
         <SkeletonView style={styles.skeleton} shimmerGradientColors={[baseColors.dark.primary,baseColors.dark.secondary]} />
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  section: {
    marginBottom: 25,
    gap: 24,
  },
  skeleton: {
    width: 300,
    height: 48,
  },
});
