#!/bin/sh

readonly installerFolder=$(pwd)
readonly netconfbrowserVersion="1.0"
readonly netconfbrowserKitLocation="${installerFolder}/../target/NetconfBrowserApp-${netconfbrowserVersion}.tar.gz"

echo "Building new Netconf Browser archive in ${installerFolder}/tar"
cd ../../
mvn clean install -Prelease		#	This generates /home/draganb/NetconfBrowser/gui/target/NetconfBrowserApp-1.0.tar.gz

rm -rf "${installerFolder}/tar"
mkdir -p "${installerFolder}/tar"
cp ${netconfbrowserKitLocation} ${installerFolder}/tar

cd ${installerFolder}/tar
tar -zxf ${installerFolder}/tar/NetconfBrowserApp-${netconfbrowserVersion}.tar.gz
rm -rf "${installerFolder}/tar/NetconfBrowserApp"

cp NetconfBrowserApp-${netconfbrowserVersion}.tar.gz ~/NetconfBrowser/resources/

pwd