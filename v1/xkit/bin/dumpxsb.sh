#!/bin/sh

#XSB file dumper
#Prints the contents of an xsb file in human-readable form

cp=
cp=$cp:$XMLBEANDIR/xbean.jar

java -classpath $cp com.bea.xbean.tool.XsbDumper $*

