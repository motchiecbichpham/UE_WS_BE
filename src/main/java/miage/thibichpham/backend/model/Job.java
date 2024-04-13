package miage.thibichpham.backend.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "job")
@Data
public class Job implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "company_id", referencedColumnName = "id")
  private Company company;

  private String title;
  @Lob
  @Column(length = 512)
  private String description;

  private Double salary;
  private String workplace;
  private int yearOfExp;
  private String contract;
  private Date expiredDate;
  private String status;
  private int amountHiring;
  private Date createdDate;

}
