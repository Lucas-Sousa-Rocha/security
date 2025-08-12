package com.lucas_sousa_rocha.security.specification;

import com.lucas_sousa_rocha.security.model.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserSpecifications {

    // Filtra username contendo texto (ignora case)
    public static Specification<User> usernameContains(String username) {
        return (root, query, builder) -> {
            if (username == null || username.isEmpty()) {
                return null; // sem filtro
            }
            return builder.like(builder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    // Filtra email contendo texto (ignora case)
    public static Specification<User> emailContains(String email) {
        return (root, query, builder) -> {
            if (email == null || email.isEmpty()) {
                return null;
            }
            return builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    // Filtra role exata (ex: ROLE_USER, ROLE_ADMIN)
    public static Specification<User> hasRole(String role) {
        return (root, query, builder) -> {
            if (role == null || role.isEmpty()) {
                return null;
            }
            return builder.equal(root.get("role"), role);
        };
    }

    // Filtra usuários com data de inclusão >= startDateTime
    public static Specification<User> dateAfter(LocalDateTime startDateTime) {
        return (root, query, builder) -> {
            if (startDateTime == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("inclusion_date"), startDateTime);
        };
    }

    // Filtra usuários com data de inclusão <= endDateTime
    public static Specification<User> dateBefore(LocalDateTime endDateTime) {
        return (root, query, builder) -> {
            if (endDateTime == null) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("inclusion_date"), endDateTime);
        };
    }
}
