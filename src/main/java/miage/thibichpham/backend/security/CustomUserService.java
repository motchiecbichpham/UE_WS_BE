package miage.thibichpham.backend.security;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;
import miage.thibichpham.backend.model.UserType;
import miage.thibichpham.backend.repository.CandidateRepository;
import miage.thibichpham.backend.repository.CompanyRepository;

@Service
public class CustomUserService implements UserDetailsService {

  @Autowired
  private CompanyRepository comRepo;
  @Autowired
  private CandidateRepository canRepo;

  private UserType userType;

  public UserType getUserType() {
    return userType;
  }

  public void setUserType(UserType userType) {
    this.userType = userType;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (userType == UserType.COMPANY) {
      Company company = comRepo.findByContact(username);
      SimpleGrantedAuthority comAuthority = new SimpleGrantedAuthority(UserType.COMPANY.toString());
      Collection<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(comAuthority);
      return new User(company.getContact(), company.getPassword(), authorities);
    } else if (userType == UserType.CANDIDATE) {
      Candidate candidate = canRepo.findByEmail(username);
      SimpleGrantedAuthority canAuthor = new SimpleGrantedAuthority(UserType.CANDIDATE.toString());
      Collection<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(canAuthor);
      return new User(candidate.getEmail(), candidate.getPassword(), authorities);
    }
    return null;
  }

}
