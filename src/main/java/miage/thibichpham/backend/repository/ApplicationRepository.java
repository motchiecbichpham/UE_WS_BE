package miage.thibichpham.backend.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import miage.thibichpham.backend.model.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

  Application save(Application a);

  Application findById(long id);

  @Query(value = "Select * from Application where candidate_id = :id", nativeQuery = true)
  ArrayList<Application> findAllByCandidate(@Param("id") long id);

  @Query(value = "Select * from Application join Job on Application.job_id = job.id where job.company_id = :id", nativeQuery = true)
  ArrayList<Application> findAllByCompany(@Param("id") long id);

  @Query(value = "Select * from Application where candidate_id = :candidateId and job_id = :jobId", nativeQuery = true)
  Application findExistedApplication(@Param("candidateId") long candidateId, @Param("jobId") long jobId);

}
