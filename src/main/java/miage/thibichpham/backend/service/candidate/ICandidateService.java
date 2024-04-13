package miage.thibichpham.backend.service.candidate;

import java.util.ArrayList;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;

public interface ICandidateService {

  // CANDIDATE

  void register(Candidate c);

  Candidate getCandidateByEmail(String email);

  Candidate getCandidateById(long id);

  void updateCandidate(Candidate c);

  // JOB

  ArrayList<Job> getJobs();

  Job getJobById(long id);

  // APPLICATION

  void createApplication(Application a);

  void deleteApplication(long applicationId);

  ArrayList<Application> getApplicationsByCandidate(long candidateId);

  Boolean isCandidateApplied(Candidate c, Job j);

  Application getApplicationById(long applicationId);

  void sendEmail(Application application);

}
