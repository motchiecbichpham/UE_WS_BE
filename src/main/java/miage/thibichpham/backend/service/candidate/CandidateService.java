package miage.thibichpham.backend.service.candidate;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.repository.ApplicationRepository;
import miage.thibichpham.backend.repository.CandidateRepository;
import miage.thibichpham.backend.repository.CompanyRepository;
import miage.thibichpham.backend.repository.JobRepository;

@Service
public class CandidateService implements ICandidateService {

  @Autowired
  private JobRepository jobRepo;
  @Autowired
  private CompanyRepository companyRepo;
  @Autowired
  private CandidateRepository candidateRepo;
  @Autowired
  private ApplicationRepository appRepo;

  // account
  @Override
  public void register(Candidate c) {
    String hashedPassword = new BCryptPasswordEncoder().encode(c.getPassword());
    c.setPassword(hashedPassword);
    candidateRepo.save(c);
  }


  @Override
  public Candidate getCandidateByEmail(String email) {
    Candidate existedCandidat = candidateRepo.findByEmail(email);
    return existedCandidat;
  }

  @Override
  public Candidate getCandidateById(long id) {
    return candidateRepo.findById(id);
  }

  @Override
  public void updateCandidate(Candidate c) {
    Candidate existedCandidate = candidateRepo.findById(c.getId());
    if (existedCandidate != null) {
      candidateRepo.save(c);
      return;
    }
  }

  @Override
  public void deleteCandidat(long id) {
    candidateRepo.deleteById(id);

  }

  @Override
  public ArrayList<Job> getJobs() {
    return jobRepo.findJobOpen();
  }

  @Override
  public Job getJobById(long id) {
    return jobRepo.findById(id);
  }



  // company
  @Override
  public Company getCompanyById(long id) {
    return companyRepo.findById(id);
  }

  @Override
  public ArrayList<Company> getCompany() {
    return companyRepo.findAll();
  }

  // application

  @Override
  public void createApplication(Application a) {
    appRepo.save(a);
  }

  @Override
  public void deleteApplication(long id) {
    appRepo.deleteById(id);
  }

  @Override
  public ArrayList<Application> getApplications(long id) {
    return appRepo.findAllByCandidate(id);
  }

  @Override
  public Boolean isCandidateApplied(Candidate c, Job j) {
    Application existedApplication = appRepo.findExistedApplication(c.getId(), j.getId());
    if (existedApplication != null) {
      return true;
    }
    return false;
  }

  @Override
  public Application getApplicationById(long id) {
    return appRepo.findById(id);
  }

}
