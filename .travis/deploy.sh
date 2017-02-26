cd `dirname $0`/..

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "--> releasing tag $TRAVIS_TAG"
    mvn -Prelease clean deploy --settings .travis/settings.xml -DskipTests=true -B -U
else
    echo "not on a tag -> keep snapshot version in pom.xml"
fi
