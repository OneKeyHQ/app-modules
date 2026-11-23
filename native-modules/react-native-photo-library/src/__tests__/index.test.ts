const mockNative = {
  getSavePermission: jest.fn(),
  requestSavePermission: jest.fn(),
  saveToLibrary: jest.fn(),
};

jest.mock('react-native-nitro-modules', () => ({
  NitroModules: { createHybridObject: () => mockNative },
}));

import PhotoLibrary from '../index';

beforeEach(() => jest.clearAllMocks());

it('keeps permission denial and permanent denial visible to callers', async () => {
  mockNative.getSavePermission.mockResolvedValue({
    status: 'undetermined',
    canAskAgain: true,
  });
  mockNative.requestSavePermission.mockResolvedValue({
    status: 'denied',
    canAskAgain: false,
  });
  await expect(PhotoLibrary.getSavePermission()).resolves.toEqual({
    status: 'undetermined',
    canAskAgain: true,
  });
  await expect(PhotoLibrary.requestSavePermission()).resolves.toEqual({
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
    PhotoLibrary.saveToLibrary('/selected.png')
  ).rejects.toMatchObject({
    code: 'E_NO_LIBRARY_PERMISSION',
    message: 'Adding photos is not authorized',
  });
  expect(mockNative.requestSavePermission).not.toHaveBeenCalled();
});

it('saves the supplied local file without an implicit permission prompt', async () => {
  mockNative.saveToLibrary.mockResolvedValue(undefined);
  await expect(
    PhotoLibrary.saveToLibrary('file:///selected.png')
  ).resolves.toBeUndefined();
  expect(mockNative.saveToLibrary).toHaveBeenCalledWith('file:///selected.png');
  expect(mockNative.requestSavePermission).not.toHaveBeenCalled();
});
