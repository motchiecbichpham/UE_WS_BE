package miage.thibichpham.backend.service.candidate;

import java.io.File;
import java.util.List;

import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;

public interface ICandidateService {
  void register(Candidate c);

  void login(String email, String password);

  List<Job> getJobs();

  Job getJobById(long id);

  List<Job> getJobsByFilter(Job job);

  void applyJob(File cv);

  Company getCompany(long id);

  Boolean isCandidatExisted(Candidate c);

  Candidate getCandidateById(long id);

  void updateCandidate(Candidate c);

  void deleteCandidat(long id);

}
