import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

export interface {{viewPascalCase}}Props extends HybridViewProps {
  color: string;
}
export interface {{viewPascalCase}}Methods extends HybridViewMethods {}

export type {{viewPascalCase}} = HybridView<{{viewPascalCase}}Props, {{viewPascalCase}}Methods>;
