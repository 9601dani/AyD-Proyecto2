package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.PageRequest;
import com.bugtrackers.ms_user.dto.request.RoleRequest;
import com.bugtrackers.ms_user.dto.response.PageResponse;
import com.bugtrackers.ms_user.dto.response.RoleResponse;
import com.bugtrackers.ms_user.exceptions.ServiceNotSaveException;
import com.bugtrackers.ms_user.exceptions.UserNotFoundException;
import com.bugtrackers.ms_user.models.Page;
import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.models.RolePage;
import com.bugtrackers.ms_user.repositories.PageRepository;
import com.bugtrackers.ms_user.repositories.RolePageRepository;
import com.bugtrackers.ms_user.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PageRepository pageRepository;
    private final RolePageRepository rolePageRepository;

    public List<RoleResponse> getRoles() {
        return this.roleRepository.findAll().stream().map(RoleResponse::new).toList();
    }

    public RoleResponse saveRole(RoleRequest request) {
        Optional<Role> optional = this.roleRepository.findByName(request.name());

        if (optional.isPresent()) {
            throw new ServiceNotSaveException("El rol ya existe!");
        }

        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());

        Role saved = this.roleRepository.save(role);
        RoleResponse response = new RoleResponse(saved);
        return response;
    }

    public List<PageResponse> getPagesByRoleId(Integer id) {
        Optional<Role> optional = this.roleRepository.findById(id);

        if(optional.isEmpty()) {
            throw new UserNotFoundException("No se encontró el role.");
        }

        Role role = optional.get();
        return role.getPages().stream().map(PageResponse::new).toList();
    }

    public List<PageResponse> getAllPages() {
        List<Page> pages = this.pageRepository.findAllByIsAvailable(true);
        return pages.stream().map(PageResponse::new).toList();
        
    }

    public String updatePages(Integer id, List<PageRequest> pageRequests) {
        
        Optional<Role> rOptional = this.roleRepository.findById(id);

        if(rOptional.isEmpty()) {
            throw new UserNotFoundException("Role no encontrado.");
        }

        Role role = rOptional.get();

        this.rolePageRepository.deleteAllByRole(role);

        List<RolePage> rolePages = pageRequests.stream().map(page -> {
            RolePage rolePage =new RolePage();
            rolePage.setRole(role);
            rolePage.setPage(new Page(page.id(), page.name(), page.path(), null, null, null, null));
            return rolePage;
        }).toList();
        this.rolePageRepository.saveAll(rolePages);
        return "Privilegios actualizados.";
    }



}
