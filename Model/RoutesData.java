package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutesData {

  @SerializedName("routes_data")
  @Expose
  private List<RoutesDatum> routesData = null;

  public List<RoutesDatum> getRoutesData() {
    return routesData;
  }

  public void setRoutesData(List<RoutesDatum> routesData) {
    this.routesData = routesData;
  }

}
