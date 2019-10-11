package lol.gilliard.springboot2fa;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class TwoFAAuthenticationDetails extends WebAuthenticationDetails {

    private String twoFAVerificationCode;

    public TwoFAAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.twoFAVerificationCode = request.getParameter("twofacode");
    }

    public String getTwoFAVerificationCode() {
        return twoFAVerificationCode;
    }
}
