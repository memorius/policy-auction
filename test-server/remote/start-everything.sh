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

start_cassandra=
if [ -f "$pid_file" ]; then
    if pgrep -f cassandra ; then
        echo "Cassandra process already exists."
    else
        echo "WARNING: PID file present but no cassandra process found - removing pid file."
        rm "$pid_file"
        start_cassandra="y"
    fi
elif pgrep -f cassandra ; then
    echo "WARNING: No PID file present but a cassandra process IS running - not starting new process."
else
    start_cassandra="y"
fi

if [ -n "$start_cassandra" ]; then
    echo "Starting cassandra..."
    cd /usr/local/cassandra
    su --login --command="bin/cassandra -p \"$pid_file\"" cassandra

    while [ ! -f "$pid_file" ]; do
        echo "Waiting for cassandra pid file..."
        sleep 1
    done

    echo "Sleeping 10s to allow cassandra time to start listening..."
    sleep 10
fi

if pgrep -f tomcat ; then
    echo "Tomcat process already exists."
else
    echo "Starting tomcat..."
    /etc/init.d/tomcat5 start

    while ! pgrep -f tomcat ; do
        echo "Waiting for tomcat process to appear..."
        sleep 1
    done
fi

echo
echo "Done."
