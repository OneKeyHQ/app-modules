jest.mock('react-native', () => ({
  Linking: { openSettings: jest.fn() },
  Platform: { OS: 'android' },
}));

jest.mock('../NativeReactNativeLiteCard', () => ({
  __esModule: true,
  default: { setMnemonic: jest.fn(), changePin: jest.fn() },
  CardErrors: {},
}));

import liteCard from '../index';
import nativeLiteCard from '../NativeReactNativeLiteCard';

const setMnemonic = jest.mocked(nativeLiteCard.setMnemonic);
const changePin = jest.mocked(nativeLiteCard.changePin);

beforeEach(() => {
  setMnemonic.mockReset();
  changePin.mockReset();
});

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

it.each([undefined, 'test-card-A', ''])(
  'forwards the PIN change identity without dropping supplied values: %s',
  async (expectedSerial) => {
    changePin.mockImplementation((_oldPin, _newPin, _serial, callback) => {
      callback(null, true, null);
    });
    await expect(
      liteCard.changePin('000000', '111111', expectedSerial)
    ).resolves.toEqual({ error: null, data: true, cardInfo: null });
    expect(changePin).toHaveBeenCalledWith(
      '000000',
      '111111',
      expectedSerial ?? null,
      expect.any(Function)
    );
  }
);

it('preserves PIN change mismatch errors without an unguarded retry', async () => {
  const error = { code: 2003, message: '' };
  changePin.mockImplementation((_oldPin, _newPin, _serial, callback) => {
    callback(error, null, null);
  });
  await expect(
    liteCard.changePin('000000', '111111', 'test-card-A')
  ).resolves.toEqual({ error, data: null, cardInfo: null });
  expect(changePin).toHaveBeenCalledTimes(1);
});
