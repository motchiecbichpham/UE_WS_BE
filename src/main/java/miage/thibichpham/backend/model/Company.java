package miage.thibichpham.backend.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "company")
@Data
public class Company implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private String contact;
  private String address;
  private String city;
  
  @Lob
  @Column(length = 256)
  private String introduction;
  @Lob
  @Column(length = 512)
  private String description;
  private String password;

}
