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

echo -n "Enter username on target server: "
read username

echo -n "Enter target hostname: "
read server_host

if [ -z "$username" ] || [ -z "$server_host" ]; then
    exit_with_error "Empty host or username" 2
fi

echo "Copying script to target server..."
scp test-server/remote/stop-everything.sh "${username}@${server_host}:~/"

echo "...copied ok, stopping everything..."
ssh -t "${username}@${server_host}" sudo "~/stop-everything.sh"
