package com.github.jonathansavas.parabond.paradispatcher.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc.ParaDispatcherBlockingStub;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherGrpc.ParaDispatcherStub;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcBond;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcPortf;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcInstrumentId;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobInfo;
import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto.GrpcJobSize;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

public class ParaDispatcherClient {
  private static final Logger logger = LogManager.getLogger(ParaDispatcherClient.class);
  private static final String DEFAULT_PORT = "9898";
  private static final String DEFAULT_HOST = "localhost";

  private final ManagedChannel channel;
  private final ParaDispatcherBlockingStub blockingStub;
  private final ParaDispatcherStub asyncStub;

  public ParaDispatcherClient() {
    this(ParaDispatcherUtil.getStringPropOrElse("paradispatcher.host", DEFAULT_HOST),
        ParaDispatcherUtil.getStringPropOrElse("paradispatcher.port", DEFAULT_PORT));
  }

  public ParaDispatcherClient(String host) {
    this(host, DEFAULT_PORT);
  }

  /**
   * Construct client to ParaDispatcherServer at host:port
   * @param host Ip address of the host
   * @param port Port number the server is listening on
   */
  public ParaDispatcherClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  public ParaDispatcherClient(String host, String port) {
    this(ManagedChannelBuilder.forTarget(host + ":" + port).usePlaintext().build());
  }

  /**
   * Construct client to ParaDispatcherServer using existing channel.
   * @param channel
   */
  public ParaDispatcherClient(ManagedChannel channel) {
    this.channel = channel;
    this.blockingStub = ParaDispatcherGrpc.newBlockingStub(channel);
    this.asyncStub = ParaDispatcherGrpc.newStub(channel);
  }

  /**
   * Shutdown the client connection
   * @throws InterruptedException
   */
  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Remote procedure call to dispatcher server. This is a blocking unary call.
   * @param size Number of portfolios to price
   * @return
   */
  public GrpcJobInfo processJob(int size) {
    GrpcJobSize request = GrpcJobSize.newBuilder().setN(size).build();
    GrpcJobInfo info;
    try {
      info = blockingStub.dispatch(request);
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {}", e.getStatus());
      return null;
    }
    return info;
  }

  public GrpcBond queryBond(int id) {
    GrpcBond bond;
    try {
      bond = blockingStub.queryBond(GrpcInstrumentId.newBuilder().setId(id).build());
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {}", e.getStatus());
      return null;
    }
    return bond;
  }

  public GrpcPortf queryPortfolio(int id) {
    GrpcPortf portf;
    try {
      portf = blockingStub.queryPortfolio(GrpcInstrumentId.newBuilder().setId(id).build());
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {}", e.getStatus());
      return null;
    }
    return portf;
  }
}
