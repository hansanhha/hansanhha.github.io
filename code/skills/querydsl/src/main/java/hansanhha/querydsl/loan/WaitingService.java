package hansanhha.querydsl.loan;

import hansanhha.querydsl.loan.dto.WaitListResponse;
import hansanhha.querydsl.loan.repository.WaitListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WaitingService {

    private final WaitListRepository repository;

    public WaitListResponse applyWaiting(String memberNumber, String isbn) {
        return null;
    }

    public void cancelWaiting(Long waitlistId) {

    }
}
