#!/bin/bash
set -e
source .env
./fhirbase --nostats -d fhirbase init
./fhirbase --nostats -d fhirbase load --mode=insert bundle.ndjson.gzip
