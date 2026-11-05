import {
  codegenNativeComponent,
  type CodegenTypes,
  type ViewProps,
} from 'react-native';


export type OnNativeLayout = Readonly<{
  width: CodegenTypes.Double;
  height: CodegenTypes.Double;
}>;

export type OnPlacementChanged = Readonly<{
  placement: string;
}>;

export interface BottomAccessoryViewNativeProps extends ViewProps {
  onNativeLayout?: CodegenTypes.DirectEventHandler<OnNativeLayout>;
  onPlacementChanged?: CodegenTypes.DirectEventHandler<OnPlacementChanged>;
}

export default codegenNativeComponent<BottomAccessoryViewNativeProps>(
  'RNBottomTabsAccessoryView',
  {
    excludedPlatforms: ['android'],
  }
);
