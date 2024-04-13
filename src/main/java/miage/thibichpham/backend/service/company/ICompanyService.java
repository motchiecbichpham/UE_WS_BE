package miage.thibichpham.backend.service.company;

import java.util.ArrayList;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;

public interface ICompanyService {

  // COMPANY
  void register(Company company);

  Company getCompany(long id);

  Company getCompanyByContact(String contact);

  void updateCompany(Company c);

  // JOB
  void createJob(Job j);

  void updateJob(Job j);

  ArrayList<Job> getJobs(Long id);

  Job getJobById(long id);

  // APPLICATIONS
  ArrayList<Application> getApplicationsByJob(long jobId);

  Application getApplicationById(long applicationId);

  void sendEmail(Application application);

}
