import React from 'react';
import type { DimensionValue, NativeSyntheticEvent, ViewStyle } from 'react-native';
import BottomAccessoryViewNativeComponent from './BottomAccessoryViewNativeComponent';

const defaultStyle: ViewStyle = {
  position: 'absolute',
  top: 0,
  left: 0,
};

export interface BottomAccessoryViewProps {
  renderBottomAccessoryView: (props: {
    placement: 'inline' | 'expanded' | 'none';
  }) => React.ReactNode;
}

export const BottomAccessoryView = (props: BottomAccessoryViewProps) => {
  const { renderBottomAccessoryView } = props;
  const [bottomAccessoryDimensions, setBottomAccessoryDimensions] =
    React.useState<
      { width: DimensionValue; height: DimensionValue } | undefined
    >({ width: '100%', height: '100%' });
  const [placement, setPlacement] = React.useState<
    'inline' | 'expanded' | 'none'
  >('none');

  const handleNativeLayout = React.useCallback(
    (event: NativeSyntheticEvent<{ width: number; height: number }>) => {
      const { width, height } = event.nativeEvent;
      setBottomAccessoryDimensions({ width, height });
    },
    [setBottomAccessoryDimensions]
  );

  const handlePlacementChanged = React.useCallback(
    (event: NativeSyntheticEvent<{ placement: string }>) => {
      const newPlacement = event.nativeEvent.placement;
      if (
        newPlacement === 'inline' ||
        newPlacement === 'expanded' ||
        newPlacement === 'none'
      ) {
        setPlacement(newPlacement);
      }
    },
    [setPlacement]
  );

  return (
    <BottomAccessoryViewNativeComponent
      style={[defaultStyle, bottomAccessoryDimensions]}
      onNativeLayout={handleNativeLayout}
      onPlacementChanged={handlePlacementChanged}
    >
      {renderBottomAccessoryView({ placement })}
    </BottomAccessoryViewNativeComponent>
  );
};
