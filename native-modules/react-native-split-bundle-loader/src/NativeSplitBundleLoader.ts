import { TurboModuleRegistry } from 'react-native';

import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  getRuntimeBundleContext(): Promise<{
    runtimeKind: string;
    sourceKind: string;
    bundleRoot: string;
    builtinExtractRoot?: string;
    nativeVersion: string;
    bundleVersion?: string;
  }>;
  loadSegment(
    segmentId: number,
    segmentKey: string,
    relativePath: string,
    sha256: string,
  ): Promise<void>;
  resolveSegmentPath(relativePath: string, sha256: string): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('SplitBundleLoader');
