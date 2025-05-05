package mate.academy.carsharing.repository;

import java.util.Optional;
import mate.academy.carsharing.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Page<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Pageable pageable, Long userId);

    Page<Rental> findAllByUserIdAndActualReturnDateIsNull(Pageable pageable, Long userId);

    Optional<Rental> findByIdAndUserId(Long rentalId, Long userId);
}
