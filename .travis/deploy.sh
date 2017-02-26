cd `dirname $0`/..

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$TRAVIS_TAG 1>/dev/null 2>/dev/null
    mvn -Prelease clean deploy --settings .travis/settings.xml -DskipTests=true -B -U
else
    echo "not on a tag -> keep snapshot version in pom.xml"
fi
