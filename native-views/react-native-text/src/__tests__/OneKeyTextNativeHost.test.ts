jest.mock(
  'react-native/Libraries/Renderer/shims/createReactNativeComponentClass',
  () => ({
    __esModule: true,
    default: jest.fn(() => 'OneKeyText'),
  })
);

import { ONEKEY_TEXT_VIEW_CONFIG } from '../OneKeyTextNativeHost';

describe('OneKeyText native host config', () => {
  it('opts onTextLayout into Paragraph props and registers its direct event', () => {
    expect(ONEKEY_TEXT_VIEW_CONFIG.validAttributes.onTextLayout).toBe(true);
    expect(
      ONEKEY_TEXT_VIEW_CONFIG.directEventTypes.topTextLayout.registrationName
    ).toBe('onTextLayout');
  });
});
