#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven was not found. Install Maven 3.8+ and add it to PATH." >&2
  exit 1
fi
mvn clean compile exec:java
