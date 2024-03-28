package miage.thibichpham.backend.service.company;

import java.util.ArrayList;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.repository.CompanyRepository;
// import miage.thibichpham.backend.security.JwtUtil;

@Service
public class CompanyService implements ICompanyService {

  @Autowired
  private CompanyRepository companyRepo;

  // @Autowired
  // private JwtUtil jwtUtil;

  // @Autowired
  // private AuthenticationManager authenticationManager;

  // @Override
  // public String generateToken(String contact) {
  // Company company = companyRepo.findByContact(contact);
  // if (company == null) {
  // throw new RuntimeException("Company not found with contact: " + contact);
  // }
  // return jwtUtil.generateToken(contact);
  // }

  // @Override
  // public String login(String contact, String password) {
  // Company company = companyRepo.findByContact(contact);
  // String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
  // if (company == null || hashedPassword != company.getPassword()) {
  // throw new BadCredentialsException("Invalid email/password");
  // }
  // Authentication authentication = authenticationManager.authenticate(
  // new UsernamePasswordAuthenticationToken(contact, password));
  // SecurityContextHolder.getContext().setAuthentication(authentication);
  // return "authentication";
  // }

  @Override
  public void register(Company c) {
    String hashedPassword = BCrypt.hashpw(c.getPassword(), BCrypt.gensalt());
    c.setPassword(hashedPassword);
    companyRepo.save(c);
  }

  @Override
  public ArrayList<Company> getCompany() {
    return companyRepo.findAll();
  }

  @Override
  public Company getCompanyById(long id) {
    return companyRepo.findById(id);
  }

  @Override
  public Boolean isCompanyExisted(String contact) {
    Company c = companyRepo.findByContact(contact);
    if (c == null) {
      return false;
    }
    return true;
  }

  @Override
  public void updateCompany(Company c) {
    Company existedCompany = companyRepo.findById(c.getId());
    if (existedCompany != null) {
      companyRepo.save(c);
      return;
    }
  }

  @Override
  public void deleteCompanyById(long id) {
    companyRepo.deleteById(id);
  }

}
