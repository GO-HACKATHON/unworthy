package gohackathon.unworthy.rest_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @SerializedName("data_key")
  @Expose
  private String dataKey;
  @SerializedName("distance")
  @Expose
  private Double distance;
  @SerializedName("first_location")
  @Expose
  private FirstLocation firstLocation;
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("reported_at")
  @Expose
  private String reportedAt;
  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("type_raw")
  @Expose
  private String typeRaw;

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getDataKey() {
    return dataKey;
  }

  public void setDataKey(String dataKey) {
    this.dataKey = dataKey;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public FirstLocation getFirstLocation() {
    return firstLocation;
  }

  public void setFirstLocation(FirstLocation firstLocation) {
    this.firstLocation = firstLocation;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getReportedAt() {
    return reportedAt;
  }

  public void setReportedAt(String reportedAt) {
    this.reportedAt = reportedAt;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTypeRaw() {
    return typeRaw;
  }

  public void setTypeRaw(String typeRaw) {
    this.typeRaw = typeRaw;
  }

}
