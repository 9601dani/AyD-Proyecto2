package com.bugtrackers.ms_user.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.models.RolePage;

@Repository
public interface RolePageRepository extends JpaRepository<RolePage, Integer> {
    
    @Query(value = """
            SELECT rhp.* from module m 
            JOIN page p ON p.FK_Module = m.id
            JOIN role_has_page rhp ON rhp.FK_Page = p.id
            JOIN role r ON rhp.FK_Role = r.id
            JOIN user_has_role uhr ON uhr.FK_Role = r.id
            JOIN user u ON u.id = uhr.FK_User
            WHERE u.id=:id AND p.is_available = 1 AND m.is_available = 1
            GROUP BY rhp.id
            """, nativeQuery = true)
    List<RolePage> findRolePageByUserId(@Param("id") Integer id);

    void deleteAllByRole(Role role);
}
