#!/bin/bash
# Prepares the public directory for both Vite and Firebase.

public_dir=$1
scala_ver=3.5.2

cp firebase.json $public_dir
cp index.html $public_dir
