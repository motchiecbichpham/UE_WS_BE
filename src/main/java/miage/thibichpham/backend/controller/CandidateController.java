package miage.thibichpham.backend.controller;

import java.util.ArrayList;
import java.util.List;

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
import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Job;
import miage.thibichpham.backend.service.application.IApplicationService;
import miage.thibichpham.backend.service.candidate.ICandidateService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateController {

  private final ICandidateService candidateService;
  private final IApplicationService appService;

  @Autowired
  public CandidateController(ICandidateService candidateService, IApplicationService appService) {
    this.candidateService = candidateService;
    this.appService = appService;
  }

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

  @PostMapping("/sign-up")
  public ResponseEntity<String> register(@RequestBody Candidate c) {
    Boolean isCandidatExisted = candidateService.isCandidatExisted(c);
    if (isCandidatExisted) {
      return new ResponseEntity<>("Candidat with email " + c.getEmail() + " already exists",
          HttpStatus.CONFLICT);
    }
    candidateService.register(c);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateCandidate(@PathVariable("id") long id, @RequestBody Candidate candidate) {
    Candidate existedCandidate = candidateService.getCandidateById(id);
    if (existedCandidate == null) {
      return new ResponseEntity<>("Candidate with ID " + id + " not found", HttpStatus.NOT_FOUND);
    }
    candidate.setId(id);
    candidateService.updateCandidate(candidate);
    return ResponseEntity.status(HttpStatus.OK).build();
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

  @PostMapping("/apply-job")
  public ResponseEntity<String> applyJobById(@RequestBody Application a) {
    // check job id and candidate id
    Boolean isApplicationExisted = appService.isCandidateApplied(a.getCandidate(), a.getJob());
    if (isApplicationExisted) {
      return new ResponseEntity<>("Candidate with ID " + a.getCandidate().getId() + " already applied for this job",
          HttpStatus.CONFLICT);
    }
    appService.createApplication(a);
    return ResponseEntity.status(HttpStatus.CREATED).build();

  }

  @GetMapping("/application")
  public ResponseEntity<ArrayList<Application>> getApplicationByCandidat(
      @RequestParam("candidateId") long candidateId) {
    ArrayList<Application> applications = appService.getAllByCandidate(candidateId);
    if (applications.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    return new ResponseEntity<>(applications, HttpStatus.OK);

  }
}
