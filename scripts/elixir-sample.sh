#!/usr/bin/env bash

date
time ./scripts/_run.sh testdata/sample-elixir
date
open -a "Google Chrome" generated/elixir/reports/index.html
