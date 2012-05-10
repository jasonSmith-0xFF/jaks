#!/bin/bash
mvn clean install \
&& rm -rf $HOME/apps/jsaf \
&& mkdir $HOME/apps/jsaf \
&& tar --directory "$HOME/apps/jsaf" -xf "$(dirname 0)/jsaf-examples/target/jsaf-examples-0.0.1-SNAPSHOT.tar.gz"
