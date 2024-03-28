package miage.thibichpham.backend.service.job;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.repository.JobRepository;
import miage.thibichpham.backend.repository.CompanyRepository;

@Service
public class JobService implements IJobService {

  @Autowired
  private JobRepository jobRepo;
  @Autowired
  private CompanyRepository companyRepo;

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
  public void deleteJobById(long id) {
    jobRepo.deleteById(id);
  }

  @Override
  public ArrayList<Job> getJobs() {
    return jobRepo.findAll();
  }

  @Override
  public Job getJobById(long id) {
    return jobRepo.findById(id);
  }

  @Override
  public ArrayList<Job> getJobsByCompany(long companyId) {
    return jobRepo.findJobsByCompanyId(companyId);
  }

}
