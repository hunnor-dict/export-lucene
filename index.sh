#!/bin/bash

HUNNOR_HOME=/opt/hunnor-dict

cd $HUNNOR_HOME/hunnor-export/android

rm -rf hunnor-lucene-index
rm -rf hunnor-lucene-spelling

# Index Norwegian-Hungarian
cp $HUNNOR_HOME/Dropbox/Public/Databases/HunNor-XML-NH.xml.gz ./
gunzip -f HunNor-XML-NH.xml.gz
java -jar target/hunnor-android-indexer-0.1.jar index file HunNor-XML-NH.xml no
rm HunNor-XML-NH.xml

# Index Hungarian-Norwegian
cp $HUNNOR_HOME/Dropbox/Public/Databases/HunNor-XML-HN.xml.gz ./
gunzip -f HunNor-XML-HN.xml.gz
java -jar target/hunnor-android-indexer-0.1.jar index file HunNor-XML-HN.xml hu
rm HunNor-XML-HN.xml

# Create spellchecking index
java -jar target/hunnor-android-indexer-0.1.jar spellcheck index

# Package
zip -q -9 hunnor-lucene-3.6.zip hunnor-lucene-index/* hunnor-lucene-spelling/*

# Cleanup
rm -rf hunnor-lucene-index
rm -rf hunnor-lucene-spelling
