FROM maven:3.5-jdk-8 as maven
COPY . /opt/hunnor-dict/export-lucene
WORKDIR /opt/hunnor-dict/export-lucene
RUN mvn package

FROM alpine
COPY --from=maven /opt/hunnor-dict/export-lucene/target/export-lucene-1.0.0.jar /opt/export-lucene/
