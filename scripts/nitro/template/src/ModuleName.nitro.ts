import type { HybridObject } from 'react-native-nitro-modules';

export interface {{modulePascalCase}}Params {
  message: string;
}

export interface {{modulePascalCase}}Result {
  success: boolean;
  data: string;
}

export interface {{modulePascalCase}}
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  hello(params: {{modulePascalCase}}Params): Promise<{{modulePascalCase}}Result>;
}
