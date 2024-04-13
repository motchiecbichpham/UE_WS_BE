package miage.thibichpham.backend.service.company;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.repository.ApplicationRepository;
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
  private JavaMailSender emailSender;

  // COMPANY

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
  // ------------------------------------------------------------------------------------------------------------------

  // JOB

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
  public ArrayList<Job> getJobs(Long id) {
    return jobRepo.findJobsByCompanyId(id);
  }

  @Override
  public Job getJobById(long id) {
    return jobRepo.findById(id);
  }

  // ------------------------------------------------------------------------------------------------------------------

  // APPLICATIONS

  @Override
  public ArrayList<Application> getApplicationsByJob(long jobId) {
    return appRepo.findAllByJob(jobId);
  }

  @Override
  public Application getApplicationById(long applicationId) {
    return appRepo.findById(applicationId);
  }

  @Override
  public void sendEmail(Application application) {
    SimpleMailMessage message = new SimpleMailMessage();
    Candidate candidate = application.getCandidate();
    Company company = application.getJob().getCompany();
    String emailContent = String.format(
        "Dear %s,\n\n" +
            "I hope this email finds you well.\n\n"
            +
            "I am writing to inform you that your application for job position %s has been viewed by our recruitment team at %s.\n\n"
            +
            "At this stage, the recruiter would review your resume including your skills, experiences, and career goals further.\n\n"
            +
            "We will contact you in case your application got accepted to pass to the interview process.\n\n"
            +
            "If you have any questions or require further information, please feel free to contact me.\n\n"
            +
            "Best regards,\n" +
            "TalentNet Services Team\n",
        candidate.getFirstName(), application.getJob().getTitle(), company.getName());
    message.setTo(candidate.getEmail());
    message.setSubject("Your application got viewed");
    message.setText(emailContent);
    emailSender.send(message);
  }
}
