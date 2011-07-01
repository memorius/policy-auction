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

check_directory () {
    local directory="$1"

    if [ ! -d "$directory" ]; then
        exit_with_error "Directory does not exist: $directory. Try running install-cassandra-for-dev.sh and start-cassandra-for-dev.sh first" 3
    fi
}

trap trap_err ERR && set -o errexit

. conf/cassandra-dev-dirs.sh

check_directory "$cassandra_bin"
check_directory "$cassandra_conf"

full_conf_dir="$(readlink -mnq "${cassandra_conf}")"

# Tell cassandra where our config scripts are then start the cli, looking at local cassandra instance
export CASSANDRA_CONF="$full_conf_dir"
exec "$cassandra_bin/bin/cassandra-cli" --host localhost
