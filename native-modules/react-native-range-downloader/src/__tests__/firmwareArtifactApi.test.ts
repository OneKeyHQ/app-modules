import type {
  FirmwareArchiveMaterializeParams,
  FirmwareArtifactCapabilities,
  FirmwareArtifactDownloadParams,
  FirmwareArtifactLeaseReleaseParams,
  FirmwareArtifactReaderReadParams,
} from '../index';

describe('firmware artifact API contract', () => {
  it('keeps the native capability gate explicit', () => {
    const capabilities: FirmwareArtifactCapabilities = {
      firmwareArtifactProtocolVersion: 2,
      supportedRouteTypes: ['domain', 'pinnedIp'],
      supportsArchiveMaterialization: true,
      maxReadBytes: 256 * 1024,
    };

    expect(capabilities).toEqual({
      firmwareArtifactProtocolVersion: 2,
      supportedRouteTypes: ['domain', 'pinnedIp'],
      supportsArchiveMaterialization: true,
      maxReadBytes: 262_144,
    });
  });

  it('does not expose a destination path, hostname, or arbitrary headers', () => {
    const download: FirmwareArtifactDownloadParams = {
      taskId: 'task-1',
      leaseRef: 'lease-1',
      artifactId: 'firmware-main',
      url: 'https://downloads.example.com/firmware.bin',
      routeType: 'pinnedIp',
      resolvedIp: '93.184.216.34',
      expectedSize: 79 * 1024 * 1024,
      expectedSha256: 'a'.repeat(64),
      maxBytes: 80 * 1024 * 1024,
    };

    expect(Object.keys(download)).not.toEqual(
      expect.arrayContaining(['destFilePath', 'hostname', 'headers'])
    );
  });

  it('models bounded binary reads with opaque reader ids', () => {
    const read: FirmwareArtifactReaderReadParams = {
      readerId: 'reader-1',
      offset: 0,
      length: 256 * 1024,
    };

    expect(read).toEqual({
      readerId: 'reader-1',
      offset: 0,
      length: 262_144,
    });
  });

  it('models policy-driven archive materialization and safe lease dispositions', () => {
    const materialize: FirmwareArchiveMaterializeParams = {
      leaseRef: 'lease-1',
      parentArtifactId: 'firmware-archive',
      archiveArtifactRef: 'artifact-archive',
      archiveImmutableToken: 'immutable-archive',
      materializationPolicy: 'v3-resource-files-v1',
    };
    const release: FirmwareArtifactLeaseReleaseParams = {
      leaseRef: 'lease-1',
      disposition: 'safeAbandoned',
    };

    expect(materialize.materializationPolicy).toBe('v3-resource-files-v1');
    expect(release.disposition).toBe('safeAbandoned');
  });
});
