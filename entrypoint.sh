#!/bin/sh

exec java -Djava.security.egd=file:/dev/./urandom ${JAVA_OPTS} -jar app.jar