#!/usr/bin/env bash

date
time ./scripts/_run.sh samples/sample-kotlin
date
open -a "Google Chrome" generated/kotlin/reports/index.html
