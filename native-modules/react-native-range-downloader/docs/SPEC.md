# Range Downloader Native Contract

Status: Native source updated; Android arm64 AAR/Nitro C++ and iOS wrapper host builds plus focused native range checks passed. Device transfer and cancellation acceptance remain pending.

The native downloader owns concurrent segment transfer, bounded retries,
cancel handles, and fallback to a caller's single-stream path. Android callers
observe `CancelHandle.aborted` only after explicit caller cancellation; an
internal segment fallback or failure stops sibling workers without marking the
caller cancelled, so the updater retains the fallback or original failure.
Callers may inspect `PermanentHttpException.code` and `TransientHttpException.code`
when a typed HTTP failure escapes a probe or segment. Only typed transient HTTP
and socket or HTTP framing failures use the existing bounded retry budget; TLS, storage, and
unknown failures leave that loop immediately. A short 206 segment body is a
recoverable protocol failure: Android keeps the segment bytes for the next
caller retry and reports a typed network error. iOS passes the original error or
HTTP status as the last element of its in-process result tuple, without a new
Nitro field. Unsupported ranges still fall back to a single stream. An Android
caller may set a maximum total size; a larger probed total is rejected before
segment workers start. Callers without a maximum keep the existing path. Cancelling
an active iOS run completes its pending continuation once. Cancellation reserves
the channel/task identity through task cancellation and artifact cleanup; repeated
cancel calls join that cleanup. Segment callbacks belong to the run that created
or adopted their URLSession task, so late callbacks cannot write to or complete a
replacement run. Cancellation waits for already-entered file writes before
deleting artifacts, while newly arriving callbacks cannot restart segments or
write files. The owning updater must not parse
exception prose for status. Runtime acceptance of the enclosing update flow
remains with each updater.
