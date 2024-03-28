package miage.thibichpham.backend.service.company;

import java.util.ArrayList;
import miage.thibichpham.backend.model.Company;

public interface ICompanyService {
  // String login(String contact, String password);

  void register(Company c);

  ArrayList<Company> getCompany();

  Company getCompanyById(long id);

  Boolean isCompanyExisted(String contact);

  void updateCompany(Company c);

  void deleteCompanyById(long id);

  // String generateToken(String contact);

}
