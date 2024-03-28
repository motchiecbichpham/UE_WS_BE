package miage.thibichpham.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import miage.thibichpham.backend.model.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
  Candidate save(Candidate c);

  Candidate findById(long id);

  @Query(value = "Select * from Candidate where email = :email", nativeQuery = true)
  Candidate findByEmail(@Param("email") String email);

  void deleteById(long id);
}
