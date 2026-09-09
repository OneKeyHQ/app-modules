import { Text as ReactNativeText } from 'react-native';

import { Text } from '../Text';

describe('Text fallback', () => {
  it('is exactly React Native Text outside Android', () => {
    expect(Text).toBe(ReactNativeText);
  });
});
