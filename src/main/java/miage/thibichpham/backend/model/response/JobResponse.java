package miage.thibichpham.backend.model.response;

import miage.thibichpham.backend.model.Job;

public class JobResponse {
  private Boolean isApplied;
  private Job job;
  private String message;

  public JobResponse(String message) {
    this.message = message;
  }

  public JobResponse(Boolean isApplied, Job job) {
    this.isApplied = isApplied;
    this.job = job;
  }

  public Boolean getIsApplied() {
    return isApplied;
  }

  public void setIsApplied(Boolean isApplied) {
    this.isApplied = isApplied;
  }

  public Job getJob() {
    return job;
  }

  public void setJob(Job job) {
    this.job = job;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
