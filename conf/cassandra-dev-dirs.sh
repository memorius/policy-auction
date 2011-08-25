#!/bin/bash

# Config for cassandra install / start scripts in parent dir

replace_data_and_log_dir_placeholders () {
    local data_dir="$1"
    local log_dir="$2"
    shift 2
    for f ; do
        rm -f "$f"
        cp "$f.template" "$f"
        sed -i -r 's!%%cassandra_log_dir%%!'"${log_dir}!" "$f"
        sed -i -r 's!%%cassandra_data_dir%%!'"${data_dir}!" "$f"
    done
}

replace_data_dir_placeholders () {
    local data_dir="$1"
    shift
    for f ; do
        rm -f "$f"
        cp "$f.template" "$f"
        sed -i -r 's!%%cassandra_data_dir%%!'"${data_dir}!" "$f"
    done
}

platform_readlink () {
    local readlink_bin="$(which greadlink || which readlink)"
    if [ -z "$readlink_bin" ]; then
        exit_with_error "Cannot execute 'readlink' or 'greadlink'"
    fi

    "$readlink_bin" -mnq "$@"
}

# Relative to repos root dir
cassandra_bin='cassandra/cassandra-bin'
cassandra_log='cassandra/cassandra-log'
cassandra_data='cassandra/cassandra-data'
cassandra_conf='conf/cassandra-dev'

cassandra_unittest_data='target/cassandra-unit-test-data'
