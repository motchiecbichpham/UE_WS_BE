package miage.thibichpham.backend.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.model.UserType;
import miage.thibichpham.backend.model.response.LoginResponse;
import miage.thibichpham.backend.model.response.StringResponse;
import miage.thibichpham.backend.security.CustomUserService;
import miage.thibichpham.backend.security.JwtGenerator;
import miage.thibichpham.backend.service.company.ICompanyService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

  private final ICompanyService companyService;
  private JwtGenerator jwtGen = new JwtGenerator();

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private CustomUserService customUserService;

  @Autowired
  public CompanyController(ICompanyService companyService) {
    this.companyService = companyService;
  }

  // AUTH API

  @PostMapping("/sign-up")
  public ResponseEntity<StringResponse> register(@RequestBody Company c) {
    Company comByContact = companyService.getCompanyByContact(c.getContact());
    if (comByContact != null) {
      StringResponse response = new StringResponse("Company with contact " + c.getContact() + " already exists");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    companyService.register(c);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody Company company) {
    customUserService.setUserType(UserType.COMPANY);
    Company comByContact = companyService.getCompanyByContact(company.getContact());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(company.getContact(),
            company.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtGen.generateToken(authentication, UserType.COMPANY.toString());
    LoginResponse lr = new LoginResponse(token, comByContact);

    return ResponseEntity.ok().body(lr);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Company> updateCompany(@PathVariable("id") long id, @RequestBody Company company,
      Authentication authentication) {
    Company existedCompany = companyService.getCompany(id);
    if (existedCompany == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!validTokenWithData(authentication, existedCompany.getContact())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    company.setId(id);
    company.setPassword(existedCompany.getPassword());
    company.setContact(existedCompany.getContact());
    companyService.updateCompany(company);
    return ResponseEntity.status(HttpStatus.OK).body(company);
  }

  // ------------------------------------------------------------------------------------------------------------------

  // JOB API

  @PostMapping("/create-job")
  public ResponseEntity<StringResponse> createJob(@RequestBody Job job, Authentication authentication) {
    Company existedCompany = companyService.getCompanyByContact(job.getCompany().getContact());
    StringResponse response = new StringResponse("");
    if (existedCompany == null) {
      response.setMessage("Company with contact " + job.getCompany().getId() + " not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!validTokenWithData(authentication, existedCompany.getContact())) {
      response.setMessage("Forbidden");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    job.setCreatedDate(new Date());
    companyService.createJob(job);
    response.setMessage("Create job successfully");
    return ResponseEntity.status(HttpStatus.CREATED).build();

  }

  @GetMapping("/job")
  public ResponseEntity<ArrayList<Job>> getJobsByCompany(@RequestParam("companyId") long companyId,
      Authentication authentication) {
    Company existedCompany = companyService.getCompany(companyId);
    if (existedCompany == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
    if (!validTokenWithData(authentication, existedCompany.getContact())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    ArrayList<Job> jobs = companyService.getJobs(companyId);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return ResponseEntity.status(HttpStatus.OK).body(jobs);
  }

  @GetMapping("/job/{id}")
  public ResponseEntity<?> getJobById(@PathVariable("id") long id, Authentication authentication) {
    Job existedJob = companyService.getJobById(id);
    StringResponse response = new StringResponse("");
    if (existedJob == null) {
      response.setMessage("Job with ID " + id + " not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!validTokenWithData(authentication, existedJob.getCompany().getContact())) {
      response.setMessage("Forbidden");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    return ResponseEntity.status(HttpStatus.OK).body(existedJob);
  }

  @PutMapping("/job/{id}")
  public ResponseEntity<StringResponse> updateJob(@PathVariable("id") long id, @RequestBody Job job,
      Authentication authentication) {
    StringResponse response = new StringResponse("");
    Job existedJob = companyService.getJobById(id);
    if (existedJob == null) {
      response.setMessage("Job with ID " + id + " not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    if (!validTokenWithData(authentication, job.getCompany().getContact())) {
      response.setMessage("Forbidden");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    job.setId(id);
    job.setCreatedDate(existedJob.getCreatedDate());
    companyService.updateJob(job);
    response.setMessage("Update successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ------------------------------------------------------------------------------------------------------------------
  // APPLICATION API

  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByJob(@RequestParam("jobId") long jobId,
      Authentication authentication) {
    Job existedJob = companyService.getJobById(jobId);
    StringResponse response = new StringResponse("");
    if (existedJob == null) {
      response.setMessage("Job not found");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (!validTokenWithData(authentication, existedJob.getCompany().getContact())) {
      response.setMessage("Forbidden");
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    ArrayList<Application> applications = companyService.getApplicationsByJob(jobId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  @GetMapping("/file-application/{id}")
  public ResponseEntity<?> getFile(@PathVariable long id, Authentication authentication) {
    Application existedApplication = companyService.getApplicationById(id);
    StringResponse response = new StringResponse("");
    if (existedApplication == null) {
      response.setMessage("Application with ID " + id + " not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }
    if (!validTokenWithData(authentication, existedApplication.getJob().getCompany().getContact())) {
      response.setMessage("Forbidden");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    companyService.sendEmail(existedApplication);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + existedApplication.getResumeName() + "\"")
        .body(existedApplication.getResume());
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
