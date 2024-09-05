#!/usr/bin/env bash

date
time \
./scripts/_clean.sh && \
./scripts/_build_skip_tests.sh && \
./scripts/_run.sh
date
say "done with clean test build run skip tests"
open -a "Google Chrome" "generated/self/index.html"
