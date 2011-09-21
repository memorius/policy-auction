# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

CASSANDRA_HOME=/usr/local/cassandra

# The directory where Cassandra's configs live (required)
CASSANDRA_CONF=/usr/share/cassandra/conf


# This can be the path to a jar file, or a directory containing the 
# compiled classes. NOTE: This isn't needed by the startup script,
# it's just used here in constructing the classpath.
cassandra_bin=$CASSANDRA_HOME/build/classes/main
cassandra_bin=$cassandra_bin:$CASSANDRA_HOME/build/classes/thrift
#cassandra_bin=$cassandra_home/build/cassandra.jar

# JAVA_HOME can optionally be set here
JAVA_HOME="/usr/lib/jvm/java-6-oracle"

# The java classpath (required)
CLASSPATH=$CASSANDRA_CONF:$cassandra_bin

for jar in $CASSANDRA_HOME/lib/*.jar; do
    CLASSPATH=$CLASSPATH:$jar
done

# Credentials for SimpleAuthenticator as configured in cassandra.yaml
JVM_OPTS="-Dpasswd.properties=${CASSANDRA_CONF}/passwd.properties"
