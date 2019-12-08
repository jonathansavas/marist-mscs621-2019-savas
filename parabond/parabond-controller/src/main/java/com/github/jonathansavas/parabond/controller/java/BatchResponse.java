package com.github.jonathansavas.parabond.controller.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to respond with information about the user request.
 */
public class BatchResponse {
  private int numPortfolios;
  private String t1;
  private String tN;
  private List<String> portfs;

  /**
   * Constructs a BatchResponse object
   * @param info GrpcJobInfo with timing and misses information.
   * @param numPortfolios Number of porfolios analyzed
   */
  public BatchResponse(ParaDispatcherProto.GrpcJobInfo info, int numPortfolios) {
    this.numPortfolios = numPortfolios;
    this.t1 = info.getT1() / 1000000000.0 + "s";
    this.tN = info.getTN() / 1000000000.0 + "s";

    List<String> p = new ArrayList<>();

    for (ParaDispatcherProto.GrpcPortf portf : info.getPortfsList()) {
      p.add(String.format("%s:%s", portf.getId(), portf.getValue()));
    }

    this.portfs = p;
  }

  public int getNumPortfolios() {
    return numPortfolios;
  }

  public String getT1() {
    return t1 != null ? t1 : "null";
  }

  public String gettN() {
    return tN != null ? tN : "null";
  }

  public List<String> getPortfs() { return portfs != null ? portfs : new ArrayList<>(); }
}
