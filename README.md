![Build](https://github.com/hunnor-dict/export-lucene/workflows/Build/badge.svg)

Lucene indexer task and search module. The indexer creates the Lucene index for the native Android app. The app uses the search module. Lucene 3.6.2 is used because of compatibility issues with Android.

# Indexing

### Input

The indexer processes the XML export files `HunNor-XML-HN.xml` and `HunNor-XML-NH.xml`. See [`export-ant`](https://github.com/hunnor-dict/export-ant) for download instructions.

### Usage

1. Package the application with Maven.

1. Copy files in the `jars` directory and `export-lucene-indexer/target/export-lucene-indexer-1.0.0.jar` to Ant's classpath.

1. Define the task in the Ant build file:
```
<taskdef name="lucene" classname="net.hunnor.dict.lucene.AntTask"/>
```
1. Run the indexer task:
```
<lucene language="HU" main="hunnor-lucene-index" source="HunNor-XML-HN.xml" spelling="hunnor-lucene-spelling"/>
<lucene language="NO" main="hunnor-lucene-index" source="HunNor-XML-NH.xml" spelling="hunnor-lucene-spelling"/>
```

The main index is generated to the `hunnor-lucene-index` directory, the separate spell checking index to the `hunnor-lucene-spelling` directory. Indexing adds entries to the index in these directories.
