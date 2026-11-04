import { StyleSheet, View } from 'react-native';
import renderer, { act } from 'react-test-renderer';
import { OneKeyIcon } from '../icon/index.web';

jest.mock('#onekey-icon-assets', () => ({
  iconResourceHref: (name: string) => `/icons/${name}.svg#onekey-${name}`,
}));

describe('Web named icon inheritance', () => {
  it('keeps CSS colors on the outer host and lets caller styles win', () => {
    let component: renderer.ReactTestRenderer;
    act(() => {
      component = renderer.create(
        <OneKeyIcon name="ChartPositionOutline" color="var(--icon)" />
      );
    });
    const svg = component!.root.findByType('svg');
    expect(svg.props.color).toBeUndefined();
    expect(svg.props['aria-hidden']).toBe(true);
    expect(component!.root.findByType('use').props.href).toBe(
      '/icons/ChartPositionOutline.svg#onekey-ChartPositionOutline'
    );
    expect(
      StyleSheet.flatten(component!.root.findByType(View).props.style).color
    ).toBe('var(--icon)');
    const callerStyle = { width: 102, height: 28, color: 'var(--caller)' };
    act(() => {
      component!.update(
        <OneKeyIcon name="DotVerSolid" color="inherit" style={callerStyle} />
      );
    });
    const style = StyleSheet.flatten(
      component!.root.findByType(View).props.style
    );
    expect(style).toEqual(callerStyle);
    act(() => component!.unmount());
  });
});
