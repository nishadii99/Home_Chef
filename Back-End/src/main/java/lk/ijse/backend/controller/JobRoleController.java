package lk.ijse.backend.controller;

import jakarta.validation.Valid;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.dto.RoleDTO;
import lk.ijse.backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/jobRole")
@CrossOrigin
public class JobRoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<RoleDTO> saveJobRole(@RequestBody @Valid RoleDTO roleDTO) {
        roleService.saveRole(roleDTO);
        return ResponseEntity.ok(roleDTO);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<RoleDTO>> getJobRole() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
