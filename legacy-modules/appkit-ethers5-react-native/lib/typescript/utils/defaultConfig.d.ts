import type { Metadata, Provider, ProviderType, AppKitFrameProvider } from '@reown/appkit-scaffold-utils-react-native';
export interface ConfigOptions {
    metadata: Metadata;
    extraConnectors?: (Provider | AppKitFrameProvider)[];
}
export declare function defaultConfig(options: ConfigOptions): ProviderType;
//# sourceMappingURL=defaultConfig.d.ts.map