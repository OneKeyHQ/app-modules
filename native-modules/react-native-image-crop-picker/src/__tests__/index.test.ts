const mockNative = {
  openPicker: jest.fn(),
};

jest.mock('react-native-nitro-modules', () => ({
  NitroModules: { createHybridObject: () => mockNative },
}));

import packageManifest from '../../package.json';

import ImageCropPicker from '../index';

const { getPackageEntryPoint } = jest.requireActual<{
  getPackageEntryPoint(
    context: { mainFields: string[] },
    packageInfo: { packageJson: Record<string, unknown> },
    platform: string
  ): string;
}>('metro-resolver/private/PackageResolve');

beforeEach(() => jest.clearAllMocks());

it.each(['ios', 'android'])(
  'resolves the native package entry on %s',
  (platform) => {
    expect(
      getPackageEntryPoint(
        { mainFields: ['react-native', 'browser', 'main'] },
        { packageJson: packageManifest },
        platform
      )
    ).toBe('./lib/module/index.js');
  }
);

it('retains the browser package entry on web', () => {
  expect(
    getPackageEntryPoint(
      { mainFields: ['browser', 'main'] },
      { packageJson: packageManifest },
      'web'
    )
  ).toBe('./lib/module/index.web.js');
});

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
