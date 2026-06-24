export const meta = {
  name: 'ocds-ios-sim-verify',
  description: 'Autonomously verify the iOS OCDS concurrent-download spec (SPEC §6 #1-#11) on the booted simulator',
  phases: [
    { title: 'Preflight', detail: 'server+sim health, prove the localhost download seam' },
    { title: 'Capture', detail: 'serially drive each OCDS scenario on the sim, dump evidence' },
    { title: 'Verify', detail: 'adversarially judge each evidence bundle vs the SPEC expectation' },
    { title: 'Synthesis', detail: 'SPEC §6 #1-#11 coverage table + verdict' },
  ],
};

const DIR = '/Users/huhuanming/Project/ocds-verify';

// Each scenario maps to SPEC §6 row(s). Capture is SERIAL (one simulator); verify is parallel.
const SCENARIOS = [
  { scenario: 'clean', mode: 'normal', spec: 'base T1/T2', what: 'concurrent multi-range download + concat',
    expect: 'serverRangeStarts has ~8 distinct 2MB windows tiling the 16MB file (beyond the 0-0 probe); appLogTail "concurrent completed successfully"; shaMatch=true. (segPeak unreliable — judge by distinct windows + shaMatch.)' },
  { scenario: 'delay-tail', mode: 'kill-resume', spec: '#2', what: 'app killed mid-run → resumes from completed segments',
    expect: 'seededSegs lists head segments on disk at force-quit (e.g. 0 1 2 3 4); after the kill the server log was reset, so serverRangeStarts on RESUME must NOT re-request those head windows from 0 (starts 0/2097152/4194304/6291456/8388608) — at most a 0-0 probe; shaMatch=true. THE ORIGINAL BUG: prove no restart-from-0.' },
  { scenario: 'range-ignored-probe', mode: 'normal', spec: '#3 (probe-200)', what: '200 to the Range probe → range unsupported → single-stream',
    expect: 'probe (0-0) got 200; NO concurrent segment GETs; appLogTail shows single-stream download; shaMatch=true.' },
  { scenario: 'range-ignored-segment', mode: 'normal', spec: '#3 (segment-200)', what: '206 probe but 200 to a segment → serverIgnoredRange → permanent fallback',
    expect: 'serverStatuses include 200 on a segment GET; appLogTail "[RangeDownloader] ... server returned 200 to a Range request" AND "[BundleUpdate] ... concurrent permanent fallback ... using single-stream"; a single non-range full GET (200) follows; segments cleaned; shaMatch=true.' },
  { scenario: 'transient-5xx', mode: 'normal', spec: '#1 / #4', what: '5xx on segments → backoff retry, segments kept, no restart',
    expect: 'serverStatuses go 503...→206 recovery; appLogTail per-segment "retry segment N attempt"; all ranges eventually 206; shaMatch=true.' },
  { scenario: 'range-416', mode: 'normal', spec: '#4 (416)', what: '416 is transient (re-evaluate size), not permanent',
    expect: 'serverStatuses include 416 then 206; appLogTail "416 — re-evaluating size before retry"; every 416 range later recovers as 206; shaMatch=true. (Guards the 416-must-not-be-permanent regression.)' },
  { scenario: 'short-body', mode: 'normal', spec: '#5 (short)', what: 'short 206 body → retry resumes the tail',
    expect: 'a range delivered fewer bytes than promised then was re-fetched/recovered; appLogTail shows truncation/retry; shaMatch=true.' },
  { scenario: 'overlong-206', mode: 'normal', spec: '#5 (over-long)', what: 'over-long 206 body (Content-Length+4096) → rejected, no corruption',
    expect: 'appLogTail "[RangeDownloader] ... segment N truncated/size mismatch (got ..., expected ...)"; segment retried then transient fallback → single-stream; FINAL shaMatch=true (no corruption survived). The point of #5: bad body rejected, final checksum passes.' },
  { scenario: 'misaligned-206', mode: 'normal', spec: '#5 (mis-aligned)', what: 'Content-Range off-by-one → bounds mismatch → permanent fallback',
    expect: 'appLogTail permanent-fallback reason "non-conforming 206 (multipart / range / total mismatch)"; "[BundleUpdate] ... using single-stream"; segments cleaned; FINAL shaMatch=true.' },
  { scenario: 'bad-total-206', mode: 'normal', spec: '#5 (bad total)', what: 'disagreeing Content-Range total → permanent fallback',
    expect: 'same permanent-fallback → single-stream path as mis-aligned; FINAL shaMatch=true.' },
  { scenario: 'corrupt-bytes', mode: 'normal', spec: '#6', what: 'corrupted assembly (wrong bytes, correct length) → checksum mismatch',
    expect: 'appLogTail "[BundleUpdate] ... concurrent finished, verifying SHA256..." then a SHA256_MISMATCH / update error; shaMatch=false; NO final .zip; bounded (exactly ONE concurrent range-set, no repeated/looping re-download). PASS = mismatch detected → artifacts discarded → terminal failure, no infinite loop. (OCDS 1.2 removed the old single-stream retry-once requirement, so the ABSENCE of a "using single-stream" line after a mismatch is CONFORMANT, not a deviation.)' },
  { scenario: 'flap', mode: 'normal', spec: '#7 (network toggle)', what: 'TCP resets mid-run → transient retry, progress never resets to 0',
    expect: 'serverStatuses/notes show connection resets then recovery; appLogTail retries; reported progress (if visible) is monotonic non-decreasing — never resets to 0; completed segments survive the resets; shaMatch=true.' },
  { scenario: 'stall', mode: 'normal', spec: '#10', what: 'stalled socket → stall watchdog cancels + retries',
    expect: 'appLogTail "stall watchdog cancelling segment N (no bytes for Ns)" + "retry segment N"; stalled ranges recover; shaMatch=true.' },
  { scenario: 'give-up', mode: 'normal', spec: '#9', what: 'persistent failure → bounded terminal give-up, no infinite loop',
    expect: 'serverStatuses all 500; appLogTail "retry 5/5 ... code=HTTP_500" (budget reached); shaMatch=false; NO final .zip; bounded (does not loop forever).' },
  { scenario: 'permanent-4xx', mode: 'normal', spec: '#3-adjacent (404)', what: '4xx permanent on range → single-stream fallback',
    expect: 'range got 404; appLogTail "concurrent permanent fallback ... using single-stream"; non-range full 200 GET; shaMatch=true.' },
];

