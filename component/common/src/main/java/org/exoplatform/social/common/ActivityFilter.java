package org.exoplatform.social.common;

public class ActivityFilter {
  
  private String activityId;
  
  private Long postedTime;
  
  private Long updatedTime;
  
  private boolean before;
  
  private boolean after;

  public Long getPostedTime() {
    return postedTime;
  }

  public void setPostedTime(Long postedTime) {
    this.postedTime = postedTime;
  }

  public Long getUpdatedTime() {
    return updatedTime;
  }

  public void setUpdatedTime(Long updatedTime) {
    this.updatedTime = updatedTime;
  }

  public ActivityFilter() {
    this.activityId = null;
    this.postedTime = null;
    this.updatedTime = null;
    this.before = false;
    this.after = false;
  }

  public boolean getBefore() {
    return before;
  }

  public void setBefore(boolean before) {
    this.before = before;
  }

  public boolean getAfter() {
    return after;
  }

  public void setAfter(boolean after) {
    this.after = after;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }
  
}
