jest.mock('react-native', () => ({
  Linking: { openSettings: jest.fn() },
  Platform: { OS: 'android' },
}));

jest.mock('../NativeReactNativeLiteCard', () => ({
  __esModule: true,
  default: { setMnemonic: jest.fn() },
  CardErrors: {},
}));

import liteCard from '../index';
import nativeLiteCard from '../NativeReactNativeLiteCard';

const setMnemonic = jest.mocked(nativeLiteCard.setMnemonic);

beforeEach(() => setMnemonic.mockReset());

it.each([undefined, 'test-card-A', ''])(
  'forwards expected serial without dropping supplied values: %s',
  async (expectedSerial) => {
    setMnemonic.mockImplementation(
      (_mnemonic, _pwd, _overwrite, _serial, callback) => {
        callback(null, true, null);
      }
    );
    await expect(
      liteCard.setMnemonic('test-backup', '000000', false, expectedSerial)
    ).resolves.toEqual({ error: null, data: true, cardInfo: null });
    expect(setMnemonic).toHaveBeenCalledWith(
      'test-backup',
      '000000',
      false,
      expectedSerial ?? null,
      expect.any(Function)
    );
  }
);

it('preserves mismatch errors without retrying or enabling overwrite', async () => {
  const error = { code: 2003, message: '' };
  setMnemonic.mockImplementation(
    (_mnemonic, _pwd, _overwrite, _serial, callback) => {
      callback(error, null, null);
    }
  );
  await expect(
    liteCard.setMnemonic('test-backup', '000000', false, 'test-card-A')
  ).resolves.toEqual({ error, data: null, cardInfo: null });
  expect(setMnemonic).toHaveBeenCalledTimes(1);
});
