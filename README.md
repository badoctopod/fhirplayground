# Fhirplayground

[![Build](https://github.com/badoctopod/fhirplayground/actions/workflows/ci_cd.yml/badge.svg)](https://github.com/badoctopod/fhirplayground/actions/workflows/ci_cd.yml)
   •   
**[Fhirplayground on Heroku](https://fhirplayground.herokuapp.com)**

Fhirplayground is my personal learning curve project which main goal is getting
basic understanding what [FHIR](https://www.hl7.org/fhir/) is.

Subgoal of this project is moving from my old setup to other devtools:
- emacs/spacemacs (*goodbye, mouse! ^_^*).
- Clojure CLI (tools.deps.alpha, tools.build).
- Kaocha test runner for ClojureScript.
- figwheel-main + npm + webpack.

## Local development

Start postgres:
```
cd bin
source .env
docker-compose up -d
```

For the first time, init db with FHIR schema and load FHIR data:
```
./fhirbase init
./fhirbase load mode=insert bundle.ndjson.gzip
```

In emacs/spacemacs issue command `cider-jack-in-clj&cljs`, choose ClojureScript REPL type `figwheel-main` and build id `dev`.

## Build locally

Build ClojureScript app:
```
clojure -M:fig:min
```

Build Clojure app:
```
clojure -T:build uberjar
```

## Deploy to Heroku

Based on GitHub actions, see `.github/workflows`.
