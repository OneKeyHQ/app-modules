# OneKey Concurrent Download Standard (OCDS)

- **Version:** 1.3
- **Status:** Active
- **Last updated:** 2026-07-25
- **Applies to:** every implementation of OneKey's concurrent (multi-range)
  downloader — iOS (Swift), Android (Kotlin), Desktop (Node/Electron), and any
  future platform.

This document defines the behavior a concurrent downloader is expected to
provide. It is platform-agnostic: implementations differ in language and
transport, but must be **behaviorally equivalent** with respect to everything
described here. It is the reference; when an implementation and this document
disagree, the implementation is at fault.

---

## 1. Purpose & scope

The concurrent downloader fetches one file over **N parallel HTTP byte-range
connections** to improve throughput on high-latency / lossy networks (mobile is
the main beneficiary). It is used for OTA bundle, APK, and asset delivery.

It is, by design, a drop-in accelerator: a single, sequential **single-stream**
downloader remains the baseline, and the concurrent path falls back to it
whenever range downloading is not possible. Correctness never depends on the
concurrent path succeeding.

**In scope:** range discovery, segmentation, parallel fetch, on-disk layout,
resume, retry, integrity verification, finalization, fallback, progress,
cancellation, security, and background execution.

**Out of scope:** the single-stream downloader's internals, the OTA install
pipeline, signature/key management, and UI.

### 1.1 Firmware artifact profile

Firmware downloads use a dedicated artifact profile in this module. The legacy
`download(url, dest)` API for OTA bundles, APKs, and assets remains compatible;
firmware callers use opaque artifact and reader references and never choose an
absolute destination path.

The firmware profile supports two routes:

- `domain`: connect through normal platform DNS and HTTPS routing;
- `pinnedIp`: derive the canonical hostname from the HTTPS URL and connect the
  socket to a validated public `resolvedIp:443`.

For `pinnedIp`, URL authority, `Host`, TLS SNI, and certificate hostname
verification MUST all use the same canonical URL hostname. JavaScript MUST NOT
provide a second hostname field. Redirects MUST NOT change the canonical
hostname or escape the pinned destination policy.

The tuple `(canonical URL, expected size, expected SHA-256)` defines object
identity. Persisted segments, partial files, verified artifacts, materialized
archive children, and resume metadata MUST remain bound to that identity.
State from a different identity MUST NOT be reused.

A firmware artifact is not observable through the public API until all bytes
are durable, the exact size and SHA-256 match, and an atomic promotion succeeds.
Verification always happens before promotion. A checksum mismatch invalidates
all bytes for that object identity and is terminal for the current source.

Active firmware tasks, readers, leases, metadata, and file locks are
process-shared native state managed by one process singleton. Independent
React Native JS runtimes MUST NOT create separate native download truths. The
native layer is authoritative when JS runtimes initialize in either order.

iOS route behavior is intentionally different:

- `domain` uses a background `URLSession`, allowing the OS to continue eligible
  transfers while the app is suspended or terminated;
- `pinnedIp` uses a foreground session with the pinned transport protocol,
  because background sessions cannot use a custom `URLProtocol`. Process
  termination stops transport but preserves durable segments for resume after
  the next launch.

Android `domain` and `pinnedIp` routes use process-local streaming. Process
death stops transport and the next background bootstrap resumes from durable
state. This profile does not promise an Android foreground service or
WorkManager continuation.

---

## 2. Architecture

```
  caller (OTA / APK / asset)
        │  download(url, dest)
        ▼
  ┌─────────────────────────── Concurrent Downloader ───────────────────────────┐
  │                                                                              │
  │   ┌─────────┐      ┌──────────────┐      ┌───────────────────────────────┐  │
  │   │  Probe  │─────▶│ Range planner│─────▶│ Reconcile (reuse on-disk segs)│  │
  │   │ 1 byte  │ size │  N segments  │ plan │  skip complete / resume short │  │
  │   └─────────┘ etag └──────────────┘      └───────────────┬───────────────┘  │
  │      │ no range / 200 / too small                        │ fetch missing     │
  │      ▼                                  ┌────────┬────────┼────────┬───────┐ │
  │  [PERMANENT]                            ▼        ▼        ▼        ▼       │ │
  │      │                              worker0  worker1   ...     workerN-1  │ │  N parallel
  │      │                                 │        │        │        │       │ │  Range GETs
  │      │                                 ▼        ▼        ▼        ▼       │ │  (+ If-Range)
  │      │                            dest.seg0  dest.seg1  ...   dest.segN-1 │ │  on-disk
  │      │                                 └────────┴────┬───┴────────┘       │ │  segments
  │      │                                               │ all complete        │ │
  │      │                                               ▼                     │ │
  │      │                                    ┌────────────────────┐           │ │
  │      │                                    │ Assemble (ordered  │           │ │
  │      │                                    │ concat → .partial) │           │ │
  │      │                                    └─────────┬──────────┘           │ │
  │      │                                              ▼                       │ │
  │      │                                    ┌────────────────────┐           │ │
  │      │                                    │ Verify SHA256 / sig │           │ │
  │      │                                    └─────────┬──────────┘           │ │
  │      │                                       ok ▼      ▼ mismatch [PERMANENT]│ │
  │      │                              atomic rename .partial → dest           │ │
  │      └──────────────┐                          │                            │ │
  └─────────────────────┼──────────────────────────┼────────────────────────────┘
                        ▼                           ▼
            Single-stream fallback ──────────▶  dest (final, verified)
```