phase('Preflight');
const pre = await agent(
  `Verify the OCDS sim harness is ready and PROVE the localhost download seam. Run bash:
  1. curl -s http://localhost:8788/ocds/health (must be ok).
  2. source ${DIR}/drive.sh; sim_id (must print a udid); app_data (must print a path).
  3. Prove the seam: \`source ${DIR}/drive.sh; curl -s -X POST localhost:8788/ocds/reset; set_scenario clean; app_reinstall; app_launch\` then poll up to 60s: \`curl -s localhost:8788/ocds/log | python3 -c 'import json,sys;d=json.load(sys.stdin);print(len(d),[r["path"] for r in d][:4])'\` until BOTH /ocds/manifest.json AND /ocds/bundle.zip requests appear (proves fetchConfig hit our server AND the silent strategy auto-started the concurrent download).
  Return {seamOk:boolean, sawManifest:boolean, sawBundle:boolean, note:string}. If no bundle request in 60s, seamOk=false and include \`source ${DIR}/drive.sh; tail_since 0 | tail -30\`.`,
  { label: 'preflight-seam', schema: { type: 'object', additionalProperties: true, required: ['seamOk'], properties: { seamOk: { type: 'boolean' }, sawManifest: { type: 'boolean' }, sawBundle: { type: 'boolean' }, note: { type: 'string' } } } },
);
log(`preflight: seamOk=${pre?.seamOk}`);
if (!pre?.seamOk) return { aborted: true, reason: 'localhost download seam not working', preflight: pre };

