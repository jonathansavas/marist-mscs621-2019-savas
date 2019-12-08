package com.github.jonathansavas.parabond.paradispatcher.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcInstrumentId;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcBond;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcPortf;
import com.github.jonathansavas.parabond.paradispatcher.scala.ParaDispatcher;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobInfo;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobSize;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parabond.casa.MongoHelper;
import parabond.db.DbLoader;

/**
 * Server to handle requests to the dispatcher from the controller.
 */
public class ParaDispatcherServer {
  private static final Logger logger = LogManager.getLogger(ParaDispatcherServer.class.getName());
  private static final int DEFAULT_PORT = 9898;
  private final String PROPERTIES_FILE = "paradispatcher.properties";

  private final int port;
  private final Server server;

  private Properties props;

  /**
   * Construct server on default port.
   */
  public ParaDispatcherServer() {
    this.port = DEFAULT_PORT;
    this.server = ServerBuilder.forPort(port).addService(new ParaDispatcherService()).build();
  }

  /*
  public ParaDispatcherServer() {
    loadConfig(PROPERTIES_FILE);
    this.port = ParaDispatcherUtil.getIntPropOrElse("paradispatcher.port", DEFAULT_PORT);
    this.server = ServerBuilder.forPort(port).addService(new ParaDispatcherService()).build();
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
        ParaDispatcherServer.this.stop();
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
    if (! MongoHelper.isLoaded()) {
      logger.info("database not loaded, loading bonds and portfolios collections");
      DbLoader.loadBonds();
      DbLoader.loadPortfolios();
    }

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
    new ParaDispatcherServer().go();
  }

  /**
   * Implements gRPC methods available on this server.
   */
  private static class ParaDispatcherService extends ParaDispatcherGrpc.ParaDispatcherImplBase {

    /**
     * Sends job to paradispatcher.
     * @param jobSize Number of portfolios to analyze
     * @param responseObserver Listens for response to send to the client
     */
    @Override
    public void dispatch(GrpcJobSize jobSize, StreamObserver<GrpcJobInfo> responseObserver) {
      responseObserver.onNext(new ParaDispatcher().dispatch(jobSize));
      responseObserver.onCompleted();
    }

    @Override
    public void queryBond(GrpcInstrumentId bondId, StreamObserver<GrpcBond> responseObserver) {
      responseObserver.onNext(new ParaDispatcher().queryBond(bondId));
      responseObserver.onCompleted();
    }

    @Override
    public void queryPortfolio(GrpcInstrumentId portfId, StreamObserver<GrpcPortf> responseObserver) {
      responseObserver.onNext(new ParaDispatcher().queryPortfolio(portfId));
      responseObserver.onCompleted();
    }
  }
}