**Components**

- **Probe** — a single lightweight request (a 1-byte range) that learns total
  size, range support, and a validator (`ETag`) in one round trip. Background
  transports that cannot issue this kind of request use a separate foreground
  request for the probe only.
- **Range planner** — splits the total size into N contiguous segments.
- **Reconcile** — before fetching, inspects on-disk artifacts from a prior
  attempt and plans to fetch only what is missing or incomplete.
- **Workers** — N concurrent range GETs, each writing its own segment artifact.
- **Assembler** — concatenates completed segments, in order, into a `.partial`.
- **Verifier** — checks the assembled file against the expected whole-file
  checksum (and signature, where applicable) before promotion.
- **Single-stream fallback** — the sequential downloader used whenever the
  concurrent path is not possible.

---

## 3. On-disk model

The concurrent state is represented as **one artifact per segment**
(`<dest>.seg0 … <dest>.segN-1`), assembled into the final file by ordered
concatenation, then atomically renamed to `<dest>`.

```
  <dest>.seg0   ┐
  <dest>.seg1   │  completed segments persist on disk across
  …             │  attempts and across process restarts
  <dest>.segN-1 ┘
        │ concat (ordered)
        ▼
  <dest>.partial ──(verify ok)──atomic rename──▶ <dest>
```

This layout is deliberate. A background download on some platforms can only
deliver a whole file per connection and cannot do positioned writes into a
shared file, so a single pre-allocated file written by offset is not portable.
The per-segment layout is the one model that works everywhere. (See Appendix A.)

A size-based resume heuristic, if used, must operate on artifacts whose on-disk
size truthfully reflects durably-downloaded bytes — i.e. no full-size
pre-allocation that reports "complete" before the bytes exist.

---

## 4. Failure model

Every failure during a run resolves to exactly one of two classes, and the two
classes have **opposite** recoveries. The class is an explicit result of the
operation, never inferred from incidental side effects (such as whether some
files happen to remain on disk).

| Observed condition | Class | Required outcome |
| --- | --- | --- |
| Server answers `200` to a `Range` request | **Permanent** | discard segments, fall back to single-stream |
| Range unsupported / probe inconclusive | **Permanent** | single-stream from the start |
| Total size below the concurrency threshold | not a failure | skip concurrency, use single-stream |
| `Content-Range` window ≠ requested range | **Permanent** | reject the body, discard, fall back |
| Whole-file checksum/signature mismatch after assembly | **Permanent** | discard final + artifacts; terminal failure surfaced to caller (no infinite re-download). Re-fetching the same object would re-corrupt, so no automatic single-stream retry is required. |
| `401` / `403` (auth / expired signed URL) | **Permanent** (this URL) | stop; surface to caller to obtain a fresh signed URL; do not blindly retry the dead URL |
| `404` / `410` (not found / gone) | **Permanent** | terminal failure surfaced to caller (single-stream will also fail) |
| Rejected non-HTTPS redirect (per §5.9) | **Permanent** | abort, surface to caller |
| Connection lost / timeout / DNS / TLS error | **Transient** | retry the segment in place; keep artifacts |
| `429` / throttling / `5xx` (except `501` / `505`) | **Transient** | back off, retry; keep artifacts |
| `416` to a resume request | **Transient** | re-evaluate size; keep artifacts |
| App suspended or terminated mid-download | **Transient** | keep artifacts; resume on relaunch |
| Local disk I/O error | **Transient** | retry; keep artifacts |

Any condition not listed above is classified by default as: `4xx` → Permanent
(except `408` and `429` → Transient); `5xx` → Transient (except `501` and `505`
→ Permanent); anything else or unknown → Permanent. This default rule guarantees
that the "exactly one of two classes" property holds for every failure, listed
or not.

