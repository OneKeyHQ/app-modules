export {};

const mockNative = {
  prevent: jest.fn().mockResolvedValue(undefined),
  allow: jest.fn().mockResolvedValue(undefined),
  setListener: jest.fn(),
};
const mockCreate = jest.fn(() => mockNative);
jest.mock('react-native-nitro-modules', () => ({
  NitroModules: { createHybridObject: mockCreate },
}));

const load = () => {
  jest.resetModules();
  mockCreate.mockReset().mockReturnValue(mockNative);
  mockNative.setListener.mockReset();
  return require('../index') as typeof import('../index');
};

test('fans events out and suppresses delivery to removed subscribers', () => {
  const { CaptureProtection, CaptureEventType } = load();
  const first = jest.fn();
  const second = jest.fn();
  const a = CaptureProtection.addListener(first);
  const b = CaptureProtection.addListener(second);
  expect(mockNative.setListener).toHaveBeenCalledTimes(1);
  const callback = mockNative.setListener.mock.calls[0]![0] as (
    event: number
  ) => void;
  callback(CaptureEventType.CAPTURED);
  expect(first).toHaveBeenCalledWith(CaptureEventType.CAPTURED);
  expect(second).toHaveBeenCalledWith(CaptureEventType.CAPTURED);
  a.remove();
  a.remove();
  callback(CaptureEventType.RECORDING);
  expect(first).toHaveBeenCalledTimes(1);
  expect(second).toHaveBeenCalledTimes(2);
  b.remove();
  expect(mockNative.setListener).toHaveBeenLastCalledWith(undefined);
  callback(CaptureEventType.CAPTURED);
  expect(second).toHaveBeenCalledTimes(2);
});

test('removed subscriptions cannot interrupt registration of a new consumer', () => {
  const { CaptureProtection } = load();
  const old = CaptureProtection.addListener(jest.fn());
  old.remove();
  const current = CaptureProtection.addListener(jest.fn());
  const calls = mockNative.setListener.mock.calls.length;
  old.remove();
  expect(mockNative.setListener).toHaveBeenCalledTimes(calls);
  current.remove();
});

test('a failed native registration can be retried without a leaked subscriber', () => {
  const { CaptureProtection } = load();
  mockCreate.mockImplementationOnce(() => {
    throw new Error('Not registered');
  });
  expect(() => CaptureProtection.addListener(jest.fn())).toThrow(
    'Not registered'
  );
  const callback = jest.fn();
  CaptureProtection.addListener(callback);
  expect(mockNative.setListener).toHaveBeenCalledTimes(1);
});

test('protection failure is propagated rather than reported as success', async () => {
  const { CaptureProtection } = load();
  mockNative.prevent.mockRejectedValueOnce(new Error('No foreground activity'));
  await expect(CaptureProtection.prevent()).rejects.toThrow(
    'No foreground activity'
  );
});
