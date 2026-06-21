#!/bin/bash
# OCDS simulator driver helpers — lifecycle + container inspection + server control.
# Source this: `source drive.sh`. Bundle id so.onekey.wallet.
SERVER=http://localhost:8788
APPID=so.onekey.wallet
DLDIR_REL="Documents/onekey-bundle-download"
LOG_REL="Library/Caches/logs/app-latest.log"

sim_id() {
  xcrun simctl list devices booted -j | python3 -c "
import json,sys
for rt,ds in json.load(sys.stdin).get('devices',{}).items():
  for d in ds:
    if d.get('state')=='Booted': print(d['udid']); sys.exit(0)
sys.exit(1)"
}
app_data() { xcrun simctl get_app_container "$(sim_id)" "$APPID" data 2>/dev/null; }
dldir() { echo "$(app_data)/$DLDIR_REL"; }
applog() { echo "$(app_data)/$LOG_REL"; }

# --- server scenario control ---
set_scenario() { curl -s -X POST "$SERVER/ocds/scenario" -d "{\"name\":\"$1\"}" >/dev/null; echo "scenario=$1"; }
server_reset() { curl -s -X POST "$SERVER/ocds/reset" >/dev/null; }
server_log()  { curl -s "$SERVER/ocds/log"; }
server_health(){ curl -s "$SERVER/ocds/health"; }

# --- app lifecycle ---
app_launch()    { xcrun simctl launch "$(sim_id)" "$APPID" >/dev/null 2>&1; echo "launched"; }
app_terminate() { xcrun simctl terminate "$(sim_id)" "$APPID" >/dev/null 2>&1; echo "terminated"; }
app_kill_relaunch() { app_terminate; sleep 1; app_launch; }
# background our app by foregrounding Settings (closest sim approximation to suspend)
app_background() { xcrun simctl launch "$(sim_id)" com.apple.Preferences >/dev/null 2>&1; echo "backgrounded (Settings fg)"; }
# clean slate between scenarios: wipe persisted update state + download dir.
APP_PATH="$HOME/Project/app-monorepo/apps/mobile/ios/build/Build/Products/Release-iphonesimulator/OneKeyWallet.app"
app_reinstall() {
  local id; id="$(sim_id)"
  xcrun simctl terminate "$id" "$APPID" >/dev/null 2>&1
  xcrun simctl uninstall "$id" "$APPID" >/dev/null 2>&1
  xcrun simctl install "$id" "$APP_PATH" >/dev/null 2>&1
  echo "reinstalled clean"
}

# --- download-dir inspection (segments / partial / final) ---
seg_snapshot() {
  local d; d="$(dldir)"
  if [ ! -d "$d" ]; then echo "(no download dir yet: $d)"; return; fi
  ls -la "$d" 2>/dev/null | grep -E "\.seg[0-9]+|\.partial|\.zip|\.resume" || echo "(empty)"
}
clear_dldir() { local d; d="$(dldir)"; rm -rf "$d" 2>/dev/null; echo "cleared $d"; }

# --- app-latest.log capture ---
log_lines() { wc -l < "$(applog)" 2>/dev/null || echo 0; }
# tail_since <startLine> [filterRegex]
tail_since() {
  local start="${1:-0}" filt="${2:-BundleUpdate|RangeDownloader|appUpdate|downloadBundle}"
  local f; f="$(applog)"; [ -f "$f" ] || { echo "(no log)"; return; }
  tail -n +"$((start+1))" "$f" | grep -aE "$filt"
}

echo "drive.sh loaded. sim=$(sim_id) appdata=$(app_data)"
