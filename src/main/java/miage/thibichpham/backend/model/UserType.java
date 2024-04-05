package miage.thibichpham.backend.model;

public enum UserType {
  COMPANY("COMPANY"), CANDIDATE("CANDIDATE");

  private final String type;

  UserType(String string) {
    type = string;
  }

  @Override
  public String toString() {
    return type;
  }

}
