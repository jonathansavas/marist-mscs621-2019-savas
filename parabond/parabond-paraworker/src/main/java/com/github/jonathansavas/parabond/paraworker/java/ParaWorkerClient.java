package com.github.jonathansavas.parabond.paraworker.java;

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc.ParaWorkerBlockingStub;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerGrpc.ParaWorkerStub;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.GrpcResult;
import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.GrpcPartition;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class ParaWorkerClient {
  private static final Logger logger = LogManager.getLogger(ParaWorkerClient.class);
  private static final int DEFAULT_PORT = 9999;

  private final ManagedChannel channel;
  private final ParaWorkerBlockingStub blockingStub;
  private final ParaWorkerStub asyncStub;

  public ParaWorkerClient() {
    this(ParaWorkerUtil.getStringPropOrElse("paraworker.host", "localhost"),
        ParaWorkerUtil.getIntPropOrElse("paraworker.port", DEFAULT_PORT));
  }

  public ParaWorkerClient(String host) {
    this(host, DEFAULT_PORT);
  }

  /**
   * Construct client to ParaWorkerServer at host:port
   * @param host Ip address of the host
   * @param port Port number the server is listening on
   */
  public ParaWorkerClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  /**
   * Construct client to ParaWorkerServer using existing channel.
   * @param channel
   */
  public ParaWorkerClient(ManagedChannel channel) {
    this.channel = channel;
    this.blockingStub = ParaWorkerGrpc.newBlockingStub(channel);
    this.asyncStub = ParaWorkerGrpc.newStub(channel);
  }

  /**
   * Shutdown the client connection
   * @throws InterruptedException
   */
  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Remote procedure call to worker server. This is a blocking unary call.
   * @param n Number of portfolios in the partition
   * @param begin Begin index of the portfolio list for this partition
   * @return
   */
  public GrpcResult priceBonds(int n, int begin) {
    GrpcPartition partition = GrpcPartition.newBuilder().setN(n).setBegin(begin).build();
    GrpcResult result;
    try {
      result = blockingStub.work(partition);
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {}", e.getStatus());
      return null;
    }
    return result;
  }
}
