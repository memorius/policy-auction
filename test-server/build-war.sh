#!/bin/bash

trap_err () {
    err=$?

    echo 1>&2 "$0: in command $BASH_COMMAND
Error $err occurred, aborting."
}

print_error () {
    echo 1>&2 "$0: $1"
}

exit_with_error () {
    local message="$1"
    local exit_code=$2

    print_error "$message"
    if [ -z "$exit_code" ]; then
        exit_code=1
    fi
    exit $exit_code
}

trap trap_err ERR && set -o errexit

# Substitute our web.xml which configures http auth, then put back how it was after building
echo "Building war..."
mv src/main/webapp/WEB-INF/web.xml src/main/webapp/WEB-INF/web.unauthenticated.xml
cp src/main/webapp/WEB-INF/web.http-auth.xml src/main/webapp/WEB-INF/web.xml
build_result=0
mvn clean package || build_result=$?
mv src/main/webapp/WEB-INF/web.unauthenticated.xml src/main/webapp/WEB-INF/web.xml
exit $build_result
