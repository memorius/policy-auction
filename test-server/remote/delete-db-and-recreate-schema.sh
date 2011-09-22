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

if pgrep -f tomcat ; then
    exit_with_error "Error: a tomcat process is still running"
fi

if [ -f "$pid_file" ]; then
    exit_with_error "Error: PID file $pid_file exists - check cassandra is not running then delete it"
fi

if pgrep -f cassandra ; then
    exit_with_error "Error: a cassandra process is still running"
fi

install -T --owner=root --group=cassandra --mode=u=rw,go=r ~/cassandra-schema.txt /usr/share/cassandra/cassandra-schema.txt

echo "Deleting old data..."
rm -rf /var/lib/cassandra/commitlog /var/lib/cassandra/data /var/lib/cassandra/saved_caches

echo "Starting cassandra..."
cd /usr/local/cassandra
su --login --command="bin/cassandra -p \"$pid_file\"" cassandra

while [ ! -f "$pid_file" ]; do
    echo "Waiting for cassandra pid file..."
    sleep 1
done

while ! pgrep -f cassandra ; do
    echo "Waiting for cassandra process to appear..."
    sleep 1
done

echo "Sleeping 10s to allow cassandra time to start listening..."
sleep 10

echo "Setting up new schema..."
password="$(sed -nr '/^webapp=/{s:^webapp=(.*)$:\1:p}' /usr/share/cassandra/conf/passwd.properties)"
su --login --command="bin/cassandra-cli --username webapp --password \"${password}\" --host localhost -f /usr/share/cassandra/cassandra-schema.txt" cassandra

echo
echo "Done."
