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
@Table(name = "Application")
@Data
public class Application implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "job_id", referencedColumnName = "id")
  private Job job;

  @ManyToOne
  @JoinColumn(name = "candidate_id", referencedColumnName = "id")
  private Candidate candidate;

  private String resumeName;

  private String resumeType;
  
  @Lob
  @Column(length = 100000)
  private byte[] resume;
  private Date createdDate;

}
