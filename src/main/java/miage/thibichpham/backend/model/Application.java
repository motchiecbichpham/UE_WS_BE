package miage.thibichpham.backend.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

  @OneToOne
  @JoinColumn(name = "job_id", referencedColumnName = "id")
  private Job job;

  @OneToOne
  @JoinColumn(name = "candidate_id", referencedColumnName = "id")
  private Candidate candidate;

  private int status;

}
