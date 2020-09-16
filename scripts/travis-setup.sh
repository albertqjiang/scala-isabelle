#!/bin/bash

set -ex

mkdir -p ~/bin
curl -Ls https://git.io/sbt > ~/install/sbt
chmod +x ~/install/sbt

mkdir -p ~/install
if ! [ -e /opt/Isabelle2020 ]; then
  case "$TRAVIS_OS_NAME" in
    linux) curl https://isabelle.in.tum.de/dist/Isabelle2020_linux.tar.gz | tar -x -z -C ~/install;;
    osx) curl https://isabelle.in.tum.de/dist/Isabelle2020_macos.tar.gz | tar -x -z -C ~/install;;
    *) echo "Unsupported OS: $TRAVIS_OS_NAME"; exit 1;;
  esac
fi

case "$TRAVIS_OS_NAME" in
  linux) ISABELLE_HOME=~/install/Isabelle2020;;
  osx) ISABELLE_HOME=~/install/Isabelle2020.app/Isabelle;;
  *) echo "Unsupported OS: $TRAVIS_OS_NAME"; exit 1;;
esac

echo "$ISABELLE_HOME" > .isabelle-home

# "$ISABELLE_HOME/bin/isabelle" build -b -v HOL-Analysis
