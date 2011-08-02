#!/bin/bash

cassandra_version='0.8.2'

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

check_not_directory () {
    local directory="$1"

    if [ -d "$directory" ]; then
        exit_with_error "Directory already exists: $directory" 3
    fi
}

trap trap_err ERR && set -o errexit

. conf/cassandra-dev-dirs.sh

check_not_directory "$cassandra_log"
check_not_directory "$cassandra_data"
check_not_directory "$cassandra_bin"

cassandra_tar="downloaded/apache-cassandra-${cassandra_version}-bin.tar.gz"

tar xzvf "$cassandra_tar"
mkdir -p "$cassandra_bin"
rmdir "$cassandra_bin"
mv "apache-cassandra-${cassandra_version}" "$cassandra_bin"
mkdir -p "$cassandra_log" "$cassandra_data"

# We have our own modified copy of this in git
rm -r "$cassandra_bin/conf"

case "`uname`" in
    Linux)
        cp "downloaded/jna.jar" "$cassandra_bin/lib/"
        limit="$(ulimit -H -l)"
        if [ "$limit" != "unlimited" ]; then
            echo
            echo "memlock limit is too low ($limit):"
            echo "To use JNA (which improves memory usage and compaction on linux) you need to edit /etc/security/limits.conf,"
            echo "see docs/Running in dev mode.txt"
        fi
        ;;
    *)
        ;;
esac
