package miage.thibichpham.backend.service.company;

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
public class CompanyService implements ICompanyService {

  @Autowired
  private CompanyRepository companyRepo;

  @Autowired
  private JobRepository jobRepo;

  @Autowired
  private ApplicationRepository appRepo;

  @Autowired
  private CandidateRepository canRepo;

  // account
  @Override
  public void register(Company c) {
    String hashedPassword = new BCryptPasswordEncoder().encode(c.getPassword());
    c.setPassword(hashedPassword);
    companyRepo.save(c);
  }

  @Override
  public Company getCompany(long id) {
    return companyRepo.findById(id);
  }

  @Override
  public Company getCompanyByContact(String contact) {
    return companyRepo.findByContact(contact);

  }

  @Override
  public void updateCompany(Company c) {
    Company existedCompany = companyRepo.findById(c.getId());
    if (existedCompany != null) {
      companyRepo.save(c);
      return;
    }
  }

  @Override
  public void deleteCompanyById(long id) {
    companyRepo.deleteById(id);
  }

  // job
  @Override
  public void createJob(Job j) {
    Company company = companyRepo.findById(j.getCompany().getId());
    j.setCompany(company);
    jobRepo.save(j);
  }

  @Override
  public void updateJob(Job j) {
    Job existedJob = jobRepo.findById(j.getId());
    if (existedJob != null) {
      jobRepo.save(j);
      return;
    }
  }

  @Override
  public void deleteJob(Job j) {
    jobRepo.deleteById(j.getId());
  }

  @Override
  public ArrayList<Job> getJobs(Long id) {
    return jobRepo.findJobsByCompanyId(id);
  }

  @Override
  public Job getJobById(long id) {
    return jobRepo.findById(id);
  }

  @Override
  public ArrayList<Job> getJobsByCriteria(String title, Double salary, String place) {
    // return jobRepo.findByCriteria(title, salary, place);
    return new ArrayList<>();
  }

  // applications
  @Override
  public ArrayList<Application> getApplications(long id) {
    return appRepo.findAllByCompany(id);
  }

  // candidate
  @Override
  public Candidate getCandidate(long id) {
    return canRepo.findById(id);
  }

  @Override
  public void login(String email, String password) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'login'");
  }

}
