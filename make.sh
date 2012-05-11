#!/bin/bash
mvn clean install \
&& rm -rf $HOME/apps/jaks \
&& mkdir $HOME/apps/jaks \
&& tar --directory "$HOME/apps/jaks" -xf "$(dirname 0)/jaks-examples/target/jaks-examples-0.0.1-SNAPSHOT.tar.gz"
