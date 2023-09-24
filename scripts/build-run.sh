#!/usr/bin/env bash

date
time \
./scripts/_build.sh && \
./scripts/_run.sh
date
say "done with build run"
