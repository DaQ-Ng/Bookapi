package com.dat.bookapi.order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>{
    // derived query: Buyer → Email walks into the buyer's User; newest order first
    List<Order> findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
