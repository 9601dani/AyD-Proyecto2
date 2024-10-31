package com.bugtrackers.ms_user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = """ 
            SELECT s.name AS service_name, COUNT(ahs.FK_Service) AS total_requests
            FROM service s
            LEFT JOIN appointment_has_service ahs ON s.id = ahs.FK_Service
            GROUP BY s.name""", nativeQuery = true)
    List<Object[]> getPopularity();

    @Query(value= """
            SELECT r.name AS role_name, COUNT(uhr.FK_User) AS total_users
            FROM role r
            LEFT JOIN user_has_role uhr ON r.id = uhr.FK_Role
            LEFT JOIN user u ON uhr.FK_User = u.id
            GROUP BY r.name;
            """, nativeQuery = true)
    List<Object[]> getUserByRole();

    @Query(value= """

            SELECT r.name AS resource_name, COUNT(a.id) AS total_uses
            FROM resource r
            LEFT JOIN appointment a ON r.id = a.FK_Resource
            GROUP BY r.id;
            """, nativeQuery = true)
    List<Object[]> getPopularityResources();
    
}
