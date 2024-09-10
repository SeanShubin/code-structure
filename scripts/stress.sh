#!/usr/bin/env bash

set -e

date
time ./scripts/_stress.sh "$@"
date

open -a "Google Chrome" "generated/stress-test-project/depth-$1-breadth-$2/generated/index.html"
say "Done with stress test of depth $1 and breadth $2"
