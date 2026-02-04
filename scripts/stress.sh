#!/usr/bin/env bash

set -e

date
time ./scripts/_stress.sh "$@"
date

open -a "Google Chrome" "generated/stress-test-project/depth-$2-breadth-$4/generated/index.html"
say "Done with stress test of depth $2 and breadth $4"
