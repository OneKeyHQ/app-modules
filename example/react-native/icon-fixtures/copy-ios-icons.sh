#!/bin/sh
set -eu
fixture_directory=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
source_directory="$fixture_directory/native/icons"
destination="$1"
test -f "$source_directory/manifest.json"
count=$(find "$source_directory" -type f -name '*.svg' | wc -l | tr -d ' ')
if [ "$count" != 58 ]; then
  echo "error: Expected 58 SDK icon fixtures, found $count" >&2
  exit 1
fi
case "$destination" in
  */OneKeyIcons.bundle) ;;
  *) echo "error: Invalid SDK icon fixture destination" >&2; exit 1 ;;
esac
rm -rf "$destination"
mkdir -p "$destination"
cp "$source_directory/manifest.json" "$destination/"
cp "$source_directory/"*.svg "$destination/"
