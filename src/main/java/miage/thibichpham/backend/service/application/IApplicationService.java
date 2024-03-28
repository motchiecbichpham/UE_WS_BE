package miage.thibichpham.backend.service.application;

import java.util.ArrayList;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Job;

public interface IApplicationService {

  void createApplication(Application a);

  ArrayList<Application> getAllByCandidate(long id);

  ArrayList<Application> getAllByCompany(long id);

  Boolean isCandidateApplied(Candidate c, Job j);

}
