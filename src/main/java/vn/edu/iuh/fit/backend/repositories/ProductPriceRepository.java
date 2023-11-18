package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.backend.models.ProductPrice;
@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
}