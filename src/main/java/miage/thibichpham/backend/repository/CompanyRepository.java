package miage.thibichpham.backend.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import miage.thibichpham.backend.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  ArrayList<Company> findAll();

  Company save(Company c);

  Company findById(long id);

  @Query(value = "Select * from Company where contact = :contact", nativeQuery = true)
  Company findByContact(@Param("contact") String contact);

  void deleteById(long id);
}
