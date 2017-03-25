package gohackathon.unworthy.rest_api.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Routes {

  @SerializedName("routes_data")
  @Expose
  public List<RoutesDatum> routesData = null;
  @SerializedName("status")
  @Expose
  public Integer status;

  public List<RoutesDatum> getRoutesData() {
    return routesData;
  }

  public void setRoutesData(List<RoutesDatum> routesData) {
    this.routesData = routesData;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

}