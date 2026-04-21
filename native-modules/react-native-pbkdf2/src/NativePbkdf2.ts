import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  derive(
    password: string,
    salt: string,
    rounds: number,
    keyLength: number,
    hash: string,
  ): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Pbkdf2');
