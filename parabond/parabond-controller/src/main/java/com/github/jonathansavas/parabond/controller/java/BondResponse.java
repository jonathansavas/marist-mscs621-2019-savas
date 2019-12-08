package com.github.jonathansavas.parabond.controller.java;

import com.github.jonathansavas.parabond.ParaDispatcher.ParaDispatcherProto;

public class BondResponse {
  private int id;
  private double coupon;
  private int freq;
  private double tenor;
  private double maturity;
  private double value;

  public BondResponse(ParaDispatcherProto.GrpcBond bond) {
    this.id = bond.getId();
    this.coupon = bond.getCoupon();
    this.freq = bond.getFreq();
    this.tenor = bond.getTenor();
    this.maturity = bond.getMaturity();
    this.value = bond.getValue();
  }

  public int getId() {
    return id;
  }

  public double getCoupon() {
    return coupon;
  }

  public int getFreq() {
    return freq;
  }

  public double getTenor() {
    return tenor;
  }

  public double getMaturity() {
    return maturity;
  }

  public double getValue() {
    return value;
  }
}
