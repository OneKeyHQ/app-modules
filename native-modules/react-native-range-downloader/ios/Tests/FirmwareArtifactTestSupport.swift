@testable import RangeDownloadLogic

func prepareFirmwareArtifactForTest(
  store: FirmwareArtifactStore,
  artifactRef: String,
  leaseRef: String,
  taskId: String,
  artifactId: String,
  canonicalURL: String,
  hostname: String,
  route: StoredFirmwareArtifactRoute,
  expectedSize: Int64,
  expectedSha256: String,
  strongETag: String?,
  ranges: [(start: Int64, end: Int64)]
) throws -> StoredFirmwareArtifactMetadata {
  let preparation = try store.planTransferPreparation(
    artifactRef: artifactRef,
    leaseRef: leaseRef,
    taskId: taskId,
    artifactId: artifactId,
    canonicalURL: canonicalURL,
    hostname: hostname,
    route: route,
    expectedSize: expectedSize,
    expectedSha256: expectedSha256,
    strongETag: strongETag,
    lastModified: nil,
    ranges: ranges,
    usesRange: true
  )
  return try store.prepareArtifactForDownload(
    preparation: preparation,
    artifactRef: artifactRef,
    leaseRef: leaseRef,
    taskId: taskId,
    artifactId: artifactId,
    canonicalURL: canonicalURL,
    hostname: hostname,
    route: route,
    expectedSize: expectedSize,
    expectedSha256: expectedSha256,
    strongETag: strongETag,
    lastModified: nil,
    ranges: ranges,
    usesRange: true
  )
}
