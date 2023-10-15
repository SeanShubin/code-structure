#!/usr/bin/env bash

date
time ./scripts/_run.sh samples/sample-elixir
date
open -a "Google Chrome" generated/elixir/reports/index.html
