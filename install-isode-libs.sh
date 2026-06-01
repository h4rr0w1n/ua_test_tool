#!/bin/bash
echo "Installing Isode and ATTech local libraries to Maven local repository..."

mvn install:install-file -Dfile=lib/isode-x400.jar -DgroupId=com.isode.x400 -DartifactId=isode-x400 -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-lib.jar -DgroupId=com.isode -DartifactId=isode-lib -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-asn.jar -DgroupId=com.isode -DartifactId=isode-asn -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-crypto.jar -DgroupId=com.isode -DartifactId=isode-crypto -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-dsapi.jar -DgroupId=com.isode -DartifactId=isode-dsapi -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-dsapigui.jar -DgroupId=com.isode -DartifactId=isode-dsapigui -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-emmash.jar -DgroupId=com.isode -DartifactId=isode-emmash -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-hlxja.jar -DgroupId=com.isode -DartifactId=isode-hlxja -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-mvc.jar -DgroupId=com.isode -DartifactId=isode-mvc -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-nettrace.jar -DgroupId=com.isode -DartifactId=isode-nettrace -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-rbac.jar -DgroupId=com.isode -DartifactId=isode-rbac -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/isode-ca.jar -DgroupId=com.isode -DartifactId=isode-ca -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/jswrapper.jar -DgroupId=com.isode -DartifactId=jswrapper -Dversion=1.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=lib/com.attech.amhs.ua.db.jar -DgroupId=com.attech.amhs.ua -DartifactId=com.attech.amhs.ua.db -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/com.attech.amhs.ua.common.jar -DgroupId=com.attech.amhs.ua -DartifactId=com.attech.amhs.ua.common -Dversion=1.0.0 -Dpackaging=jar

echo "Installation complete."
