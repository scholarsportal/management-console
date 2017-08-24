#!/bin/bash
echo 'Starting before-deploy.sh'
if [ "$TRAVIS_BRANCH" = 'master' ] || [ "$TRAVIS_BRANCH" = 'develop' ] || [ ! -z "$TRAVIS_TAG" ]; then
    if [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
        echo "Decrypting code signing key"
        openssl aes-256-cbc -K $encrypted_1349c3be5890_key -iv $encrypted_1349c3be5890_iv -in resources/travis/codesignkey.asc.enc -out codesignkey.asc -d
        gpg --fast-import codesignkey.asc
    fi
fi

targetDir=$TRAVIS_BUILD_DIR/target
mkdir -p $targetDir

currentGitCommit=`git rev-parse HEAD`;
projectVersion=`mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec`

if [ "$TRAVIS_BRANCH" = 'develop' ] || [ "$TRAVIS_BRANCH" = 'master' ] || [ ! -z "$TRAVIS_TAG" ]; then
   # copy to the project target dir
   cp $TRAVIS_BUILD_DIR/account-management-app/target/ama-*.war $targetDir/
   #make a copy of the beanstalk file using fixed name:
   cp $targetDir/ama*.war $targetDir/ama-latest.war
fi

if [ ! -z "$TRAVIS_TAG" ]; then
    # generate javadocs only for tagged releases
    echo "Generating  javadocs..."
    # the irodsstorageprovider is excluded due to maven complaining about it. This exclusion will likely be temporary.
    # same goes for duradmin and synctoolui due to dependencies on unconventional setup of org.duracloud:jquery* dependencies.
    mvn javadoc:aggregate -Dadditionalparam="-Xdoclint:none" -Pjava8-disable-strict-javadoc  --batch-mode -pl \!account-management-app
    cd $targetDir/site/apidocs
    zipFile=duracloud-management-console-${projectVersion}-apidocs.zip
    echo "Zipping javadocs..."
    zip -r ${zipFile} .
    mv ${zipFile} $targetDir/
    cd $targetDir
    rm -rf install site javadoc-bundle-options

    # generate signed checksum file
    sha512sum * > sha512sum.txt
    echo $GPG_PASSPHRASE | gpg --passphrase-fd 0 --clearsign sha512sum.txt
fi

cd $TRAVIS_BUILD_DIR
echo 'Completed before-deploy.sh'
