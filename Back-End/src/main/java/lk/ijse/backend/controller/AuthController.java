package lk.ijse.backend.controller;

import lk.ijse.backend.dto.AuthDTO;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.dto.UserDTO;
import lk.ijse.backend.repository.UserRepo;
import lk.ijse.backend.service.imple.UserServiceImpl;
import lk.ijse.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController { private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final ResponseDTO responseDTO;
    private UserRepo userRepo;

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseDTO> authenticate(@RequestBody UserDTO userDTO) {

//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Invalid Credentials", e.getMessage()));
//        }
//
//        UserDTO loadedUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
//        if (loadedUser == null) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(new ResponseDTO(HttpStatus.CONFLICT.value(), "Authorization Failure! Please Try Again", null));
//        }
//
//        String token = jwtUtil.generateToken(loadedUser);
//        if (token == null || token.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(new ResponseDTO(HttpStatus.CONFLICT.value(), "Authorization Failure! Please Try Again", null));
//        }
//
//        AuthDTO authDTO = new AuthDTO();
//        authDTO.setEmail(loadedUser.getEmail());
//        authDTO.setToken(token);
//        authDTO.setRole(loadedUser.getRole());
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", token);
//        response.put("role", authDTO.getRole());
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ResponseDTO(HttpStatus.CREATED.value(), "Success", authDTO));

        System.out.println("Login attempt for user: " + userDTO.getEmail());
        System.out.println("Password provided: " + userDTO.getPassword());
        UserDTO userDTO1 = userService.getUserByEmail(userDTO.getEmail());
        String token = jwtUtil.generateToken(userDTO1);
        AuthDTO authDTO = AuthDTO.builder()
                .email(userDTO1.getEmail())
                .role(userDTO1.getRole())
                .token(token)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(HttpStatus.CREATED.value(), "Success", authDTO));

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDTO userDTO) {
        System.out.println("Login attempt for user: " + userDTO.getEmail());
        System.out.println("Password provided: " + userDTO.getPassword());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));

        String token = jwtUtil.generateToken(userDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", userDTO.getRole());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/check")
    public ResponseEntity<String> checkAuth(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                "User: " + userDetails.getUsername() + "\n" +
                        "Authorities: " + userDetails.getAuthorities()
        );
    }

}