phase('Capture');
const captured = [];
for (const s of SCENARIOS) {
  const r = await agent(
    `Drive OCDS scenario "${s.scenario}" (mode ${s.mode}, SPEC ${s.spec}: ${s.what}) on the booted sim.
    Run: \`bash ${DIR}/capture.sh ${s.scenario} ${s.mode}\` (reinstalls clean, sets scenario, launches with silent auto-download, drives kill-resume if mode=kill-resume, writes ${DIR}/evidence/${s.scenario}.json).
    It polls up to ~110s. Then cat ${DIR}/evidence/${s.scenario}.json and return {scenario, spec:"${s.spec}", evidenceJson:<parsed object>, ranToTerminal:boolean, captureError:string?}. Do NOT judge pass/fail — just capture verbatim.`,
    { label: `capture:${s.scenario}`, phase: 'Capture', schema: { type: 'object', additionalProperties: true, required: ['scenario'], properties: { scenario: { type: 'string' }, spec: { type: 'string' }, evidenceJson: { type: 'object', additionalProperties: true }, ranToTerminal: { type: 'boolean' }, captureError: { type: 'string' } } } },
  );
  captured.push({ ...s, capture: r });
}

phase('Verify');
const verdicts = await parallel(
  captured.map((c) => () =>
    agent(
      `Adversarially verify ONE OCDS iOS scenario from captured evidence. Default to FAIL unless the evidence genuinely proves the expectation — cross-check serverStatuses + serverRangeStarts + appLogTail + shaMatch together; a single coincidental log line is NOT proof.
      Scenario: ${c.scenario} (SPEC ${c.spec}) — ${c.what}
      EXPECTATION: ${c.expect}
      Read ${DIR}/evidence/${c.scenario}.json (Read or cat). If a field is missing or the run didn't reach terminal, treat as a gap.
      Return {spec:"${c.spec}", scenario:"${c.scenario}", pass:boolean, provenBy:[exact evidence fields/values cited], gaps:[unproven bits], specDeviation:string?}. Only set specDeviation for a genuine departure from OCDS 1.2 (note: terminating on a checksum mismatch without a single-stream retry is CONFORMANT under 1.2, not a deviation).`,
      { label: `verify:${c.scenario}`, phase: 'Verify', schema: { type: 'object', additionalProperties: true, required: ['scenario', 'pass'], properties: { spec: { type: 'string' }, scenario: { type: 'string' }, pass: { type: 'boolean' }, provenBy: { type: 'array', items: { type: 'string' } }, gaps: { type: 'array', items: { type: 'string' } }, specDeviation: { type: 'string' } } } },
    ).then((v) => ({ ...v, what: c.what })),
  ),
);

phase('Synthesis');
const summary = await agent(
  `Build the final OCDS iOS-simulator conformance report from these per-scenario verdicts:\n${JSON.stringify(verdicts.filter(Boolean), null, 2)}\n
  Produce markdown: (1) a table mapping SPEC §6 rows #1-#11 to scenario(s) and pass/fail/partial with "proven by"; (2) call out any genuine specDeviation vs OCDS 1.2 (note: #6 terminating on a checksum mismatch with NO single-stream retry is CONFORMANT under 1.2 — do not report it as a deviation); (3) the residual items that are NOT covered here and WHY: #8 cancel (no cancel trigger exists in the iOS app code at all — needs a temp shim) and #11 two-concurrent-download (native single-flight guard — needs a shim or bg/main dual-dispatch), plus literal screen-lock (#7) which the simulator cannot do and which is behaviorally redundant with the background/kill path; (4) a blunt one-line VERDICT. Be honest about any FAIL or weak proof.`,
  { label: 'synthesis', schema: { type: 'object', additionalProperties: true, required: ['markdown', 'passCount', 'failCount'], properties: { markdown: { type: 'string' }, passCount: { type: 'number' }, failCount: { type: 'number' }, verdict: { type: 'string' }, specDeviations: { type: 'array', items: { type: 'string' } } } } },
);

return {
  preflight: pre,
  passCount: summary?.passCount,
  failCount: summary?.failCount,
  specDeviations: summary?.specDeviations,
  verdict: summary?.verdict,
  report: summary?.markdown,
  verdicts: verdicts.filter(Boolean),
};
