package hansanhha.specification;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SpecUserService {

    private final SpecUserRepository specUserRepository;

    public SpecUserService(SpecUserRepository specUserRepository) {
        this.specUserRepository = specUserRepository;
    }

    public SpecUser create(String firstName, String lastName, int age) {
        return specUserRepository.save(new SpecUser(firstName, lastName, age));
    }

    public List<SpecUser> findUsers(String lastName, int age) {
        Specification<SpecUser> spec = Specification
                .where(SpecUserSpecification.hasLastName(lastName))
                .and(SpecUserSpecification.hasAgeGreaterThan(age));

        return specUserRepository.findAll(spec);
    }
}
