package miage.thibichpham.backend.service.candidate;

import java.util.ArrayList;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;

public interface ICandidateService {
  // candidate
  void register(Candidate c);

  void login(String email, String password);

  Candidate getCandidateByEmail(String email);

  Candidate getCandidateById(long id);

  void updateCandidate(Candidate c);

  void deleteCandidat(long id);

  // job

  ArrayList<Job> getJobs();

  Job getJobById(long id);

  ArrayList<Job> getJobsByFilter(Job job);

  // company

  Company getCompanyById(long id);

  ArrayList<Company> getCompany();

  // application

  void createApplication(Application a);

  ArrayList<Application> getApplications(long id);

  Boolean isCandidateApplied(Candidate c, Job j);

  // Candidate findByUsername

}
