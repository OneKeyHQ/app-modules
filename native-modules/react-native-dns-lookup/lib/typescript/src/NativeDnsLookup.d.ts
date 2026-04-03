import type { TurboModule } from 'react-native';
export interface Spec extends TurboModule {
    getIpAddresses(hostname: string): Promise<string[]>;
}
declare const _default: Spec;
export default _default;
//# sourceMappingURL=NativeDnsLookup.d.ts.map