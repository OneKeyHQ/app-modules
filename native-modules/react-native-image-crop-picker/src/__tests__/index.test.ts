const mockNative = {
  openPicker: jest.fn(),
};

jest.mock('react-native-nitro-modules', () => ({
  NitroModules: { createHybridObject: () => mockNative },
}));

import ImageCropPicker from '../index';

beforeEach(() => jest.clearAllMocks());

it('passes original selection options and native results through unchanged', async () => {
  const image = {
    path: 'file:///selected.heic',
    mime: 'image/heic',
    data: 'original',
  };
  mockNative.openPicker.mockResolvedValue(image);
  await expect(
    ImageCropPicker.openPicker({ preserveOriginal: true })
  ).resolves.toBe(image);
  expect(mockNative.openPicker).toHaveBeenCalledWith({
    preserveOriginal: true,
  });
});

it('retains picker cancellation as a distinct terminal result', async () => {
  mockNative.openPicker.mockRejectedValue(
    new Error('E_PICKER_CANCELLED: User cancelled image selection')
  );
  await expect(ImageCropPicker.openPicker()).rejects.toMatchObject({
    code: 'E_PICKER_CANCELLED',
  });
});