**Permanent** means concurrency is fundamentally unusable for this object;
already-fetched bytes are unsalvageable, so discarding them is correct.

**Transient** means the transfer was interrupted but is resumable; completed
segments are valuable and must be kept, and the concurrent path must resume
rather than restart.

> The most damaging mistake is classifying a Transient failure as Permanent: it
> throws away tens of megabytes already on disk and restarts from zero. A run
> must never restart from byte 0 while resumable bytes exist.

---

## 5. Coverage — what every implementation must handle

The following enumerates the situations an implementation is expected to cover
and the behavior it must exhibit. Each row is a behavior that conformance
testing (§6) exercises.

### 5.1 Discovery
- Determine total size and range support before segmenting; capture a validator
  when available.
- When range is unsupported or the file is below the size threshold, use
  single-stream and do not create concurrent artifacts.

### 5.2 Segmentation & assembly
- Split into N contiguous segments; fetch each over its own connection.
- Assemble only after every segment is complete, by ordered concatenation.
- Produce the final file atomically (assemble → durable flush → atomic rename);
  a half-written final file is never observable.
- All intermediate artifacts (segment files and the `.partial`) reside on the
  same filesystem/volume as the final destination, so the finalize rename is a
  true atomic rename; copying into place across volumes is not permitted.
- On success, remove all intermediate artifacts; the verified final file is
  authoritative.

### 5.3 Resume
- Completed segments persist across re-invocations and across process restarts.
- A re-invocation re-fetches only missing or incomplete segments; a completed
  segment is never re-downloaded.
- An incomplete segment resumes from its current persisted length where the
  transport allows, instead of restarting that segment.
- The run never restarts from byte 0 while completed segments or a valid partial
  exist.

### 5.4 Retry & backoff
- A transient segment error is retried in place (bounded; at least a few
  attempts) before it is allowed to fail the run.
- One segment's transient error does not end the whole run before that segment's
  retries are exhausted.
- Each request has a connection timeout and a bytes-stalled (no-progress)
  timeout, and the run has an overall deadline; a stalled transfer (no bytes
  received within the stall window) is a Transient timeout that triggers retry.
- Retries back off between attempts. Backoff uses jitter so that N segments do
  not retry in lockstep, and when the server sends a `Retry-After` header it
  overrides the computed backoff.
- Throttling (`429`) and server errors (`5xx`) are retried, not treated as
  fatal.

These timeouts, the stall window, the backoff bounds, and the deadline are
named, caller-tunable parameters; their default values are platform
configuration, not part of this standard.

### 5.5 Integrity
- A segment is accepted only when the response is `206` and its `Content-Range`
  matches the requested window exactly.
- A response that is `multipart/byteranges`, carries more than the single
  requested range, or whose `Content-Range` total disagrees with the probe total
  (or is an unknown `*` total) is rejected; a probe that cannot yield a concrete
  total is treated as range-unsupported → single-stream.
- A segment's body length equals its planned length; short and over-long bodies
  are rejected, and no bytes are written past a segment's planned end.
- The assembled file is verified by whole-file checksum (and signature where
  applicable) before it is promoted to the final path.
- Object identity is guarded consistently — either pin via `If-Range`, or wipe
  stale artifacts before reuse and rely on the whole-file checksum as the sole
  identity guarantee. A mixed, partial policy is not acceptable.

### 5.6 Fallback
- Single-stream fallback happens only for Permanent failures.
- When the single-stream path resumes and the server returns a full body
  (`200`), existing bytes are not appended onto; that stream restarts cleanly.
- If concurrent and single-stream share any resume state, it is valid for both;
  otherwise they do not share it.

### 5.7 Progress
- Reported progress is monotonic non-decreasing within a run, even though N
  workers report concurrently.
- Progress resets only on a genuine restart; a path or attempt switch does not
  move the bar backward without a real restart.

### 5.8 Cancellation
- An external cancellation stops in-flight work first, then deletes artifacts, so
  a still-running task cannot resurrect a just-deleted file.
- Cancellation is wired from the caller through to the in-flight run, not only an
  internal flag.
- At most one run may be active per destination. A second concurrent
  `download()` for the same destination either joins the in-flight run or fails
  fast; concurrent runs never co-write the same artifacts. A lock left behind by
  a crashed run must be reclaimable (stale-lock recovery), so a crash does not
  permanently block the destination.

### 5.9 Security
- All requests are HTTPS, enforced on the initial request and on every redirect
  hop; a redirect to a non-HTTPS URL is rejected.
- Errors surfaced outside the native/runtime layer do not leak internal
  filesystem paths; full detail is logged locally only.

