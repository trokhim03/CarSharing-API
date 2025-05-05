package mate.academy.carsharing.repository;

import mate.academy.carsharing.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByRental_UserId(Pageable pageable, Long userId);

    boolean existsByRentalIdAndStatus(Long id, Payment.Status status);
}
