package lk.ijse.backend.service;

import lk.ijse.backend.dto.RoleDTO;

import java.util.List;


public interface RoleService {
    void saveRole(RoleDTO roleDTO);
    List<RoleDTO>getAllRoles();
    void updateRole(RoleDTO roleDTO);
    void deleteRole(String roleId);
}
