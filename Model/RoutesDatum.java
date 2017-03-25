
package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutesDatum {

  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @SerializedName("deleted_at")
  @Expose
  private Object deletedAt;
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("location_x")
  @Expose
  private String locationX;
  @SerializedName("location_y")
  @Expose
  private String locationY;
  @SerializedName("relation")
  @Expose
  private List<Relation> relation = null;
  @SerializedName("source")
  @Expose
  private String source;
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

  public List<Relation> getRelation() {
    return relation;
  }

  public void setRelation(List<Relation> relation) {
    this.relation = relation;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Object getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Object updatedAt) {
    this.updatedAt = updatedAt;
  }

}
