# Range Downloader Native Contract

Status: Native source updated; Android arm64 AAR/Nitro C++ and iOS wrapper host builds plus focused native range checks passed. Device transfer and cancellation acceptance remain pending.

The native downloader owns concurrent segment transfer, bounded retries,
cancel handles, and fallback to a caller's single-stream path. Android callers
may inspect `PermanentHttpException.code` and `TransientHttpException.code`
when a typed HTTP failure escapes a probe or segment. Only typed transient HTTP
and socket failures use the existing bounded retry budget; TLS, storage, and
unknown failures leave that loop immediately. iOS passes the original error or
HTTP status as the last element of its in-process result tuple, without a new
Nitro field. Unsupported ranges still fall back to a single stream. Cancelling
an active iOS run completes its pending continuation once; late callbacks cannot
restart its segments or complete it again. The owning updater must not parse
exception prose for status. Runtime acceptance of the enclosing update flow
remains with each updater.
