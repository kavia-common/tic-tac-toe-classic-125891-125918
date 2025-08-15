#!/usr/bin/env bash
# Delegates Gradle commands to the Android project's wrapper inside tic_tac_toe_frontend.
# This file is added to support CI environments that execute `./gradlew` from the repository root.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
INNER_WRAPPER="${SCRIPT_DIR}/tic_tac_toe_frontend/gradlew"

if [ ! -f "${INNER_WRAPPER}" ]; then
  echo "Inner Gradle wrapper not found at: ${INNER_WRAPPER}" >&2
  exit 127
fi

exec "${INNER_WRAPPER}" "$@"
