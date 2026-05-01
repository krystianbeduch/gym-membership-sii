package pl.krystianbeduch.gymmembership.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    boolean existsByName(String name);
}