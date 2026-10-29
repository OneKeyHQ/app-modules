import React from 'react';
import { Text, View } from 'react-native';
import ReactTestRenderer, { act } from 'react-test-renderer';

jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomTabsNativeComponent',
  () => ({
    __esModule: true,
    default: 'NativeBottomTabs',
  }),
);
jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomTabsDrawerNativeComponent',
  () => ({
    __esModule: true,
    default: 'NativeDrawer',
  }),
);
jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomAccessoryView',
  () => ({
    BottomAccessoryView: 'BottomAccessoryView',
  }),
);

import BottomTabs from '../../../native-views/react-native-bottom-tabs/src/BottomTabs';
import DrawerWeb from '../../../native-views/react-native-bottom-tabs/src/BottomTabsDrawerNativeComponent.web';
import { useBottomTabBarHeight } from '../../../native-views/react-native-bottom-tabs/src/utils/useBottomTabBarHeight';

function HeightProbe() {
  return <Text testID="tab-height-probe">{useBottomTabBarHeight()}</Text>;
}

describe('native drawer slots', () => {
  let renderer: ReactTestRenderer.ReactTestRenderer | undefined;
  afterEach(async () => {
    await act(() => renderer?.unmount());
  });
  const menu = () => <View testID="menu-content" />;
  const render = (content?: () => React.ReactNode, open = false) => (
    <BottomTabs
      navigationState={{ index: 0, routes: [{ key: 'home' }] }}
      renderScene={() => <View testID="scene" />}
      onIndexChange={() => {}}
      renderDrawerContent={content}
      drawerOpen={open}
    />
  );

  it('keeps the foreground and native tabs mounted across enabling, opening and menu removal', async () => {
    await act(() => {
      renderer = ReactTestRenderer.create(render());
    });
    const drawer = renderer!.root.findByType('NativeDrawer' as any);
    const tabs = renderer!.root.findByType('NativeBottomTabs' as any);
    const scene = renderer!.root.findByProps({ testID: 'scene' });
    expect(drawer.props.enabled).toBe(false);
    expect(drawer.props.swipeEnabled).toBe(false);
    expect(drawer.props.open).toBe(false);
    for (const [content, open] of [
      [menu, false],
      [menu, true],
      [undefined, true],
    ] as const) {
      await act(() => {
        renderer!.update(render(content, open));
      });
      expect(renderer!.root.findByType('NativeDrawer' as any)).toBe(drawer);
      expect(renderer!.root.findByType('NativeBottomTabs' as any)).toBe(tabs);
      expect(renderer!.root.findByProps({ testID: 'scene' })).toBe(scene);
    }
    expect(drawer.props.enabled).toBe(false);
  });

  it('forwards only the settled boolean, with native progress absent from the API', async () => {
    const onChange = jest.fn();
    await act(() => {
      renderer = ReactTestRenderer.create(
        <BottomTabs
          navigationState={{ index: 0, routes: [{ key: 'home' }] }}
          renderScene={() => <View />}
          onIndexChange={() => {}}
          renderDrawerContent={menu}
          onDrawerOpenChange={onChange}
          hapticFeedbackEnabled
          drawerSwipeEnabled
        />,
      );
    });
    const drawer = renderer!.root.findByType('NativeDrawer' as any);
    expect(drawer.props.hapticFeedbackEnabled).toBe(true);
    expect(drawer.props.swipeEnabled).toBe(true);
    act(() => drawer.props.onDrawerOpenChange({ nativeEvent: { open: true } }));
    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith(true);
  });

  it('Web omits the menu and returns only the foreground', async () => {
    await act(() => {
      renderer = ReactTestRenderer.create(
        <DrawerWeb enabled open>
          <View testID="menu" />
          <View testID="main" />
        </DrawerWeb>,
      );
    });
    expect(renderer!.root.findAllByProps({ testID: 'menu' })).toHaveLength(0);
    expect(renderer!.root.findAllByType(View)).toHaveLength(1);
    expect(renderer!.root.findByType(View).props.testID).toBe('main');
  });

  it('uses zero for both context and drawer immediately when the native bar is hidden', async () => {
    const tabs = (hidden: boolean) => (
      <BottomTabs navigationState={{ index: 0, routes: [{ key: 'home' }] }}
        renderScene={() => <HeightProbe />} onIndexChange={() => {}} tabBarHidden={hidden} />
    );
    await act(() => { renderer = ReactTestRenderer.create(tabs(false)); });
    const native = renderer!.root.findByType('NativeBottomTabs' as any);
    const height = () => renderer!.root.findByType(Text).props.children;
    act(() => native.props.onTabBarMeasured({ nativeEvent: { height: 83 } }));
    expect(height()).toBe(83);
    await act(() => { renderer!.update(tabs(true)); });
    expect(height()).toBe(0);
    expect(renderer!.root.findByType('NativeDrawer' as any).props.bottomBarHeight).toBe(0);
    act(() => native.props.onTabBarMeasured({ nativeEvent: { height: 0 } }));
    await act(() => { renderer!.update(tabs(false)); });
    act(() => native.props.onTabBarMeasured({ nativeEvent: { height: 83 } }));
    expect(height()).toBe(83);
    expect(renderer!.root.findByType('NativeDrawer' as any).props.bottomBarHeight).toBe(83);
  });

  it('keeps custom height separate from native hide events and presentation switches', async () => {
    const custom = () => <View testID="custom-bar" />;
    const tabs = (useCustom: boolean) => (
      <BottomTabs navigationState={{ index: 0, routes: [{ key: 'home' }] }}
        renderScene={() => <HeightProbe />} onIndexChange={() => {}}
        tabBar={useCustom ? custom : undefined} tabBarHidden={useCustom} />
    );
    await act(() => { renderer = ReactTestRenderer.create(tabs(false)); });
    const native = renderer!.root.findByType('NativeBottomTabs' as any);
    act(() => native.props.onTabBarMeasured({ nativeEvent: { height: 83 } }));
    await act(() => { renderer!.update(tabs(true)); });
    const wrapper = renderer!.root.findAllByType(View).find(node => typeof node.props.onLayout === 'function')!;
    act(() => wrapper.props.onLayout({ nativeEvent: { layout: { height: 52 } } }));
    act(() => native.props.onTabBarMeasured({ nativeEvent: { height: 0 } }));
    expect(renderer!.root.findByType(Text).props.children).toBe(52);
    expect(renderer!.root.findByType('NativeDrawer' as any).props.bottomBarHeight).toBe(52);
    await act(() => { renderer!.update(tabs(false)); });
    expect(renderer!.root.findByType(Text).props.children).toBe(0);
    act(() => native.props.onTabBarMeasured({ nativeEvent: { height: 83 } }));
    expect(renderer!.root.findByType(Text).props.children).toBe(83);
  });
});
