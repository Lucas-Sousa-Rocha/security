package com.lucas_sousa_rocha.security.repository;

import com.lucas_sousa_rocha.security.model.ConfigEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigEmailRepository extends JpaRepository<ConfigEmail, Long> {
}
