package miage.thibichpham.backend.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import miage.thibichpham.backend.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  // @Query(value = "SELECT ad.* " +
  // "FROM activity_details ad " +
  // "INNER JOIN sales sa ON ad.sale_id = sa.id " +
  // "INNER JOIN leads le ON ad.lead_id = le.id AND le.staff_id = :staffId " +
  // "WHERE sa.id = :saleId AND le.id = :leadId " +
  // "ORDER BY ad.created_at", nativeQuery = true)
  // List<ActivityDetails>
  // findActivitiesBySaleIdAndLeadIdLinkWithStaffId(@Param("saleId") Long saleId,
  // @Param("leadId") Long leadId,
  // @Param("staffId") Integer staffId);

  ArrayList<Company> findAll();

  Company save(Company c);

  Company findById(long id);

  @Query(value = "Select * from Company where contact = :contact", nativeQuery = true)
  Company findByContact(@Param("contact") String contact);

  void deleteById(long id);
}
