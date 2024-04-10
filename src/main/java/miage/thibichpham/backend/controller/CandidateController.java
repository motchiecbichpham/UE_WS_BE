package miage.thibichpham.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.model.UserType;
import miage.thibichpham.backend.model.response.JobResponse;
import miage.thibichpham.backend.model.response.LoginResponse;
import miage.thibichpham.backend.security.CustomUserService;
import miage.thibichpham.backend.security.JwtGenerator;
import miage.thibichpham.backend.service.candidate.ICandidateService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateController {

  private final ICandidateService candidateService;
  private JwtGenerator jwtGen = new JwtGenerator();

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private CustomUserService customUserService;

  @Autowired
  public CandidateController(ICandidateService candidateService) {
    this.candidateService = candidateService;
  }

  // AUTH API

  // sign up new account
  @PostMapping("/sign-up")
  public ResponseEntity<String> register(@RequestBody Candidate c) {
    Candidate candidate = candidateService.getCandidateByEmail(c.getEmail());
    if (candidate != null) {
      return new ResponseEntity<>("Candidat with email " + c.getEmail() + " already exists",
          HttpStatus.CONFLICT);
    }
    candidateService.register(c);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // login with jwt
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody Candidate candidate) {
    customUserService.setUserType(UserType.CANDIDATE);
    Candidate candidateByEmail = candidateService.getCandidateByEmail(candidate.getEmail());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(candidate.getEmail(),
            candidate.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtGen.generateToken(authentication, UserType.CANDIDATE.toString());
    LoginResponse lr = new LoginResponse(token, candidateByEmail);

    return ResponseEntity.ok().body(lr);
  }

  // update account
  @PutMapping("/{id}")
  public ResponseEntity<Candidate> updateCandidate(@PathVariable("id") long id, @RequestBody Candidate candidate) {
    Candidate existedCandidate = candidateService.getCandidateById(id);
    if (existedCandidate == null) {
      return new ResponseEntity<>(candidate, HttpStatus.NOT_FOUND);
    }
    candidate.setId(id);
    candidate.setPassword(existedCandidate.getPassword());
    candidate.setEmail(existedCandidate.getEmail());
    candidateService.updateCandidate(candidate);
    return new ResponseEntity<>(candidate, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCandidate(@PathVariable("id") long id) {
    Candidate existedCandidate = candidateService.getCandidateById(id);
    if (existedCandidate == null) {
      return new ResponseEntity<>("Candidate with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }
    candidateService.deleteCandidat(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // JOB API

  // get all jobs
  @GetMapping("/jobs")
  public ResponseEntity<List<Job>> getAllJobs() {
    List<Job> jobs = candidateService.getJobs();
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  // get job by id
  @GetMapping("/job")
  public ResponseEntity<JobResponse> getJobById(@RequestParam("jobId") long jobId,
      @RequestParam("canId") long canId) {
    Candidate can = new Candidate();
    can.setId(canId);
    Job job = candidateService.getJobById(jobId);
    if (job == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Boolean isApplied = candidateService.isCandidateApplied(can, job);
    JobResponse jr = new JobResponse(isApplied, job);
    return new ResponseEntity<JobResponse>(jr, HttpStatus.OK);
  }

  // APPLICATIONS API

  // create new application, apply job
  @PostMapping("/apply-job")
  public ResponseEntity<String> submitApplication(@RequestParam("resume") MultipartFile resume,
      @RequestParam("jobId") long jobId, @RequestParam("candidateId") long canId) {
    Candidate c = candidateService.getCandidateById(canId);
    Job j = candidateService.getJobById(jobId);

    if (!resume.isEmpty() && c != null && j != null) {
      Boolean isApplied = candidateService.isCandidateApplied(c, j);
      if (isApplied) {
        return new ResponseEntity<>("This candidate applied this job already", HttpStatus.BAD_REQUEST);
      }
      try {
        Application application = new Application();
        Candidate can = new Candidate();
        Job job = new Job();
        can.setId(canId);
        job.setId(jobId);
        application.setCandidate(can);
        application.setJob(job);
        application.setStatus(1);

        String fileName = StringUtils.cleanPath(resume.getOriginalFilename());
        application.setResumeName(fileName);
        application.setResumeType(resume.getContentType());
        application.setResume(resume.getBytes());
        candidateService.createApplication(application);
        return new ResponseEntity<>(HttpStatus.OK);

      } catch (Exception e) {
        return new ResponseEntity<>("Applied failed", HttpStatus.BAD_REQUEST);
      }

    }
    return new ResponseEntity<>("Applied failed", HttpStatus.BAD_REQUEST);

  }

  // delete an application
  @DeleteMapping("/application")
  public ResponseEntity<String> deleteApplicationById(@RequestParam("applicationId") long applicationId,
      @RequestParam("candidateId") long candidateId) {
    Application existedApplication = candidateService.getApplicationById(applicationId);
    if (existedApplication == null) {
      return new ResponseEntity<>("Application with ID " + applicationId + " not found", HttpStatus.NOT_FOUND);
    } else if (existedApplication.getCandidate().getId() != candidateId) {
      return new ResponseEntity<>("Application with ID " + applicationId + " not found", HttpStatus.NOT_FOUND);
    }
    candidateService.deleteApplication(applicationId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // get all applications
  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByCandidat(
      @RequestParam("candidateId") long candidateId) {
    ArrayList<Application> applications = candidateService.getApplications(candidateId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

}
