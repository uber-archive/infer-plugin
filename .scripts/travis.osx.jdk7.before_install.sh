#!/bin/bash

# upgrade brew cask
brew update && brew upgrade caskroom/cask/brew-cask && brew cleanup && brew cask cleanup
# remove existing Java
sudo rm -rf /Library/Java/JavaVirtualMachines
# install Java 7
brew cask install caskroom/versions/java7
javac -version
# install Android and Infer
source .scripts/travis.osx.base.before_install.sh
