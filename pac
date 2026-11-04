#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"
"$SCRIPT_DIR/gradlew" -q installDist
"$SCRIPT_DIR/build/install/pac/bin/pac" "$@"
