package miage.thibichpham.backend.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import miage.thibichpham.backend.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

  ArrayList<Job> findAll();

  @Query(value = "Select * from Job where company_id = :companyId", nativeQuery = true)
  ArrayList<Job> findJobsByCompanyId(@Param("companyId") Long companyId);

  Job save(Job j);

  Job findById(long id);

  void deleteById(long id);

  // @Query("SELECT * from Job")
  // ArrayList<Job> findByCriteria(@Param("title") String title, @Param("salary")
  // double salary,
  // @Param("place") String place);

}
