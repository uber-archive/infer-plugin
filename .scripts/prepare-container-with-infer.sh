#!/bin/sh

wget -O Dockerfile https://raw.githubusercontent.com/facebook/infer/master/docker/Dockerfile

if ! docker --version > /dev/null; then
  echo "docker is not installed."
  exit 1
fi

if [ ! -f Dockerfile ]; then
  echo "Dockerfile not found."
  exit 1
fi

export INFER_IMAGE_NAME="infer"
docker build -t $INFER_IMAGE_NAME .
