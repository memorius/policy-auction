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

. conf/cassandra-dev-dirs.sh

# Copy templates and replace "%%cassandra_log_dir%%" and "%%cassandra_data_dir%%" placeholders
full_data_dir="$(platform_readlink "${cassandra_unittest_data}")"

# Set absolute paths in the config file - TestCassandra reads them programmatically after parsing the file
replace_data_dir_placeholders "${full_data_dir}" \
    "${cassandra_conf}/cassandra.test.yaml"

# Put the created files in the test classpath where TestCassandra can get them
mv "${cassandra_conf}/cassandra.test.yaml" "src/test/resources/cassandra.test.yaml"
cp "conf/cassandra-schema.txt" "src/test/resources/cassandra-schema.test.txt"

# The rest is done by the class _fixtures.TestCassandra
