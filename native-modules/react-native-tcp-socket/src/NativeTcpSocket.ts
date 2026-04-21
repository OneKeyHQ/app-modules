import { TurboModuleRegistry } from 'react-native';

import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  /**
   * Attempt a TCP connection to host:port.
   * Resolves with connection time in ms, rejects with error message.
   */
  connectWithTimeout(
    host: string,
    port: number,
    timeoutMs: number,
  ): Promise<number>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNTcpSocket');
