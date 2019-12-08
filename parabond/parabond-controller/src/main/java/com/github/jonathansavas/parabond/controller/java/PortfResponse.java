package com.github.jonathansavas.parabond.controller.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;

import java.util.ArrayList;
import java.util.List;

public class PortfResponse {
  private int id;
  private double value;
  private List<Integer> bondIds;

  public PortfResponse(ParaDispatcherProto.GrpcPortf p) {
    this.id = p.getId();
    this.value = p.getValue();
    this.bondIds = p.getBondIdsList();
  }

  public int getId() {
    return id;
  }

  public double getValue() {
    return value;
  }

  public List<Integer> getBondIds() {
    return bondIds != null ? bondIds : new ArrayList<>();
  }
}
