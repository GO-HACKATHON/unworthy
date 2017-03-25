package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relation {

  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @SerializedName("deleted_at")
  @Expose
  private Object deletedAt;
  @SerializedName("distance")
  @Expose
  private Double distance;
  @SerializedName("events")
  @Expose
  private List<Event> events = null;
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("location_x")
  @Expose
  private String locationX;
  @SerializedName("location_y")
  @Expose
  private String locationY;
  @SerializedName("source")
  @Expose
  private String source;
  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("updated_at")
  @Expose
  private Object updatedAt;

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public Object getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(Object deletedAt) {
    this.deletedAt = deletedAt;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLocationX() {
    return locationX;
  }

  public void setLocationX(String locationX) {
    this.locationX = locationX;
  }

  public String getLocationY() {
    return locationY;
  }

  public void setLocationY(String locationY) {
    this.locationY = locationY;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Object getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Object updatedAt) {
    this.updatedAt = updatedAt;
  }

}
