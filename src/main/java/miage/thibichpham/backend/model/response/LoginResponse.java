package miage.thibichpham.backend.model.response;

import miage.thibichpham.backend.model.Candidate;
import miage.thibichpham.backend.model.Company;

public class LoginResponse {

    private String token;
    private Company company;
    private Candidate candidate;

    public LoginResponse(String token, Company company) {
        this.token = token;
        this.company = company;
    }

    public LoginResponse(String token, Candidate candidate) {
        this.token = token;
        this.candidate = candidate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

}