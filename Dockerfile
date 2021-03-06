FROM maven:3.6-jdk-11 as maven
COPY . /opt/hunnor-dict/export-lucene
WORKDIR /opt/hunnor-dict/export-lucene
RUN mvn verify

FROM alpine
COPY --from=maven /opt/hunnor-dict/export-lucene/jars/*.jar /opt/export-lucene/
COPY --from=maven /opt/hunnor-dict/export-lucene/export-lucene-indexer/target/export-lucene-indexer-1.0.0.jar /opt/export-lucene/
