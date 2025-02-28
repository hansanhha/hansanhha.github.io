package hansanhha.simple_jpa_repository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SimpleUserService {

    private final SimpleUserJpaRepository simpleUserJpaRepository;
    private final SimpleUserCrudRepository simpleUserCrudRepository;

    public SimpleUserService(SimpleUserJpaRepository simpleUserJpaRepository, SimpleUserCrudRepository simpleUserCrudRepository) {
        this.simpleUserJpaRepository = simpleUserJpaRepository;
        this.simpleUserCrudRepository = simpleUserCrudRepository;
    }

    public SimpleUser create(String firstName, String lastName, String phoneNumber) {
        return simpleUserJpaRepository.save(new SimpleUser(firstName, lastName, phoneNumber));
    }

    public SimpleUser find(Long id) {
        return simpleUserJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("not found user"));
    }
}
