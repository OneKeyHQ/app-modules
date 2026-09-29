import * as React from "react";
import { act, create, type ReactTestRenderer } from "react-test-renderer";
import {
  Keyboard,
  StyleSheet,
  View,
  type GestureResponderEvent,
  type LayoutChangeEvent,
} from "react-native";
import { NativeScroller, type NativeScrollerRef } from "../NativeScroller";
import NativeScrollerHost, { Commands } from "../NativeScrollerNativeComponent";

Object.assign(globalThis, { IS_REACT_ACT_ENVIRONMENT: true });

const mockFocusedInput = { input: "focused" };
const mockOtherInput = { input: "other" };
const mockView = { view: true };
jest.mock("react-native", () => ({
  View: "View",
  Platform: { OS: "ios", Version: 26 },
  StyleSheet: {
    create: (styles: object) => styles,
    flatten: (style: object | object[]) =>
      Object.assign({}, ...[style].flat(Infinity).filter(Boolean)),
  },
  findNodeHandle: () => 42,
  Keyboard: {
    isVisible: jest.fn(() => true),
    metrics: () => ({ height: 250 }),
    dismiss: jest.fn(),
  },
  TextInput: {
    State: {
      currentlyFocusedInput: () => mockFocusedInput,
    },
  },
}));
jest.mock("react-native/Libraries/Components/TextInput/TextInputState", () => ({
  __esModule: true,
  default: {
    isTextInput: (target: object) =>
      target === mockFocusedInput || target === mockOtherInput,
  },
}));
jest.mock("../NativeScrollerNativeComponent", () => {
  const ReactMock = jest.requireActual<typeof React>("react");
  return {
    __esModule: true,
    default: ReactMock.forwardRef((props: object, ref) =>
      ReactMock.createElement("NativeHost", { ...props, ref })
    ),
    Commands: {
      scrollTo: jest.fn(),
      scrollToEnd: jest.fn(),
      flashScrollIndicators: jest.fn(),
      setRefreshState: jest.fn(),
    },
  };
});

const nativeHandle = {
  measure: jest.fn(),
  measureInWindow: jest.fn(),
  measureLayout: jest.fn(),
  setNativeProps: jest.fn(),
  focus: jest.fn(),
  blur: jest.fn(),
};
const layout = (width: number, height: number) =>
  ({
    nativeEvent: { layout: { x: 0, y: 0, width, height } },
  } as LayoutChangeEvent);
const touch = (target: object) =>
  ({
    target,
    nativeEvent: { target, touches: [] },
  } as unknown as GestureResponderEvent);

