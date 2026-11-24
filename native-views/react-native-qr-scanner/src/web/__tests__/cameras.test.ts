import { cameraPositionFromLabel, listWebCameras, subscribeWebCameraChanges } from '../cameras';

function setMediaDevices(value: unknown) {
  Object.defineProperty(globalThis.navigator, 'mediaDevices', { configurable: true, value });
}

describe('web cameras', () => {
  it('guesses the position from the label', () => {
    expect(cameraPositionFromLabel('Back Camera')).toBe('back');
    expect(cameraPositionFromLabel('camera2 0, facing back')).toBe('back');
    expect(cameraPositionFromLabel('Front Camera')).toBe('front');
    expect(cameraPositionFromLabel('FaceTime HD Camera')).toBe('front');
    expect(cameraPositionFromLabel('后置相机')).toBe('back');
    expect(cameraPositionFromLabel('Logitech BRIO')).toBe('external');
    expect(cameraPositionFromLabel('')).toBe('external');
  });

  it('lists video inputs and names unlabeled ones', async () => {
    setMediaDevices({
      enumerateDevices: async () => [
        { kind: 'audioinput', deviceId: 'mic', label: 'Mic' },
        { kind: 'videoinput', deviceId: 'cam-a', label: 'FaceTime HD Camera' },
        { kind: 'videoinput', deviceId: 'cam-b', label: '' },
        { kind: 'videoinput', deviceId: '', label: '' },
      ],
    });
    await expect(listWebCameras()).resolves.toEqual([
      { id: 'cam-a', label: 'FaceTime HD Camera', position: 'front' },
      { id: 'cam-b', label: 'Camera 2', position: 'external' },
    ]);
  });

  it('gives duplicate listener subscriptions independent cleanup', () => {
    const mediaDevices = new EventTarget();
    setMediaDevices(mediaDevices);
    const listener = jest.fn();
    const unsubscribeFirst = subscribeWebCameraChanges(listener);
    const unsubscribeSecond = subscribeWebCameraChanges(listener);
    mediaDevices.dispatchEvent(new Event('devicechange'));
    expect(listener).toHaveBeenCalledTimes(2);
    unsubscribeFirst();
    unsubscribeFirst();
    mediaDevices.dispatchEvent(new Event('devicechange'));
    expect(listener).toHaveBeenCalledTimes(3);
    unsubscribeSecond();
    mediaDevices.dispatchEvent(new Event('devicechange'));
    expect(listener).toHaveBeenCalledTimes(3);
  });
});
