package com.bugtrackers.ms_user.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.Page;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    
    List<Page> findAllByIsAvailable(Boolean isAvailable);
}
