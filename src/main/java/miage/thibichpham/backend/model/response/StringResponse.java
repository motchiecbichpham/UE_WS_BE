package miage.thibichpham.backend.model.response;

public class StringResponse {
  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public StringResponse(String message) {
    this.message = message;
  }

}
