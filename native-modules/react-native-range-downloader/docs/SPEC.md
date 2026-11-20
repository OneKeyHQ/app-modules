# Range Downloader Native Contract

Status: Native source updated; Android arm64 AAR/Nitro C++ and iOS wrapper host builds plus focused native range checks passed. Device transfer and cancellation acceptance remain pending.

The native downloader owns concurrent segment transfer, bounded retries,
cancel handles, and fallback to a caller's single-stream path. Android callers
observe `CancelHandle.aborted` only after explicit caller cancellation; an
internal segment fallback or failure stops sibling workers for that transfer
without marking the caller cancelled. A subsequent transfer can use the same
parent cancellation handle and gets independent worker-stop state.
Callers may inspect `PermanentHttpException.code` and `TransientHttpException.code`
when a typed HTTP failure escapes a probe or segment. Only typed transient HTTP
and socket or HTTP framing failures use the existing bounded retry budget; TLS, storage, and
unknown failures leave that loop immediately. A short 206 segment body is a
recoverable protocol failure: Android keeps the segment bytes for the next
caller retry and reports a typed network error. iOS passes the original error or
HTTP status as the last element of its in-process result tuple, without a new
Nitro field. Unsupported ranges still fall back to a single stream. A native
caller may set a maximum total size. A larger probed or restored total is
rejected before accepting the run. Callers without a maximum keep the existing
path. Cancelling
an active iOS run completes its pending continuation once. Cancellation reserves
the channel/task identity through task cancellation and artifact cleanup; repeated
cancel calls join that cleanup. Segment callbacks belong to the run that created
or adopted their URLSession task, so late callbacks cannot write to or complete a
replacement run. Cancellation waits for already-entered file writes before
deleting artifacts, while newly arriving callbacks cannot restart segments or
write files. The owning updater must not parse
exception prose for status. Runtime acceptance of the enclosing update flow
remains with each updater.

## Node entry point

`@onekeyfe/react-native-range-downloader/node` provides the Desktop range
downloader and firmware artifact store through ESM and CommonJS. Importing this
entry does not initialize React Native or require Electron. The range downloader
owns the existing probe, parallel and single-stream transfer, resume manifest,
bounded retries and deadline, cancellation, checksum validation, and atomic
publication. Its default transport uses Node HTTPS. Callers may inject a
transport, which can report structured `NodeRangeDownloadError` failures.
Known download error codes and integer HTTP statuses from 100 through 599
retain their meaning across ESM and CommonJS. An HTTP status is accepted only
for `HTTP_ERROR` and feeds the same bounded retry policy. The bundle updater
keeps its existing public download entry and translates errors at its boundary.
The range package does not depend on the bundle updater.

`FirmwareArtifactNode` owns the Desktop firmware cache. Its constructor takes
the managed cache directory and may accept a fetch implementation and artifact
error class. Electron supplies its session fetch and App error class. Standalone
Node uses its built-in fetch and the package error class. Existing hostname
admission, redirect policy, integrity checks, cache names, archive validation,
read limits, cancellation, and leases apply to both hosts. One authoritative
Desktop or standalone Node adapter owns each managed cache directory.

- Concurrent calls for the same firmware download token, task, and transaction
  share one transfer. Each successful caller is checked against its own size,
  checksum, and byte limit, and its own lease retains the returned artifact.
  Transaction cancellation stays in effect until its last lease and download
  are gone.
- A trusted SHA-256 can identify and promote a completed partial even without an
  expected size. If a range request for an incomplete partial returns HTTP 416,
  the store discards the partial and makes at most one request from byte zero
  under the original transfer deadline.
- Firmware ZIP validation counts files and directory entries toward the 4,096
  entry limit. Every archive must contain a materializable file, including when
  the caller omits expected entries.
- The store orders artifact removal with cache reuse, lease acquisition and
  reader opening. Active downloads, leases, readers, and archive materialization
  protect their artifacts during a sweep. At seven days, an inactive root-level
  `archive-<UUID>/` scratch directory may be removed. The sweep reports one
  deleted entry for the directory and the bytes of its regular files.
