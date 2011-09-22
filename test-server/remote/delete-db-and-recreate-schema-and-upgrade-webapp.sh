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

echo "Stopping tomcat..."
/etc/init.d/tomcat5 stop

while pgrep -f tomcat ; do
    echo "Waiting for tomcat to go away..."
    sleep 1
done

install -T --owner=root --group=cassandra --mode=u=rw,go=r ~/cassandra-schema.txt /usr/share/cassandra/cassandra-schema.txt

if [ -f "$pid_file" ]; then
    pid="$(cat "$pid_file")"

    echo "Stopping cassandra process $pid..."
    kill "$pid"
    rm "$pid_file"
fi

while pgrep -f cassandra ; do
    echo "Waiting for cassandra to go away..."
    sleep 1
done

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

echo "Removing old war..."
rm -rf /var/lib/tomcat5/webapps/policy-auction.war /var/lib/tomcat5/webapps/policy-auction

echo "Installing new war..."
install -T --owner=tomcat --group=tomcat --mode=u=rw,g=rw,o= ~/policy-auction.war /var/lib/tomcat5/webapps/policy-auction.war

echo "Restarting tomcat..."
/etc/init.d/tomcat5 restart

while ! pgrep -f tomcat ; do
    echo "Waiting for tomcat process to appear..."
    sleep 1
done

echo
echo "Done."
