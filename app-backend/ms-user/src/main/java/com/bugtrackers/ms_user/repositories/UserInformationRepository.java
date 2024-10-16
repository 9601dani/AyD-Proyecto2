package com.bugtrackers.ms_user.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bugtrackers.ms_user.models.UserInformation;


public interface UserInformationRepository extends JpaRepository<UserInformation, Integer> {
    
    @Query (value = """
            SELECT * FROM user_information WHERE FK_User = :id
            """, nativeQuery = true)
    Optional<UserInformation> findByUserId(@Param("id")Integer id);


    @Query (value = """
            UPDATE user_information SET image_profile = :image WHERE FK_User = :id
            """, nativeQuery = true)
    void updateImageProfile(@Param("id")Integer id, @Param("image")String image);

}
