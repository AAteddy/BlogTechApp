
package com.tedsaasfaha.blogapplication.config;


import com.tedsaasfaha.blogapplication.service.CustomUserPrinciple;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements org.springframework.data.domain.AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserPrinciple) {
            return Optional.of(((CustomUserPrinciple) principal).getUser().getId());
        }
        return Optional.empty();
    }
}
//