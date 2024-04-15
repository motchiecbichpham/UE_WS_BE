package miage.thibichpham.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import miage.thibichpham.backend.model.response.StringResponse;
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

  @PostMapping("/sign-up")
  public ResponseEntity<StringResponse> register(@RequestBody Candidate c) {
    Candidate candidate = candidateService.getCandidateByEmail(c.getEmail());
    if (candidate != null) {
      StringResponse response = new StringResponse("This email has been registered already");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    candidateService.register(c);
    StringResponse response = new StringResponse("Created account successfully");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

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

    return ResponseEntity.status(HttpStatus.OK).body(lr);
  }

  @PutMapping("/{candidateId}")
  public ResponseEntity<?> updateCandidate(@PathVariable("candidateId") long candidateId,
      @RequestBody Candidate candidate,
      Authentication authentication) {
    StringResponse response = new StringResponse("");
    Candidate existedCandidate = candidateService.getCandidateById(candidateId);
    if (existedCandidate == null) {
      response.setMessage("Candidate with id " + candidateId + "is existed.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!validTokenWithData(authentication, existedCandidate.getEmail())) {
      response.setMessage("Forbidden");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    candidate.setId(candidateId);
    candidate.setPassword(existedCandidate.getPassword());
    candidate.setEmail(existedCandidate.getEmail());
    candidateService.updateCandidate(candidate);
    return ResponseEntity.status(HttpStatus.OK).body(candidate);
  }

  // ------------------------------------------------------------------------------------------------------------------
  // JOB API

  @GetMapping("/jobs")
  public ResponseEntity<List<Job>> getAllJobs() {
    List<Job> jobs = candidateService.getJobs();
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return ResponseEntity.status(HttpStatus.OK).body(jobs);
  }

  @GetMapping("/job")
  public ResponseEntity<JobResponse> getJobById(@RequestParam("jobId") long jobId,
      @RequestParam("candidateId") long candidateId, Authentication authentication) {
    Candidate existedCandidate = candidateService.getCandidateById(candidateId);
    JobResponse response = new JobResponse("");
    if (existedCandidate == null) {
      response.setMessage("Can not find the Candidate with ID " + jobId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!validTokenWithData(authentication, existedCandidate.getEmail())) {
      response.setMessage("Forbidden");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    Job existedJob = candidateService.getJobById(jobId);
    if (existedJob == null) {
      response.setMessage("Can not find the job with ID " + jobId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    Boolean isApplied = candidateService.isCandidateApplied(existedCandidate, existedJob);
    JobResponse jr = new JobResponse(isApplied, existedJob);
    return ResponseEntity.status(HttpStatus.OK).body(jr);
  }

  // ------------------------------------------------------------------------------------------------------------------
  // APPLICATIONS API

  @PostMapping("/apply-job")
  public ResponseEntity<StringResponse> submitApplication(@RequestParam("resume") MultipartFile resume,
      @RequestParam("jobId") long jobId, @RequestParam("candidateId") long candidateId, Authentication authentication) {
    Candidate existedCandidate = candidateService.getCandidateById(candidateId);
    Job existedJob = candidateService.getJobById(jobId);
    StringResponse response = new StringResponse("");
    if (!resume.isEmpty() && existedCandidate != null && existedJob != null) {
      if (!validTokenWithData(authentication, existedCandidate.getEmail())) {
        response.setMessage("Forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }
      Boolean isApplied = candidateService.isCandidateApplied(existedCandidate, existedJob);
      if (isApplied) {
        response.setMessage("This candidate applied this job already");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }
      try {
        Application application = new Application();
        application.setCandidate(existedCandidate);
        application.setJob(existedJob);

        String fileName = StringUtils.cleanPath(resume.getOriginalFilename());
        application.setResumeName(fileName);
        application.setResumeType(resume.getContentType());
        application.setResume(resume.getBytes());
        application.setCreatedDate(new Date());
        candidateService.createApplication(application);
        candidateService.sendEmail(application);
        response.setMessage("Applied successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
      } catch (Exception e) {
        response.setMessage("Errors happens when submitting");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
    }
    response.setMessage("Errors with file happens");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

  }

  @DeleteMapping("/application")
  public ResponseEntity<StringResponse> deleteApplicationById(@RequestParam("applicationId") long applicationId,
      Authentication authentication) {

    StringResponse response = new StringResponse("");
    Application existedApplication = candidateService.getApplicationById(applicationId);
    if (existedApplication == null) {
      response.setMessage("Application with ID " + applicationId + " not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!validTokenWithData(authentication, existedApplication.getCandidate().getEmail())) {
      response.setMessage(("Forbidden"));
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    candidateService.deleteApplication(applicationId);
    response.setMessage("Delete successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByCandidate(
      @RequestParam("candidateId") long candidateId, Authentication authentication) {
    Candidate existedCandidate = candidateService.getCandidateById(candidateId);
    if (existedCandidate == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!validTokenWithData(authentication, existedCandidate.getEmail())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    ArrayList<Application> applications = candidateService.getApplicationsByCandidate(candidateId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return ResponseEntity.status(HttpStatus.OK).body(applications);
  }

  // ------------------------------------------------------------------------------------------------------------------

  private boolean validTokenWithData(Authentication authentication, String username) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    if (!userDetails.getUsername().equals(username)) {
      return false;
    }
    return true;

  }
}
