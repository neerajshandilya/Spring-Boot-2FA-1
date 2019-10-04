package lol.gilliard.springboot2fa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createNewUser(UserDto userDto) {
        User newUser = new User(userDto.getUsername(), userDto.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("USER")));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(userDto);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userRepository.findByUsername(username);

        if (userDto == null) return null;

        return new UserPrincipal(userDto);
    }
}
