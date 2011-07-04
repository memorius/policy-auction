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
        exit_with_error "Directory does not exist: $directory. Try running install-cassandra-for-dev.sh first" 3
    fi
}

trap trap_err ERR && set -o errexit

. conf/cassandra-dev-dirs.sh

check_directory "$cassandra_log"
check_directory "$cassandra_data"
check_directory "$cassandra_bin"
check_directory "$cassandra_conf"

# Copy templates and replace "%%cassandra_log_dir%%" and "%%cassandra_data_dir%%" placeholders
full_log_dir="$(readlink -mnq "${cassandra_log}")"
full_data_dir="$(readlink -mnq "${cassandra_data}")"
full_conf_dir="$(readlink -mnq "${cassandra_conf}")"

for f in "${cassandra_conf}/cassandra.yaml" "${cassandra_conf}/log4j-server.properties" ; do
    rm -f "$f"
    cp "$f.template" "$f"
    sed -i -r 's!%%cassandra_log_dir%%!'"${full_log_dir}!" "$f"
    sed -i -r 's!%%cassandra_data_dir%%!'"${full_data_dir}!" "$f"
done

# Tell cassandra where our config scripts are then start it in the foreground
export CASSANDRA_CONF="$full_conf_dir"

# Override cassandra auto-calculation of heap size for dev mode:
# it uses half the physical memory by default which is way too much for small-scale testing
export MAX_HEAP_SIZE="512M"
export HEAP_NEWSIZE="128M"

exec "$cassandra_bin/bin/cassandra" -f "$@"