### 5.10 Background execution
- Where the platform provides OS-level background transfer, a run continues
  across app suspension/termination and resumes on relaunch.

### 5.11 Termination & give-up
- Retry and resume are bounded by both a total attempt budget that persists
  across process restarts and an overall wall-clock deadline; when either is
  exhausted the whole download ends.
- The downloader always reaches a definitive terminal outcome — success, or a
  terminal failure with a reason — and surfaces it to the caller. It never
  silently loops or hangs across relaunches.
- If the single-stream fallback itself fails, that is the terminal failure of
  the whole download; the fallback is not a black hole.

---

## Trust boundary

This module provides **integrity** (the whole-file checksum), not
**authenticity**. The trust responsibilities are delegated as follows; they are
existing obligations made explicit here, not new mechanism inside this module:

- The expected checksum and size are supplied by the caller from a trusted,
  signed channel (for example, a signature-verified manifest checked against a
  pinned key). They are never read from the download response.
- Authenticity of an executable artifact is verified by the consumer with a
  signature over the final bytes, before install and again at load.
- Artifacts are stored in an app-private location.
- Anti-rollback / version-monotonicity is the consumer's responsibility.

---

## 6. Conformance scenarios

An implementation is conformant when it passes all of the following:

| # | Scenario | Expected result |
| --- | --- | --- |
| 1 | Network drops on some segments mid-run | Only affected segments are re-fetched on resume; no full restart |
| 2 | App suspended / killed mid-run | Resumes on relaunch from completed segments |
| 3 | Server returns `200` to a `Range` request | Classified Permanent; clean single-stream |
| 4 | `429` / `5xx` on a segment | Backoff + retry + keep; eventual success, no restart from 0 |
| 5 | Mis-aligned / short / over-long `206` | Rejected, no corruption; final checksum passes |
| 6 | Corrupted assembly | Checksum mismatch → discard artifacts → terminal failure surfaced to caller; no infinite loop (no automatic re-download of the same object) |
| 7 | Repeated lock / background / network-toggle stress | Reported progress never resets to 0 unless a Permanent failure occurred |
| 8 | Cancel mid-run | In-flight work stops, artifacts removed, nothing resurrected |
| 9 | Permanently failing object / exhausted budget | Download ends in bounded time with a terminal failure reported; no infinite loop across relaunches |
| 10 | Stalled socket (bytes stop) | Stall timeout fires → Transient retry |
| 11 | Two concurrent `download()` for the same dest | Serialized or one fails fast; artifacts never co-written |

Conformance is evaluated per behavior. An implementation that does not yet meet
all of the above is non-conformant; its open gaps are tracked in that platform's
own notes until closed. **This document records no implementation's state.**

---

## Appendix A. Platform constraints (non-normative)

- **iOS background `URLSession`** can use only download tasks (not data tasks)
  and delivers each task's body as one complete file at finish; positioned
  writes into a shared file are impossible. This is what forces the per-segment
  on-disk model (§3) and bounds sub-segment resume (§5.3, "where the transport
  allows"). The probe (§5.1) runs on a separate foreground request.
- **Always-streaming transports** (Android OkHttp, Node `http`) read the
  response body incrementally, so sub-segment resume and byte-level progress are
  readily achievable. They do not get OS-level background survival (§5.10) and
  rely on on-disk artifacts to resume on the next launch.

## Appendix B. Change log

- **1.2** (2026-06-22) — Removed the "retry once via single-stream" requirement
  on a whole-file checksum/signature mismatch (§4 failure table, §6 scenario 6).
  A mismatch after assembly is now simply Permanent → discard + terminal failure.
  Rationale: re-fetching the same object would re-corrupt, so the retry adds cost
  with no integrity benefit; all three reference implementations (iOS, Android,
  Node) already terminate on mismatch, confirming the retry was over-specified.
- **1.1** (2026-06-20) — Added a terminal / give-up boundary (§5.11: bounded
  attempt budget + wall-clock deadline, definitive terminal outcome, fallback
  failure is terminal). Made the failure classification exhaustive with a
  catch-all default rule and reworked the checksum-mismatch row to bound its
  retry. Added stall/connection timeouts and an overall deadline, plus jitter and
  `Retry-After` handling, to retry & backoff. Added per-destination single-flight
  (with stale-lock recovery) to cancellation. Required intermediate artifacts on
  the same filesystem for a true atomic rename. Required rejection of
  `multipart/byteranges` / extra-range / disagreeing-total responses. Added a
  Trust boundary section delegating authenticity, manifest-sourced
  checksum/size, app-private storage, and anti-rollback to the caller/consumer.
- **1.0** (2026-06-20) — Initial standard.
