const mockNative = {
  openPicker: jest.fn(),
  getSavePermission: jest.fn(),
  requestSavePermission: jest.fn(),
  saveToLibrary: jest.fn(),
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
  expect(mockNative.openPicker).toHaveBeenCalledWith({ preserveOriginal: true });
});

it('keeps permission denial and permanent denial visible to callers', async () => {
  mockNative.getSavePermission.mockResolvedValue({
    status: 'undetermined',
    canAskAgain: true,
  });
  mockNative.requestSavePermission.mockResolvedValue({
    status: 'denied',
    canAskAgain: false,
  });
  await expect(ImageCropPicker.getSavePermission()).resolves.toEqual({
    status: 'undetermined',
    canAskAgain: true,
  });
  await expect(ImageCropPicker.requestSavePermission()).resolves.toEqual({
    status: 'denied',
    canAskAgain: false,
  });
  expect(mockNative.saveToLibrary).not.toHaveBeenCalled();
});

it('parses denied native saves without requesting broader access', async () => {
  mockNative.saveToLibrary.mockRejectedValue(
    new Error(
      'java.lang.Exception: E_NO_LIBRARY_PERMISSION: Adding photos is not authorized\nstack'
    )
  );
  await expect(
    ImageCropPicker.saveToLibrary('/selected.png')
  ).rejects.toMatchObject({
    code: 'E_NO_LIBRARY_PERMISSION',
    message: 'Adding photos is not authorized',
  });
  expect(mockNative.requestSavePermission).not.toHaveBeenCalled();
});

it('retains picker cancellation as a distinct terminal result', async () => {
  mockNative.openPicker.mockRejectedValue(
    new Error('E_PICKER_CANCELLED: User cancelled image selection')
  );
  await expect(ImageCropPicker.openPicker()).rejects.toMatchObject({
    code: 'E_PICKER_CANCELLED',
  });
});
