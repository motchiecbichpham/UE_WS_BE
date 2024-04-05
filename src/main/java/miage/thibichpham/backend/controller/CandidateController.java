package miage.thibichpham.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.model.UserType;
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

  // CANDIDATE
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

  @GetMapping("/{id}")
  public ResponseEntity<Candidate> getCompany(@PathVariable("id") long id) {
    Candidate c = candidateService.getCandidateById(id);
    if (c == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(c, HttpStatus.OK);
  }
  // JOB

  @GetMapping("/job")
  public ResponseEntity<List<Job>> getAllJobs() {
    List<Job> jobs = candidateService.getJobs();
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/job/{id}")
  public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
    Job job = candidateService.getJobById(id);
    if (job == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(job, HttpStatus.OK);
  }

  // APPLICATIONS

  @PostMapping("/apply-job")
  public ResponseEntity<String> submitApplication(@RequestParam("resume") MultipartFile resume,
      @RequestParam("jobId") long jobId, @RequestParam("candidateId") long canId) {
    if (!resume.isEmpty()) {
      try {
        Application application = new Application();
        Candidate can = new Candidate();
        Job job = new Job();
        can.setId(canId);
        job.setId(jobId);
        application.setCandidate(can);
        application.setJob(job);
        application.setStatus(1);
        application.setResume(resume.getBytes());

        candidateService.createApplication(application);
        return new ResponseEntity<>("OK", HttpStatus.OK);

      } catch (Exception e) {
        System.out.println(e);
        return new ResponseEntity<>("hahaha", HttpStatus.BAD_REQUEST);
      }

    }
    return new ResponseEntity<>("hahaha", HttpStatus.BAD_REQUEST);

  }

  @GetMapping("/files")
  public ResponseEntity<byte[]> getFile() {
    ArrayList<Application> a = candidateService.getApplications(1);
    byte[] resume = a.get(0).getResume();
    try {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "\"")
          .body(resume);
    } catch (Exception e) {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "\"")
          .body(null);
    }
  }

  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByCandidat(
      @RequestParam("candidateId") long candidateId) {
    ArrayList<Application> applications = candidateService.getApplications(candidateId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  // COMPANY
  @GetMapping("/company/{companyId}")
  public ResponseEntity<Company> getCompanyById(
      @PathVariable("companyId") long companyId) {
    Company company = candidateService.getCompanyById(companyId);
    if (company == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(company, HttpStatus.OK);
  }

  @GetMapping("/company")
  public ResponseEntity<ArrayList<Company>> getCompany() {
    ArrayList<Company> companies = candidateService.getCompany();
    if (companies.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(companies, HttpStatus.OK);
  }
}
