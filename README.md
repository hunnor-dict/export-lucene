![Build](https://github.com/hunnor-dict/export-lucene/workflows/Build/badge.svg)

Command line Java application for creating the Lucene index export of the dictionary.

# Usage

To get the input files, follow the instructions in [`export-ant`](https://github.com/hunnor-dict/export-ant) to download the database dump files `HunNor-XML-HN.xml` and `HunNor-XML-NH.xml`.

Package the application with Maven.

To generate the database:

`java -jar export-lucene-indexer/target/export-lucene-indexer-1.0.0.jar -l hu HunNor-XML-HN.xml -d hunnor-lucene-index -s hunnor-lucene-spelling`  
`java -jar export-lucene-indexer/target/export-lucene-indexer-1.0.0.jar -l no HunNor-XML-NH.xml -d hunnor-lucene-index -s hunnor-lucene-spelling`

The main index is generated to the `hunnor-lucene-index` directory, the separate spell checking index to the `hunnor-lucene-spelling` directory.
