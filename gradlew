#!/usr/bin/env bash
# Wrapper script to delegate Gradle commands to the Android project under tic_tac_toe_frontend.
# This allows CI or tooling that runs from the repo root to build successfully.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/tic_tac_toe_frontend"

if [ ! -f "${PROJECT_DIR}/gradlew" ]; then
  echo "Error: gradlew not found in ${PROJECT_DIR}" >&2
  exit 127
fi

cd "${PROJECT_DIR}"
# Forward all arguments to the inner Gradle wrapper
exec bash ./gradlew "$@"
