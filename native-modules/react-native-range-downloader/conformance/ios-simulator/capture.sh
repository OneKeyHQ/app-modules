#!/bin/bash
# OCDS scenario capture — deterministically drives ONE scenario on the booted
# sim and writes an evidence bundle to evidence/<scenario>.json.
#   usage: capture.sh <scenario> [mode]
#   mode: normal (default) | kill-resume
set -u
HERE="$(cd "$(dirname "$0")" && pwd)"
source "$HERE/drive.sh" >/dev/null
SCN="${1:-clean}"
MODE="${2:-normal}"
OUT="$HERE/evidence/$SCN.json"
mkdir -p "$HERE/evidence"
EXPECT_SHA="$(curl -s "$SERVER/ocds/health" | python3 -c 'import json,sys;print(json.load(sys.stdin)["sha256"])')"

note() { echo ">>> $*" >&2; }
snap() { seg_snapshot 2>/dev/null | sed 's/^/    /'; }

note "[$SCN/$MODE] clean reinstall"
app_reinstall >&2
server_reset
set_scenario "$SCN" >&2

# kill-resume (T8 / SPEC #2): scenario MUST be delay-tail — head segs 0..4 finish
# fast, tail 5..7 hang. Once head is on disk we force-quit, switch to clean, and
# relaunch; the resume must REUSE the head segs (no restart from 0).
SEEDED_SEGS=""
START_LINE="$(log_lines)"

note "launch (silent strategy -> auto-download)"
app_launch >&2

# poll for download activity + terminal state
SEG_PEAK=0; KILLED=0; RESUMED_AT=""; GIVEUP_AT=""
for i in $(seq 1 140); do
  sleep 1
  segs=$(ls "$(dldir)" 2>/dev/null | grep -cE '\.seg[0-9]+' )
  [ "$segs" -gt "$SEG_PEAK" ] && SEG_PEAK=$segs
  # has the bundle download actually started? (gate terminal checks on this so
  # pre-download launch noise can't trip a false terminal)
  breq=$(server_log | python3 -c 'import json,sys;print(sum("bundle.zip" in r.get("path","") for r in json.load(sys.stdin)))' 2>/dev/null || echo 0)

  # kill-resume: once the head segments (>=4) are on disk, force-quit + relaunch.
  if [ "$MODE" = "kill-resume" ] && [ "$KILLED" -eq 0 ] && [ "$segs" -ge 4 ]; then
    SEEDED_SEGS=$(ls "$(dldir)" 2>/dev/null | grep -oE 'seg[0-9]+' | sed 's/seg//' | sort -nu | tr '\n' ' ')
    note "kill-resume: head segs [$SEEDED_SEGS] on disk at t=${i}s -> force-quit"
    app_terminate >&2
    KILLED=1
    sleep 2
    note "post-kill persisting: $(ls "$(dldir)" 2>/dev/null | grep -oE 'seg[0-9]+' | sort -u | tr '\n' ' ')"
    server_reset            # isolate the resume request set
    set_scenario clean >&2  # let the tail complete fast on relaunch
    RESUMED_AT=$i
    app_launch >&2
    continue
  fi

  # Terminal detection. ONLY break on a definitive end-state — never on an
  # intermediate "retry N/5" / transient line (those legitimately precede
  # recovery in transient-5xx/416/stall/short and must NOT cut the capture short).
  final=$(ls "$(dldir)" 2>/dev/null | grep -E '\.zip$' | grep -vcE '\.partial$')
  succ=$(tail_since "$START_LINE" 'concurrent completed successfully|existing file SHA256 valid' | head -1)
  # definitive terminal failures: assembled-sha mismatch (corrupt), or give-up budget exhausted (retry 5/5)
  shafail=$(tail_since "$START_LINE" 'valid=false|Bundle SHA256 verification failed|SHA256_MISMATCH' | head -1)
  budgetdone=$(tail_since "$START_LINE" 'retry 5/5' | head -1)
  if [ "$KILLED" = "1" ] && [ "$final" -lt 1 ]; then : ; # after a kill-resume, wait for the resumed final file
  elif [ -n "$succ" ] || { [ "$final" -ge 1 ] && [ "$SCN" != "give-up" ] && [ "$SCN" != "corrupt-bytes" ]; }; then note "terminal: SUCCESS at t=${i}s"; break; fi
  if [ "$breq" -ge 1 ] && [ -n "$shafail" ]; then note "terminal: SHA-MISMATCH at t=${i}s"; sleep 3; break; fi
  # give-up: "retry 5/5" only SCHEDULES the final retry (24s backoff). Wait ~28s
  # PAST it so the 5th attempt actually fires + the terminal failed-result logs.
  if [ "$breq" -ge 1 ] && [ -n "$budgetdone" ]; then
    [ -z "$GIVEUP_AT" ] && { GIVEUP_AT=$i; note "give-up: retry 5/5 seen at t=${i}s, waiting for final attempt"; }
    if [ $((i - GIVEUP_AT)) -ge 28 ]; then note "terminal: give-up complete at t=${i}s"; sleep 2; break; fi
  fi
done

# final assembled file sha (success scenarios): the *.zip that is not a .partial
FINAL_FILE="$(ls "$(dldir)"/*.zip 2>/dev/null | grep -v '\.partial$' | head -1)"
GOT_SHA=""; [ -n "$FINAL_FILE" ] && [ -f "$FINAL_FILE" ] && GOT_SHA="$(shasum -a 256 "$FINAL_FILE" | awk '{print $1}')"

# assemble evidence JSON
python3 - "$OUT" "$SCN" "$MODE" "$EXPECT_SHA" "$GOT_SHA" "$SEG_PEAK" "$KILLED" "${RESUMED_AT:-}" "${SEEDED_SEGS:-}" <<PY
import json, sys, subprocess, os
out, scn, mode, esha, gsha, peak, killed, resumed, seeded = sys.argv[1:10]
def sh(c):
    try: return subprocess.check_output(c, shell=True, text=True)
    except: return ""
srvlog = sh("curl -s $SERVER/ocds/log") or "[]"
try: srv = json.loads(srvlog)
except: srv = []
applog = sh("tail -n +$((${START_LINE}+1)) \"\$(source $HERE/drive.sh >/dev/null; applog)\" 2>/dev/null | grep -aE 'BundleUpdate|RangeDownloader|appUpdate|downloadBundle' | tail -120")
ev = {
  "scenario": scn, "mode": mode,
  "expectedSha": esha, "finalSha": gsha, "shaMatch": (esha==gsha and gsha!=""),
  "segPeak": int(peak), "killedMidDownload": killed=="1", "resumedAtSec": resumed,
  "seededSegs": [int(x) for x in seeded.split()] if seeded.strip() else [],
  "serverRequests": srv,
  "serverRangeStarts": [r.get("range") for r in srv if r.get("path","").endswith("bundle.zip")],
  "serverStatuses": [r.get("status") for r in srv if r.get("path","").endswith("bundle.zip")],
  "appLogTail": applog.splitlines(),
}
open(out,"w").write(json.dumps(ev, indent=2))
print("wrote", out, "| shaMatch=", ev["shaMatch"], "segPeak=", ev["segPeak"], "reqs=", len(srv))
PY
note "done -> $OUT"
