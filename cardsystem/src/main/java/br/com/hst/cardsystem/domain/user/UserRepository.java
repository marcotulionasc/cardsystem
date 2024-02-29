/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.currentlyActive = ?1 ORDER BY u.id")
    List<User> findByCurrentlyActive(boolean currentlyActive);

    @Query("SELECT u FROM User u WHERE u.salary >= ?1 AND u.salary <= ?2 AND u.currentlyActive = true ORDER BY u.salary DESC")
    List<User> findBySalary(BigDecimal salaryMin, BigDecimal salaryMax);

}
