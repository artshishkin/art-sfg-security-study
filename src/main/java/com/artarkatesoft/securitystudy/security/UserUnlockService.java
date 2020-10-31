package com.artarkatesoft.securitystudy.security;

import com.artarkatesoft.securitystudy.domain.security.User;
import com.artarkatesoft.securitystudy.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUnlockService {

    private final UserRepository userRepository;

    @Scheduled(fixedRateString = "PT5S")
    public void unlockUsers() {
        log.debug("Unlocking users");
        List<User> lockedUsers = userRepository.findAllByAccountNonLockedIsFalseAndLastModifiedDateBefore(
                Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

        if (lockedUsers.size() > 0) {
            log.debug("Locked users found. Unlocking...");
            lockedUsers.forEach(user -> user.setAccountNonLocked(true));
            userRepository.saveAll(lockedUsers);
        }
    }
}
