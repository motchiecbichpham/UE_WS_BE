package miage.thibichpham.backend.service.candidate;

import java.io.File;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
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

  @Override
  public void register(Candidate c) {
    String hashedPassword = BCrypt.hashpw(c.getPassword(), BCrypt.gensalt());
    c.setPassword(hashedPassword);
    candidateRepo.save(c);
  }

  @Override
  public void login(String email, String password) {

  }

  @Override
  public List<Job> getJobs() {
    return jobRepo.findAll();
  }

  @Override
  public Job getJobById(long id) {
    return jobRepo.findById(id);
  }

  @Override
  public List<Job> getJobsByFilter(Job job) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getJobsByFilter'");
  }

  @Override
  public void applyJob(File cv) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'applyJob'");
  }

  @Override
  public Company getCompany(long id) {
    return companyRepo.findById(id);
  }

  @Override
  public Boolean isCandidatExisted(Candidate c) {
    Candidate existedCandidat = candidateRepo.findByEmail(c.getEmail());
    if (existedCandidat == null) {
      return false;
    }
    return true;
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

}
