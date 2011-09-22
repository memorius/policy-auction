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

pid_file=/var/run/cassandra/cassandra.pid

if [ "$(whoami)" != "root" ]; then
    exit_with_error "Error: must be run as root (try 'sudo $0')"
fi

if [ ! -f "$pid_file" ]; then
    exit_with_error "Error: PID file $pid_file does not exist"
fi

pid="$(cat "$pid_file")"

echo "Stopping cassandra process $pid..."
kill "$pid"
rm "$pid_file"

while pgrep -f cassandra ; do
    echo "Waiting for cassandra to go away..."
    sleep 1
done

echo
echo "Done."
