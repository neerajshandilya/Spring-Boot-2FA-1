package lol.gilliard.springboot2fa;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TwoFAAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public TwoFAAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        TwoFAAuthenticationDetails authDetails = (TwoFAAuthenticationDetails) (auth.getDetails());

        String serverCalculated2FACode;

        try {
            String user2FASharedSecret = userRepository.findByUsername(auth.getName()).getSuperSecretSecret();
            serverCalculated2FACode = OTP.create(user2FASharedSecret, OTP.timeInHex(), 6, Type.TOTP);
        } catch (Exception e) {
            // OTP.create and .timeInHex throw a few exceptions that shouldn't happen in normal use
            //   We can't verify their 2FA code. Bail here and the user won't log in.
            throw new BadCredentialsException("Unable to verify 2FA code", e);
        }

        String userSubmitted2FACode = authDetails.getTwoFAVerificationCode();

        if (! userSubmitted2FACode.equals(serverCalculated2FACode)){
            throw new BadCredentialsException("User's 2FA code was not correct");
        }

        return super.authenticate(auth);
    }
}
