# Code Structure

## Prerequisites
- Verified with versions
  - `java -version`
    - openjdk version "18.0.1" 2022-04-19
    - OpenJDK Runtime Environment Corretto-18.0.1.10.1 (build 18.0.1+10-FR)
    - OpenJDK 64-Bit Server VM Corretto-18.0.1.10.1 (build 18.0.1+10-FR, mixed mode, sharing)
  - `mvn -version`
    - Apache Maven 3.9.1 (2e178502fcdbffc201671fb2537d0cb4b4cc58f8)
    - Maven home: /usr/local/Cellar/maven/3.9.1/libexec
    - Java version: 18.0.1, vendor: Amazon.com Inc., runtime: /Library/Java/JavaVirtualMachines/amazon-corretto-18.jdk/Contents/Home
    - Default locale: en_US, platform encoding: UTF-8
    - OS name: "mac os x", version: "12.6.8", arch: "x86_64", family: "mac"

## Usage
- test
  - `mvn test`
- build
  - `mvn package`
- run
  - `java -jar console/target/code-structure-console.jar`
