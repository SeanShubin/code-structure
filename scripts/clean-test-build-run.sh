#!/usr/bin/env bash

date
time \
./scripts/_clean.sh && \
./scripts/_test.sh && \
./scripts/_build.sh && \
./scripts/_run.sh
date
say "done with clean test build run"
open -a "Google Chrome" "generated/self/index.html"
