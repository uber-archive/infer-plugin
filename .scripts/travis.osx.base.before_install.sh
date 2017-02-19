#!/bin/bash

brew update
brew install android-sdk
eval echo \"y\" | android update sdk --no-ui --force --all --filter extra-android-m2repository || true
eval echo \"y\" | android update sdk --no-ui --force --all --filter extra-google-m2repository || true
eval echo \"y\" | android update sdk --no-ui --force --all --filter build-tools-23.0.2 || true
eval echo \"y\" | android update sdk --no-ui --force --all --filter build-tools-23.0.3 || true
eval echo \"y\" | android update sdk --no-ui --force --all --filter android-15 || true
eval echo \"y\" | android update sdk --no-ui --force --all --filter android-23 || true
export ANDROID_HOME=/usr/local/opt/android-sdk
brew install infer
