package miage.thibichpham.backend.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

  @Lob
  private byte[] resume;
  private int status;

}
