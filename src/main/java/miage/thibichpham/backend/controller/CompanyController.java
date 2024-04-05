package miage.thibichpham.backend.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
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

import miage.thibichpham.backend.model.Application;
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.model.UserType;
import miage.thibichpham.backend.model.response.LoginResponse;
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

  // auth
  @PostMapping("/sign-up")
  public ResponseEntity<String> register(@RequestBody Company c) {
    Company comByContact = companyService.getCompanyByContact(c.getContact());
    if (comByContact != null) {
      return new ResponseEntity<>("Company with contact " + c.getContact() + " already exists",
          HttpStatus.CONFLICT);
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
  public ResponseEntity<Company> updateCompany(@PathVariable("id") long id, @RequestBody Company company) {
    Company comById = companyService.getCompany(id);
    if (comById == null) {
      return new ResponseEntity<>(company, HttpStatus.NOT_FOUND);

    }

    company.setId(id);
    company.setPassword(comById.getPassword());
    company.setContact(comById.getContact());
    companyService.updateCompany(company);
    return new ResponseEntity<>(company, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCompany(@PathVariable("id") long id) {
    Company existedCompany = companyService.getCompany(id);
    if (existedCompany == null) {
      return new ResponseEntity<>("Company with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }

    companyService.deleteCompanyById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Company> getCompany(@PathVariable("id") long id) {
    Company c = companyService.getCompany(id);
    if (c == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(c, HttpStatus.OK);
  }

  // JOB
  @PostMapping("/create-job")
  public ResponseEntity<String> createJob(@RequestBody Job j) {
    Company existedCompany = companyService.getCompany(j.getCompany().getId());
    if (existedCompany == null) {
      return new ResponseEntity<>("Company with ID " + j.getCompany().getId() + " not found", HttpStatus.NOT_FOUND);
    }
    companyService.createJob(j);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/job")
  public ResponseEntity<ArrayList<Job>> getJobsByCompany(@RequestParam("companyId") long companyId) {
    ArrayList<Job> jobs = companyService.getJobs(companyId);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/job/{id}")
  public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
    Job job = companyService.getJobById(id);
    if (job == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(job, HttpStatus.OK);
  }

  @PutMapping("/job/{id}")
  public ResponseEntity<String> updateJob(@PathVariable("id") long id, @RequestBody Job job) {
    Job existedJob = companyService.getJobById(id);
    if (existedJob == null) {
      return new ResponseEntity<>("Job with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }
    job.setId(id);
    companyService.updateJob(job);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/job/{id}")
  public ResponseEntity<String> deleteJob(@PathVariable("id") long id) {
    Job existedJob = companyService.getJobById(id);
    if (existedJob == null) {
      return new ResponseEntity<>("Job with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }

    companyService.deleteJob(existedJob);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // APPLICATION
  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByCompany(@RequestParam("companyId") long companyId) {
    ArrayList<Application> applications = companyService.getApplications(companyId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }

  // CANDIDATE
  @GetMapping("/candidate/{id}")
  public ResponseEntity<Candidate> getCandidateById(@PathVariable("id") long id) {
    Candidate existedCandidate = companyService.getCandidate(id);
    if (existedCandidate == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(existedCandidate, HttpStatus.OK);
  }

}
