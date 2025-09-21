package lk.ijse.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lk.ijse.backend.dto.*;
import lk.ijse.backend.service.UserService;
import lk.ijse.backend.service.imple.MailSendServiceImpl;
import lk.ijse.backend.service.imple.UserServiceImpl;
import lk.ijse.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final MailSendServiceImpl mailSendService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserDTO userDTO) {
        if (userDTO.getRole() == null) {
            System.out.println("Role was null, setting to BUYER by default.");
            userDTO.setRole("BUYER");
        }
        try {
            int res = userService.saveUser(userDTO);
            switch (res) {
                case 201 -> { // Created
                    String token = jwtUtil.generateToken(userDTO);
                    AuthDTO authDTO = new AuthDTO();
                    authDTO.setEmail(userDTO.getEmail());
                    authDTO.setToken(token);
                    mailSendService.sendRegisteredEmail(
                            userDTO.getUsername(),
                            userDTO.getEmail(),
                            "Registered Successfully!"
                    );

                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(new ResponseDTO(
                                    HttpStatus.CREATED.value(),
                                    "Success",
                                    authDTO
                            ));
                }
                case 406 -> { // Not Acceptable
                    return ResponseEntity
                            .status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(
                                    HttpStatus.NOT_ACCEPTABLE.value(),
                                    "Email Already Used",
                                    null
                            ));
                }
                default -> { // Bad Gateway
                    return ResponseEntity
                            .status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(
                                    HttpStatus.BAD_GATEWAY.value(),
                                    "Error",
                                    null
                            ));
                }
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }


    //
//    @PutMapping("/update")
//    public ResponseEntity<ProfileDTO> updateProfile(
//            @RequestHeader("Authorization") String token,
//            @ModelAttribute ProfileDataDTO profileDataDTO) {
//
//        // Extract email from JWT token
//        String email = jwtUtil.extractEmailFromToken(token.substring(7));
//
//        // Update profile
//        ProfileDTO updatedProfile = userService.updateUserProfile(email, profileDataDTO);
//
//        return ResponseEntity.ok(updatedProfile);
//    }
    @GetMapping("/get")
    public String getUserProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer " prefix
        String username = JwtUtil.JWT_TOKEN_VALIDITY + jwtUtil.getUsernameFromToken(token);
        return "User Email: " + username;
    }

//    @PutMapping("/update")
//    public ResponseEntity<ProfileDTO> updateProfile(@RequestHeader("Authorization") String token,
//                                                     @ModelAttribute ProfileDataDTO profileDataDTO) {
//
//        // Extract email from JWT token
//        String email = jwtUtil.getUsernameFromToken(token.substring(7));
//
//        // Update profile
//        ProfileDTO updatedProfile = userService.updateUserProfile(email, profileDataDTO);
//        return ResponseEntity.ok(updatedProfile);
//    }


}
