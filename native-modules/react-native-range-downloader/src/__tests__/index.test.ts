jest.mock('react-native-nitro-modules', () => ({
  NitroModules: {
    createHybridObject: jest.fn(() => ({
      download: jest.fn(),
      discardArtifacts: jest.fn(),
      cancel: jest.fn(),
      addDownloadListener: jest.fn(),
      removeDownloadListener: jest.fn(),
      getDownloadsDir: jest.fn(),
      getFirmwareArtifactCapabilities: jest.fn(),
      downloadFirmwareArtifact: jest.fn(),
      getFirmwareArtifactStatus: jest.fn(),
      cancelFirmwareArtifact: jest.fn(),
      discardFirmwareArtifact: jest.fn(),
      openFirmwareArtifact: jest.fn(),
      readFirmwareArtifact: jest.fn(),
      closeFirmwareArtifact: jest.fn(),
      materializeFirmwareArchive: jest.fn(),
      createFirmwareArtifactLease: jest.fn(),
      retainFirmwareArtifact: jest.fn(),
      releaseFirmwareArtifactLease: jest.fn(),
      reconcileFirmwareArtifactLeases: jest.fn(),
      sweepFirmwareArtifactOrphans: jest.fn(),
    })),
  },
}));

import { NitroModules } from 'react-native-nitro-modules';

import {
  FirmwareArtifactRoute,
  RangeDownloadChannel,
  ReactNativeRangeDownloader,
} from '../index';

const mockCreateHybridObject =
  NitroModules.createHybridObject as jest.MockedFunction<
    typeof NitroModules.createHybridObject
  >;
const mockRangeDownloader = mockCreateHybridObject.mock.results[0]?.value;

describe('react-native-range-downloader public API', () => {
  it('creates and exports the native hybrid object', () => {
    expect(mockCreateHybridObject).toHaveBeenCalledWith(
      'ReactNativeRangeDownloader'
    );
    expect(ReactNativeRangeDownloader).toBe(mockRangeDownloader);
  });

  it('keeps the legacy channel values stable', () => {
    expect(RangeDownloadChannel).toEqual({
      Bundle: 'bundle',
      Apk: 'apk',
      Chart: 'chart',
    });
  });

  it('exports the closed firmware route set without changing legacy channels', () => {
    expect(FirmwareArtifactRoute).toEqual({
      Domain: 'domain',
      PinnedIp: 'pinnedIp',
    });
  });
});
