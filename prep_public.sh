#!/bin/bash
# Prepares the public directory for both Vite and Firebase.

public_dir=$1
scala_ver=3.5.2

rm -rf $public_dir/img
rm -rf $public_dir/scss

mkdir -p $public_dir/img
mkdir -p $public_dir/scss

cp -R target/scala-$scala_ver/classes/img/* $public_dir/img

cp firebase.json $public_dir
cp index.html $public_dir
