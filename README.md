# fitnesse-quickstart
An archetype that can be used to generate a Maven based FitNesse project.

[![Build Status](https://travis-ci.org/sitture/fitnesse-quickstart.svg?branch=master&style=flat-square)](https://travis-ci.org/sitture/fitnesse-quickstart) [![Maven Central](https://img.shields.io/maven-central/v/com.sitture/fitnesse-quickstart.svg?maxAge=300&style=flat-square)](http://search.maven.org/#search|ga|1|g:"com.sitture")  [![license](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000&style=flat-square)](https://raw.githubusercontent.com/sitture/fitnesse-quickstart/master/LICENSE)

# Usage

To generate the archetype, run:

__Batch__

```bash
mvn archetype:generate -DarchetypeGroupId=com.sitture -DarchetypeArtifactId=fitnesse-quickstart
```

__Interactive__

```bash
cd x #where x is your "workspace" directory
mvn archetype:generate
#filter by e.g. "fitnesse", or com.sitture, which is {groupId}:{artifactId}
#input artifactId etc.
```

The archetype will create a directory named after the 'artifactId' you supplied, which will contain the generated project.

## Starting FitNesse

```bash
# cd into your generated project directory
mvn clean test
```

By default, fitnesse will start running on port 8082 at `http://127.0.0.1:8082`. To run it on a different port:

```bash
mvn clean test -Dport=9090
```

## Running a FitNesse Suite (JUnit)

You can run the following to run a suite headlessly.

```bash
mvn clean test-compile failsafe:integration-test
```

By default, fitnesse will run `FitNesse.SuiteAcceptanceTests` suite.

To run a different suite:

```bash
mvn clean test-compile failsafe:integration-test -DsuitePath=FitNesse.SuiteAcceptanceTests
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
