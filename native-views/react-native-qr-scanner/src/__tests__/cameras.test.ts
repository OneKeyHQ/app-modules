const mockNative = {
  listCameras: jest.fn(async () => [{ id: 'back', position: 'back', label: 'Back camera' }]),
  setOnChange: jest.fn<void, [(() => void) | undefined]>(),
};
const mockCreateHybridObject = jest.fn((_name: string) => mockNative);

jest.mock('react-native-nitro-modules', () => ({
  NitroModules: { createHybridObject: (name: string) => mockCreateHybridObject(name) },
}));

// eslint-disable-next-line import/first
import { listCameras, subscribeCameraChanges } from '../cameras';

describe('cameras', () => {
  it('lists cameras through one QrCameraList object', async () => {
    await expect(listCameras()).resolves.toEqual([
      { id: 'back', position: 'back', label: 'Back camera' },
    ]);
    await listCameras();
    expect(mockCreateHybridObject).toHaveBeenCalledTimes(1);
    expect(mockCreateHybridObject).toHaveBeenCalledWith('QrCameraList');
  });

  it('shares one native listener and clears it after the last unsubscribe', () => {
    const first = jest.fn();
    const second = jest.fn();
    const unsubscribeFirst = subscribeCameraChanges(first);
    const unsubscribeSecond = subscribeCameraChanges(second);
    expect(mockNative.setOnChange).toHaveBeenCalledTimes(1);

    const nativeListener = mockNative.setOnChange.mock.calls[0]?.[0];
    nativeListener?.();
    expect(first).toHaveBeenCalledTimes(1);
    expect(second).toHaveBeenCalledTimes(1);

    unsubscribeFirst();
    unsubscribeFirst();
    expect(mockNative.setOnChange).toHaveBeenCalledTimes(1);
    unsubscribeSecond();
    expect(mockNative.setOnChange).toHaveBeenLastCalledWith(undefined);
  });
});
