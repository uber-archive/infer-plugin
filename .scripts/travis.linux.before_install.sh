#!/bin/sh

# Check dokumentation for more info about Infer installation on Linux
# https://github.com/facebook/infer/blob/master/INSTALL.md#pre-compiled-versions

# opam >= 1.2.0
# Python 2.7 <-- preinstalled in Travis
# pkg-config
# libffi >= 3.0
# Java (only needed for the Java analysis) <-- preinstalled in Travis
# gcc >= 4.7.2 or clang >= 3.1 (only needed for the C/Objective-C analysis) <-- preinstalled in Travis
# autoconf >= 2.63 and automake >= 1.11.1 (if building from git)

echo -e "\e[33mInstalling Infer dependencies\e[0m"
sudo apt-get update
sudo apt-get -o Dpkg::Options::="--force-confnew" upgrade -y
sudo apt-get -o Dpkg::Options::="--force-confnew" install -y  \
  autoconf \
  automake \
  build-essential \
  libffi-dev \
  libgmp-dev \
  libmpc-dev \
  libmpfr-dev \
  m4 \
  pkg-config \
  python-software-properties \
  unzip \
  zlib1g-dev

# Opam is broken on some Ubuntu versions
# Install Opam from official repository (http://opam.ocaml.org/doc/Install.html#Binarydistribution)
echo -e "\e[33mInstalling Opam\e[0m"
yes '' | sudo add-apt-repository ppa:avsm/ppa
sudo apt-get update
sudo apt-get install -y ocaml ocaml-native-compilers camlp4-extra opam

# Checkout Infer
echo -e "\e[33mCloning Infer from Github\e[0m"
git clone https://github.com/facebook/infer.git infer-sources
cd infer-sources
# Compile Infer
echo -e "\e[33mCompiling Infer\e[0m"
./build-infer.sh java
# Install Infer into your PATH
echo -e "\e[33mAdding Infer to PATH\e[0m"
export PATH=`pwd`/infer/bin:$PATH
