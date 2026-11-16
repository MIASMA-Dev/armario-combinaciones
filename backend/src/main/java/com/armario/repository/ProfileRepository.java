package com.armario.repository;

import com.armario.domain.Profile;
import com.armario.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByOwner(User owner);
    List<Profile> findBySharedWithContaining(User user);
}