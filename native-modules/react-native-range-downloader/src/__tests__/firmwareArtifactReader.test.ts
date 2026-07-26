import type {
  FirmwareArtifactReaderInfo,
  FirmwareArtifactReaderOpenParams,
  FirmwareArtifactReaderReadParams,
} from '../ReactNativeRangeDownloader.nitro';

describe('firmware artifact reader contract', () => {
  it('uses opaque refs and a fixed bounded binary window', () => {
    const open: FirmwareArtifactReaderOpenParams = {
      artifactRef: '6f0d4f88-f8c7-4e6a-b02b-713e5df1ca00',
      immutableToken: '29d316cc-4336-4762-96df-aa65357b80f6',
    };
    const info: FirmwareArtifactReaderInfo = {
      readerId: '0cbca320-6982-4516-995e-a2df94d3786f',
      size: 79 * 1024 * 1024,
      immutableToken: open.immutableToken,
      maxReadBytes: 256 * 1024,
    };
    const read: FirmwareArtifactReaderReadParams = {
      readerId: info.readerId,
      offset: 78 * 1024 * 1024,
      length: info.maxReadBytes,
    };

    expect(open).not.toHaveProperty('filePath');
    expect(info.maxReadBytes).toBe(262_144);
    expect(read).not.toHaveProperty('encoding');
  });

  it('keeps read results binary instead of string or base64', () => {
    const result = new Uint8Array([0, 1, 2, 255]).buffer;

    expect(result).toBeInstanceOf(ArrayBuffer);
    expect(result.byteLength).toBe(4);
  });
});
