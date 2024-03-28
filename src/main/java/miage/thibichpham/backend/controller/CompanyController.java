package miage.thibichpham.backend.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.service.application.IApplicationService;
import miage.thibichpham.backend.service.company.ICompanyService;
import miage.thibichpham.backend.service.job.IJobService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

  private final ICompanyService companyService;
  private final IJobService jobService;
  private final IApplicationService appService;

  @Autowired
  public CompanyController(ICompanyService companyService, IJobService jobService, IApplicationService appService) {
    this.companyService = companyService;
    this.jobService = jobService;
    this.appService = appService;
  }

  @GetMapping
  public ResponseEntity<ArrayList<Company>> getAllCompanies() {
    ArrayList<Company> companies = companyService.getCompany();
    if (companies.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(companies, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
    Company c = companyService.getCompanyById(id);
    if (c == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(c, HttpStatus.OK);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<String> register(@RequestBody Company c) {
    Boolean isCompanyExisted = companyService.isCompanyExisted(c.getContact());
    if (isCompanyExisted) {
      return new ResponseEntity<>("Company with contact " + c.getContact() + " already exists",
          HttpStatus.CONFLICT);
    }
    companyService.register(c);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody Company company) {
    // companyService.login(company.getContact(), company.getPassword());
    // String token = companyService.generateToken(company.getContact());
    // System.out.println(token);
    return new ResponseEntity<>("", HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateCompany(@PathVariable("id") long id, @RequestBody Company company) {
    Company existedCompany = companyService.getCompanyById(id);
    if (existedCompany == null) {
      return new ResponseEntity<>("Company with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }
    company.setId(id);
    companyService.updateCompany(company);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCompany(@PathVariable("id") long id) {
    Company existedCompany = companyService.getCompanyById(id);
    if (existedCompany == null) {
      return new ResponseEntity<>("Company with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }

    companyService.deleteCompanyById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/create-job")
  public ResponseEntity<String> createJob(@RequestBody Job j) {
    Company existedCompany = companyService.getCompanyById(j.getCompany().getId());
    if (existedCompany == null) {
      return new ResponseEntity<>("Company with ID " + j.getCompany().getId() + " not found", HttpStatus.NOT_FOUND);
    }
    jobService.createJob(j);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/job")
  public ResponseEntity<ArrayList<Job>> getJobByCompany(@RequestParam("companyId") long companyId) {
    ArrayList<Job> jobs = jobService.getJobsByCompany(companyId);
    if (jobs.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(jobs, HttpStatus.OK);
  }

  @GetMapping("/job/{id}")
  public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
    Job job = jobService.getJobById(id);
    if (job == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(job, HttpStatus.OK);
  }

  @PutMapping("/job/{id}")
  public ResponseEntity<String> updateJob(@PathVariable("id") long id, @RequestBody Job job) {
    Job existedJob = jobService.getJobById(id);
    if (existedJob == null) {
      return new ResponseEntity<>("Job with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }
    job.setId(id);
    jobService.updateJob(job);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/job/{id}")
  public ResponseEntity<String> deleteJob(@PathVariable("id") long id) {
    Job existedJob = jobService.getJobById(id);
    if (existedJob == null) {
      return new ResponseEntity<>("Job with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }

    jobService.deleteJobById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByCompany(@RequestParam("companyId") long companyId) {
    ArrayList<Application> applications = appService.getAllByCompany(companyId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    return new ResponseEntity<>(applications, HttpStatus.OK);

  }
}
