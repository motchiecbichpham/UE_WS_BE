package miage.thibichpham.backend.service.company;

import java.util.ArrayList;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;

public interface ICompanyService {

  // company
  void register(Company c);

  Company getCompany(long id);

  Company getCompanyByContact(String contact);

  void updateCompany(Company c);

  void deleteCompanyById(long id);

  // job
  void createJob(Job j);

  void updateJob(Job j);

  void deleteJob(Job j);

  ArrayList<Job> getJobs(Long id);

  Job getJobById(long id);

  // applications
  ArrayList<Application> getApplications(long id);

  Application getApplicationById(long id);

}
