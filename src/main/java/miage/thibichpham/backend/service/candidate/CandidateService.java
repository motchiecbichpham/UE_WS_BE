package miage.thibichpham.backend.service.candidate;

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
import miage.thibichpham.backend.repository.CandidateRepository;
import miage.thibichpham.backend.repository.JobRepository;

@Service
public class CandidateService implements ICandidateService {

  @Autowired
  private JobRepository jobRepo;

  @Autowired
  private CandidateRepository candidateRepo;

  @Autowired
  private ApplicationRepository appRepo;

  @Autowired
  private JavaMailSender emailSender;

  // CANDIDATE

  @Override
  public void register(Candidate candidate) {
    String hashedPassword = new BCryptPasswordEncoder().encode(candidate.getPassword());
    candidate.setPassword(hashedPassword);
    candidateRepo.save(candidate);
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
  // ------------------------------------------------------------------------------------------------------------------

  // JOB

  @Override
  public ArrayList<Job> getJobs() {
    return jobRepo.findJobOpen();
  }

  @Override
  public Job getJobById(long id) {
    return jobRepo.findById(id);
  }

  // ------------------------------------------------------------------------------------------------------------------

  // APPLICATIONS

  @Override
  public void createApplication(Application application) {
    appRepo.save(application);
  }

  @Override
  public void deleteApplication(long applicationId) {
    appRepo.deleteById(applicationId);
  }

  @Override
  public ArrayList<Application> getApplicationsByCandidate(long candidateId) {
    return appRepo.findAllByCandidate(candidateId);
  }

  @Override
  public Boolean isCandidateApplied(Candidate candidate, Job job) {
    Application existedApplication = appRepo.findExistedApplication(candidate.getId(), job.getId());
    if (existedApplication != null) {
      return true;
    }
    return false;
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
            "I'm writing to inform you that we've received an application for the %s position you posted on TalentNet.\n\n"
            +
            "Candidate: %s\n"
            +
            "Date of Application: %s\n"
            +
            "Please review the candidate's qualifications and keep us updated on the progress.\n"
            +
            "Thank you for your trust in us.\n\n"
            +
            "Best regards,\n"
            +
            "TalentNet Services Team",
        company.getName(), application.getJob().getTitle(), candidate.getFirstName(), application.getCreatedDate());
    String subject = String.format("Job %s - Applied by candidate %s", application.getJob().getTitle(),
        application.getCandidate().getFirstName());
    message.setTo(application.getJob().getCompany().getContact());
    message.setSubject(subject);
    message.setText(emailContent);
    emailSender.send(message);
  }

}
