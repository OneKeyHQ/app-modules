import React from 'react';
import { View } from 'react-native';
import ReactTestRenderer from 'react-test-renderer';

type MockNativeSheetProps = Readonly<{
  open: boolean;
  sheetHeight: number;
  dismissOnPanDown: boolean;
  dismissOnBackdropPress: boolean;
  onDismiss: (event: { nativeEvent: { reason: string } }) => void;
  onPresented: (event: { nativeEvent: { height: number } }) => void;
}>;

let mockNativeSheetProps: MockNativeSheetProps;

jest.mock(
  '../../../native-views/react-native-native-sheet/src/NativeSheetNativeComponent',
  () => {
    const ReactForMock = require('react') as typeof React;
    return {
      __esModule: true,
      default: (
        props: MockNativeSheetProps & { children?: React.ReactNode },
      ) => {
        mockNativeSheetProps = props;
        return ReactForMock.createElement(
          'RNCNativeSheet',
          props,
          props.children,
        );
      },
    };
  },
);

const { NativeSheet } = jest.requireActual(
  '../../../native-views/react-native-native-sheet/src',
) as typeof import('../../../native-views/react-native-native-sheet/src');

describe('NativeSheet executable lifecycle', () => {
  it('measures each open cycle and emits native lifecycle callbacks once', async () => {
    const onOpenChange = jest.fn();
    const onDismiss = jest.fn();
    const onPresented = jest.fn();
    const onAnimationComplete = jest.fn();
    let renderer!: ReactTestRenderer.ReactTestRenderer;
    const renderSheet = (open: boolean) => (
      <NativeSheet
        open={open}
        maxHeight={600}
        disableDrag
        dismissOnSnapToBottom
        dismissOnOverlayPress
        onOpenChange={onOpenChange}
        onDismiss={onDismiss}
        onPresented={onPresented}
        onAnimationComplete={onAnimationComplete}
      >
        <View testID="sheet-content" />
      </NativeSheet>
    );

    await ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(renderSheet(true));
    });

    expect(mockNativeSheetProps.open).toBe(false);
    expect(mockNativeSheetProps.sheetHeight).toBe(1);
    expect(mockNativeSheetProps.dismissOnPanDown).toBe(false);
    expect(mockNativeSheetProps.dismissOnBackdropPress).toBe(true);

    const autoMeasureContent = renderer.root.find(
      node => typeof node.props.onLayout === 'function',
    );
    await ReactTestRenderer.act(() => {
      autoMeasureContent.props.onLayout({
        nativeEvent: { layout: { height: 240.2 } },
      });
    });

    expect(mockNativeSheetProps.open).toBe(true);
    expect(mockNativeSheetProps.sheetHeight).toBe(241);

    await ReactTestRenderer.act(() => {
      mockNativeSheetProps.onPresented({ nativeEvent: { height: 241 } });
      mockNativeSheetProps.onDismiss({
        nativeEvent: { reason: 'programmatic' },
      });
      mockNativeSheetProps.onDismiss({
        nativeEvent: { reason: 'programmatic' },
      });
    });

    expect(onPresented.mock.calls).toEqual([[241]]);
    expect(onOpenChange.mock.calls).toEqual([[false]]);
    expect(onDismiss.mock.calls).toEqual([['programmatic']]);
    expect(onAnimationComplete.mock.calls).toEqual([
      [{ open: true }],
      [{ open: false }],
    ]);

    await ReactTestRenderer.act(() => {
      renderer.update(renderSheet(false));
    });
    await ReactTestRenderer.act(() => {
      renderer.update(renderSheet(true));
    });

    expect(mockNativeSheetProps.open).toBe(false);
    expect(mockNativeSheetProps.sheetHeight).toBe(1);

    const reopenedAutoMeasureContent = renderer.root.find(
      node => typeof node.props.onLayout === 'function',
    );
    await ReactTestRenderer.act(() => {
      reopenedAutoMeasureContent.props.onLayout({
        nativeEvent: { layout: { height: 180 } },
      });
    });

    expect(mockNativeSheetProps.open).toBe(true);
    expect(mockNativeSheetProps.sheetHeight).toBe(180);

    await ReactTestRenderer.act(() => {
      renderer.unmount();
    });
  });
});
