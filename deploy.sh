#!/bin/bash

echo ">>> Step 1: Instal all npm dependencies"
npm install

echo ">>> Step 2: Clean the repo"
sbt clean

echo ">>> Step 3: Perform full JS link (compile and build Scala.js code)"
sbt fullLinkJS

echo ">>> Step 4: Prepare public assets directory for hosting in Firebase"
./prep_public.sh public

echo ">>> Step 5: Instal firebase-tools (it's a node tool)"
npm install -g firebase-tools

echo ">>> Step 6: Build the website with Vite (vite build)"
npm run build

echo ">>> Step 7: Deploy to Firebase"
# This step (ie command `firebase deploy`) has some pre-requisites:
# - A Firebase service account with roles `Firebase Admin` and `Firebase App Distribution Admin`;
# - The service account must have a key in JSON format;
# - An environment variable GOOGLE_APPLICATION_CREDENTIALS whose value is the path to a temp CI file with the JSON key
#   content (this is done in the CI step, just before calling this script).
firebase deploy --public dist --project button-football-prj-aecdb