describe("NativeScroller bridge", () => {
  let renderer: ReactTestRenderer;
  const host = () => renderer.root.findByType(NativeScrollerHost).props;
  const content = () => renderer.root.findByType(View).props;
  const mount = (element: React.ReactElement) =>
    act(() => {
      renderer = create(element, { createNodeMock: () => nativeHandle });
    });
  beforeEach(() => jest.clearAllMocks());
  afterEach(() => {
    if (renderer) act(() => renderer.unmount());
  });

  it("mounts one Yoga content view and reports changed dimensions without a layout feedback loop", () => {
    const onContentSizeChange = jest.fn();
    const onLayout = jest.fn();
    mount(
      <NativeScroller
        pagerScrollKey="spot"
        onLayout={onLayout}
        onContentSizeChange={onContentSizeChange}
        contentContainerStyle={{
          paddingBottom: 24,
          flexGrow: 1,
          minHeight: 100,
        }}
      >
        <React.Fragment />
      </NativeScroller>
    );
    expect(host().nativeID).toBe("rnc-collapsible-pager-native-scroller:spot");
    expect(host().nestedScrollEnabled).toBe(true);
    expect(content().collapsable).toBe(false);
    act(() => host().onLayout(layout(320, 600)));
    expect(onLayout).toHaveBeenCalledTimes(1);
    expect(StyleSheet.flatten(content().style).minHeight).toBe(100);
    act(() => host().onContentViewportChange({ nativeEvent: { height: 500 } }));
    expect(StyleSheet.flatten(content().style)).toMatchObject({
      paddingBottom: 24,
      minHeight: 500,
      position: "absolute",
    });
    act(() => content().onLayout(layout(320, 900)));
    act(() => content().onLayout(layout(320, 900)));
    expect(host().contentWidth).toBe(320);
    expect(host().contentHeight).toBe(900);
    expect(onContentSizeChange).toHaveBeenCalledTimes(1);
    act(() => content().onLayout(layout(320, 600)));
    expect(onContentSizeChange).toHaveBeenLastCalledWith(320, 600);
  });

  it("fills only the native content viewport after inset changes and long-content shrink", () => {
    const onContentSizeChange = jest.fn();
    mount(
      <NativeScroller
        contentContainerStyle={{ flexGrow: 1, paddingBottom: 24 }}
        onContentSizeChange={onContentSizeChange}
      />
    );
    expect(StyleSheet.flatten(content().style).minHeight).toBe(0);
    act(() => content().onLayout(layout(320, 3000)));
    act(() =>
      host().onContentViewportChange({ nativeEvent: { height: 1577 } })
    );
    act(() =>
      host().onContentViewportChange({ nativeEvent: { height: 1577 } })
    );
    expect(StyleSheet.flatten(content().style)).toMatchObject({
      minHeight: 1577,
      paddingBottom: 24,
    });
    expect(onContentSizeChange).toHaveBeenCalledTimes(1);
    act(() => content().onLayout(layout(320, 1577)));
    expect(host().contentHeight).toBe(1577);
    act(() =>
      host().onContentViewportChange({ nativeEvent: { height: 1500 } })
    );
    expect(StyleSheet.flatten(content().style).minHeight).toBe(1500);
    act(() => host().onContentViewportChange({ nativeEvent: { height: -1 } }));
    expect(StyleSheet.flatten(content().style).minHeight).toBe(1500);
  });

  it("preserves an explicit content minimum above the usable native viewport", () => {
    mount(
      <NativeScroller
        contentContainerStyle={{ flexGrow: 1, minHeight: 2000 }}
      />
    );
    act(() =>
      host().onContentViewportChange({ nativeEvent: { height: 1577 } })
    );
    expect(StyleSheet.flatten(content().style).minHeight).toBe(2000);
  });

  it("maps commands and host measurement through a stable public ref", () => {
    const ref = React.createRef<NativeScrollerRef>();
    mount(<NativeScroller ref={ref} nestedScrollEnabled={false} />);
    const firstRef = ref.current;
    act(() => ref.current?.scrollTo({ y: 60, animated: false }));
    expect(Commands.scrollTo).toHaveBeenLastCalledWith(
      nativeHandle,
      0,
      60,
      false
    );
    act(() => ref.current?.scrollTo(80, 10, true));
    expect(Commands.scrollTo).toHaveBeenLastCalledWith(
      nativeHandle,
      10,
      80,
      true
    );
    act(() => ref.current?.scrollToEnd());
    expect(Commands.scrollToEnd).toHaveBeenLastCalledWith(nativeHandle, true);
    const callback = jest.fn();
    ref.current?.measure(callback);
    expect(nativeHandle.measure).toHaveBeenCalledWith(callback);
    expect(ref.current?.getScrollableNode()).toBe(42);
    expect(host().nestedScrollEnabled).toBe(false);
    act(() => host().onContentViewportChange({ nativeEvent: { height: 600 } }));
    expect(ref.current).toBe(firstRef);
  });

  it("maps refresh props without mounting the RN refresh child and restores an unchanged controlled false", () => {
    const onRefresh = jest.fn();
    const refresh = React.createElement("RefreshControl", {
      refreshing: false,
      onRefresh,
      tintColor: "red",
      colors: ["blue"],
      progressViewOffset: 20,
    });
    mount(<NativeScroller refreshControl={refresh} />);
    expect(
      renderer.root.findAllByType("RefreshControl" as React.ElementType)
    ).toHaveLength(0);
    expect(host()).toMatchObject({
      refreshEnabled: true,
      refreshing: false,
      refreshTintColor: "red",
      refreshColors: ["blue"],
      refreshProgressViewOffset: 20,
    });
    jest.mocked(Commands.setRefreshState).mockClear();
    act(() => host().onRefresh());
    expect(onRefresh).toHaveBeenCalledTimes(1);
    expect(Commands.setRefreshState).toHaveBeenCalledWith(nativeHandle, false);
  });

  it("uses the latest controlled refresh value after the refresh callback updates React state", () => {
    function Controlled() {
      const [refreshing, setRefreshing] = React.useState(false);
      return (
        <NativeScroller
          refreshControl={React.createElement("RefreshControl", {
            refreshing,
            onRefresh: () => setRefreshing(true),
          })}
        />
      );
    }
    mount(<Controlled />);
    act(() => host().onRefresh());
    expect(Commands.setRefreshState).toHaveBeenLastCalledWith(
      nativeHandle,
      true
    );
  });

  it("never captures a different TextInput and handled only claims unhandled bubbled taps", () => {
    mount(<NativeScroller keyboardShouldPersistTaps="never" />);
    expect(host().onStartShouldSetResponderCapture(touch(mockOtherInput))).toBe(
      false
    );
    expect(host().onStartShouldSetResponderCapture(touch(mockView))).toBe(true);
    act(() =>
      renderer.update(<NativeScroller keyboardShouldPersistTaps="handled" />)
    );
    expect(host().onStartShouldSetResponderCapture(touch(mockView))).toBe(
      false
    );
    expect(host().onStartShouldSetResponder(touch(mockView))).toBe(true);
    act(() => host().onResponderGrant(touch(mockView)));
    act(() => host().onResponderRelease(touch(mockView)));
    expect(Keyboard.dismiss).toHaveBeenCalledTimes(1);
  });

  it("forwards native events and does not dismiss keyboard after a scroll or persist-always tap", () => {
    const onScroll = jest.fn();
    mount(
      <NativeScroller onScroll={onScroll} keyboardShouldPersistTaps="handled" />
    );
    const event = { nativeEvent: { contentOffset: { x: 0, y: 5 } } };
    act(() => host().onResponderGrant(touch(mockView)));
    act(() => host().onScroll(event));
    act(() => host().onResponderRelease(touch(mockView)));
    expect(onScroll).toHaveBeenCalledWith(event);
    expect(Keyboard.dismiss).not.toHaveBeenCalled();
    act(() =>
      renderer.update(<NativeScroller keyboardShouldPersistTaps="always" />)
    );
    expect(host().onStartShouldSetResponderCapture(touch(mockView))).toBe(
      false
    );
    expect(host().onStartShouldSetResponder(touch(mockView))).toBe(false);
  });
});
