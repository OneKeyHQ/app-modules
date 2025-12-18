import { View, StyleSheet } from 'react-native';
import { TestPageBase } from './TestPageBase';
import { SkeletonView } from '@onekeyfe/react-native-skeleton';

interface SkeletonTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function SkeletonTestPage({ onGoHome, safeAreaInsets }: SkeletonTestPageProps) {

  return (
    <TestPageBase
      title="Skeleton Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      <View style={styles.section}>
         <SkeletonView style={styles.skeleton} />
         <SkeletonView style={styles.skeleton}  />
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
    height: 60,
  },
});
