package miage.thibichpham.backend.service.job;

import java.util.ArrayList;

import miage.thibichpham.backend.model.Job;

public interface IJobService {
  void createJob(Job j);

  void updateJob(Job j);

  void deleteJobById(long id);

  ArrayList<Job> getJobs();

  ArrayList<Job> getJobsByCompany(long companyId);

  Job getJobById(long id);

}
