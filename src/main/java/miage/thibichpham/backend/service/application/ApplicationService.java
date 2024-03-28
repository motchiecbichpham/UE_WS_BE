package miage.thibichpham.backend.service.application;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.repository.ApplicationRepository;

@Service
public class ApplicationService implements IApplicationService {
  @Autowired
  private ApplicationRepository appRepo;

  @Override
  public void createApplication(Application a) {
    appRepo.save(a);
  }

  @Override
  public ArrayList<Application> getAllByCandidate(long id) {
    return appRepo.findAllByCandidate(id);
  }

  @Override
  public ArrayList<Application> getAllByCompany(long id) {
    return new ArrayList<>();
  }

  @Override
  public Boolean isCandidateApplied(Candidate c, Job j) {
    Application existedApplication = appRepo.findExistedApplication(c.getId(), j.getId());
    if (existedApplication != null) {
      return true;
    }
    return false;
  }

}
