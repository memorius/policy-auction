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

echo "Removing old war..."
rm -rf /var/lib/tomcat5/webapps/policy-auction.war /var/lib/tomcat5/webapps/policy-auction

echo "Installing new war..."
install -T --owner=tomcat --group=tomcat --mode=u=rw,g=rw,o= ~/policy-auction.war /var/lib/tomcat5/webapps/policy-auction.war

echo "Restarting tomcat..."
/etc/init.d/tomcat5 restart

echo "Done."
