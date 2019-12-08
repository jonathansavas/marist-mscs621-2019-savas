package com.github.jonathansavas.parabond.paraworker.java;

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc;
import com.github.jonathansavas.parabond.paraworker.scala.ParaWorker;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.GrpcPartition;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.GrpcResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Server to handle requests to the worker from the dispatcher.
 */
public class ParaWorkerServer {
  private static final Logger logger = LogManager.getLogger(ParaWorkerServer.class);
  private static final int DEFAULT_PORT = 9999;
  private final String PROPERTIES_FILE = "paraworker.properties";

  private final int port;
  private final Server server;

  private Properties props;

  /**
   * Construct server on default port.
   */
  public ParaWorkerServer() {
    this.port = DEFAULT_PORT;
    this.server = ServerBuilder.forPort(port).addService(new ParaWorkerService()).build();
  }

  /*
  public ParaWorkerServer() {
    loadConfig(PROPERTIES_FILE);
    this.port = ParaWorkerUtil.getIntPropOrElse("paraworker.port", DEFAULT_PORT);
    this.server = ServerBuilder.forPort(port).addService(new ParaWorkerService()).build();
  }
   */

  /**
   * Starts the server.
   * @throws IOException
   */
  public void start() throws IOException {
    server.start();
    logger.info("Server started, listening on port " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        ParaWorkerServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  /**
   * Stops the server
   */
  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Keeps the server running after startup. The server handles each request in a
   * separate thread.
   * @throws InterruptedException
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Load properties file to configure the server.
   * @param propFile
   */
  protected void loadConfig(String propFile) {
    try {
      this.props = System.getProperties();

      props.load(new FileInputStream(propFile));

    } catch (IOException ex) {
      logger.error("Failed to load {}: {}", propFile, ex);
    }
  }

  /**
   * Convenience method to start server and keep alive.
   * @throws InterruptedException
   */
  protected void go() throws InterruptedException {
    try {
      start();
    } catch (IOException e) {
      logger.warn("ParaWorkerServer startup failed: {}", e.getMessage());
    }

    blockUntilShutdown();
  }

  /**
   * Main method to run the server.
   * @param args
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    new ParaWorkerServer().go();
  }

  /**
   * Implements gRPC methods available on this server.
   */
  private static class ParaWorkerService extends ParaWorkerGrpc.ParaWorkerImplBase {

    /**
     * Sends partition to paraworker
     * @param partition Partition of portfolios to analyze
     * @param responseObserver Listens for response to send to the client
     */
    @Override
    public void work(GrpcPartition partition, StreamObserver<GrpcResult> responseObserver) {
      logger.info("Received partition: {}", partition);
      responseObserver.onNext(new ParaWorker().work(partition));
      responseObserver.onCompleted();
      logger.info("Sent results for partition: {}", partition);
    }
  }
}
