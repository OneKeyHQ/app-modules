import { QrScannerError, toQrScannerError } from '../errors';

describe('toQrScannerError', () => {
  it('reads the code native code prefixes to the message', () => {
    const error = toQrScannerError(
      new Error('invalid-image: unreadable or unsupported image')
    );
    expect(error).toBeInstanceOf(QrScannerError);
    expect(error.code).toBe('invalid-image');
    expect(error.message).toBe('unreadable or unsupported image');
  });

  it('finds the code after a platform prefix', () => {
    expect(
      toQrScannerError(
        new Error('java.lang.UnsupportedOperationException: decoder-unavailable: not yet')
      ).code
    ).toBe('decoder-unavailable');
  });

  it('drops the stack trace Android appends to the message', () => {
    const error = toQrScannerError(
      new Error(
        'invalid-image: unreadable or unsupported image\n\tat com.margelo.nitro.qrscanner.QrImageDecodingPipeline.loadOriented(QrImageDecodingPipeline.kt:125)\n\tat java.lang.Thread.run(Thread.java:1012)\n'
      )
    );
    expect(error.code).toBe('invalid-image');
    expect(error.message).toBe('unreadable or unsupported image');
  });

  it('maps unknown failures to decoder-unavailable', () => {
    const error = toQrScannerError('boom');
    expect(error.code).toBe('decoder-unavailable');
    expect(error.message).toBe('boom');
  });

  it('passes a QrScannerError through', () => {
    const original = new QrScannerError('resource-limit', 'too big');
    expect(toQrScannerError(original)).toBe(original);
  });
});
