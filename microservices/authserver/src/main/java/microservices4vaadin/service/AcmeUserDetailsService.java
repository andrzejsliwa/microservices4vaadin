package microservices4vaadin.service;

import lombok.extern.slf4j.Slf4j;
import microservices4vaadin.auth.AcmeUser;
import microservices4vaadin.auth.AcmeUserDetails;
import microservices4vaadin.exception.UserNotActivatedException;
import microservices4vaadin.exception.UserNotFoundException;
import microservices4vaadin.repository.UserRepository;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads user specific AcmeUser data
 */
@Component("userDetailsService")
@Slf4j
public class AcmeUserDetailsService implements UserDetailsService {

    private static final String SESSION_USER_ATTRIBUTE_NAME="user";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        log.debug("Authenticating {}", email);
        String lowercaseEmail = email.toLowerCase();

        AcmeUser acmeUser = userRepository.findOneByEmail(lowercaseEmail);
        if (acmeUser == null) {
            throw new UserNotFoundException("User " + lowercaseEmail + " was not found in the database");
        } else if (!acmeUser.isActivated()) {
            throw new UserNotActivatedException("User " + lowercaseEmail + " was not activated");
        }


        AcmeUserDetails acmeUserDetails = new AcmeUserDetails(acmeUser);
        httpSession.setAttribute(SESSION_USER_ATTRIBUTE_NAME, acmeUserDetails);
        return acmeUserDetails;
    }

}
