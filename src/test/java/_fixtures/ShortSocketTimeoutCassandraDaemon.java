/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package _fixtures;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.CassandraServer;
import org.apache.cassandra.thrift.CustomTThreadPoolServer;
import org.apache.cassandra.thrift.TBinaryProtocol;
import org.apache.cassandra.thrift.TCustomServerSocket;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modified version of CassandraDaemon from 0.8.2 release, patched to use a TServerSocket implementation
 * that sets a short socket timeout for "accept" so the {@link #stopServer()} actually shuts down promptly.
 *
 * @author Nick Clarke
 */
public class ShortSocketTimeoutCassandraDaemon extends org.apache.cassandra.service.AbstractCassandraDaemon
{
    private static final int ACCEPT_SOCKET_TIMEOUT_MILLIS = 5000;

    private static Logger logger = LoggerFactory.getLogger(ShortSocketTimeoutCassandraDaemon.class);
    private ThriftServer server;

    @Override
    protected void startServer()
    {
        if (server == null)
        {
            server = new ThriftServer(listenAddr, listenPort);
            server.start();
        }
    }

    @Override
    protected void stopServer()
    {
        if (server != null)
        {
            server.stopServer();
            try
            {
                server.join();
            }
            catch (InterruptedException e)
            {
                logger.error("Interrupted while waiting thrift server to stop", e);
            }
            server = null;
        }
    }

    public static void main(String[] args)
    {
        new ShortSocketTimeoutCassandraDaemon().activate();
    }

    /**
     * Simple class to run the thrift connection accepting code in separate
     * thread of control.
     */
    private static class ThriftServer extends Thread
    {
        private TServer serverEngine;

        public ThriftServer(InetAddress listenAddr, int listenPort)
        {
            // now we start listening for clients
            final CassandraServer cassandraServer = new CassandraServer();
            Cassandra.Processor processor = new Cassandra.Processor(cassandraServer);

            // Transport
            TServerSocket tServerSocket = null;

            try
            {
                tServerSocket = new TCustomServerSocket(new InetSocketAddress(listenAddr, listenPort),
                        DatabaseDescriptor.getRpcKeepAlive(),
                        DatabaseDescriptor.getRpcSendBufferSize(),
                        DatabaseDescriptor.getRpcRecvBufferSize()) {

                    /**
                     * Override the base class to set a timeout - base class sets 0 which means infinite!
                     */
                    @Override
                    public void listen() throws TTransportException {
                        ServerSocket serverSocket = getServerSocket();
                        if (serverSocket != null) {
                          try {
                            serverSocket.setSoTimeout(ACCEPT_SOCKET_TIMEOUT_MILLIS);
                          } catch (SocketException sx) {
                            logger.error("Could not set socket timeout.", sx);
                          }
                        }
                    }
                };
            }
            catch (TTransportException e)
            {
                throw new RuntimeException(String.format("Unable to create thrift socket to %s:%s",
                            listenAddr, listenPort), e);
            }

            logger.info(String.format("Binding thrift service to %s:%s", listenAddr, listenPort));

            // Protocol factory
            TProtocolFactory tProtocolFactory = new TBinaryProtocol.Factory(true,
                    true,
                    DatabaseDescriptor.getThriftMaxMessageLength());

            // Transport factory
            TTransportFactory inTransportFactory, outTransportFactory;
            int tFramedTransportSize = DatabaseDescriptor.getThriftFramedTransportSize();
            inTransportFactory  = new TFramedTransport.Factory(tFramedTransportSize);
            outTransportFactory = new TFramedTransport.Factory(tFramedTransportSize);
            logger.info("Using TFastFramedTransport with a max frame size of {} bytes.", tFramedTransportSize);

            // ThreadPool Server
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(tServerSocket)
                                          .minWorkerThreads(DatabaseDescriptor.getRpcMinThreads())
                                          .maxWorkerThreads(DatabaseDescriptor.getRpcMaxThreads())
                                          .inputTransportFactory(inTransportFactory)
                                          .outputTransportFactory(outTransportFactory)
                                          .inputProtocolFactory(tProtocolFactory)
                                          .outputProtocolFactory(tProtocolFactory)
                                          .processor(processor);

            ExecutorService executorService = new CleaningThreadPool(cassandraServer.clientState,
                    args.minWorkerThreads,
                    args.maxWorkerThreads);
            serverEngine = new CustomTThreadPoolServer(args, executorService);
        }

        @Override
        public void run()
        {
            logger.info("Listening for thrift clients...");
            serverEngine.serve();
        }

        public void stopServer()
        {
            logger.info("Stop listening to thrift clients");
            serverEngine.stop();
        }
    }
}
