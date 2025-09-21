package lk.ijse.backend.controller;

import jakarta.servlet.annotation.MultipartConfig;
import lk.ijse.backend.dto.ProfileDTO;
import lk.ijse.backend.dto.ProfileDataDTO;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.dto.UserDTO;
import lk.ijse.backend.service.ProfileService;
import lk.ijse.backend.service.UserService;
import lk.ijse.backend.service.imple.UserServiceImpl;
import lk.ijse.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/profile")
@CrossOrigin
@MultipartConfig(fileSizeThreshold = 10 * 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024)
public class ProfileController {

    @Autowired
    private final ProfileService profileService;
    @Autowired
    private final UserServiceImpl userServiceImpl;
    @Autowired
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public ProfileController(ProfileService profileService, UserServiceImpl userServiceImpl, UserService userService, JwtUtil jwtUtil) {
        this.profileService = profileService;
        this.userServiceImpl = userServiceImpl;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping(path = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> saveImage(@ModelAttribute ProfileDataDTO profileDataDTO) {

        String savedProfile = userService.saveItemImage(profileDataDTO.getImage());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(HttpStatus.CREATED.value(), "Success", savedProfile));
    }

    @PutMapping(path = "/updateProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    public ResponseEntity<ResponseDTO> updateProfile(@RequestHeader("Authorization") String token,
                                                     @ModelAttribute ProfileDataDTO profileDataDTO) {
        // Print token (without "Bearer ")
        String rawToken = token.substring(7);
        System.out.println("=== Incoming Token ===");
        System.out.println(rawToken);

        // Get user from token
        UserDTO userDTO = userService.getUserDTOByToken(rawToken);
        System.out.println("=== Extracted UserDTO ===");
        System.out.println("Email: " + userDTO.getEmail());
        System.out.println("Username: " + userDTO.getUsername());
        System.out.println("Role: " + userDTO.getRole());

        // Print incoming profile data
        System.out.println("=== Incoming ProfileDataDTO ===");
        System.out.println("First Name: " + profileDataDTO.getFirstName());
        System.out.println("Last Name: " + profileDataDTO.getLastName());
        System.out.println("Phone: " + profileDataDTO.getContact());
        System.out.println("Address: " + profileDataDTO.getAddress());
        System.out.println("Profile Picture: " +
                (profileDataDTO.getImage() != null ? profileDataDTO.getImage().getOriginalFilename() : "No File"));

        // Update profile
        ProfileDTO updatedProfile = userService.updateUserProfile(userDTO.getEmail(), profileDataDTO);

        // Print updated profile result
        System.out.println("=== Updated ProfileDTO ===");
        System.out.println("First Name: " + updatedProfile.getFirstName());
        System.out.println("Last Name: " + updatedProfile.getLastName());
        System.out.println("Phone: " + updatedProfile.getContact());
        System.out.println("Address: " + updatedProfile.getAddress());
        System.out.println("Profile Image URL: " + updatedProfile.getImage());

        // Return response
        ResponseDTO response = new ResponseDTO(HttpStatus.OK.value(), "Success", updatedProfile);
        System.out.println("=== Final ResponseDTO ===");
        System.out.println(response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<ResponseDTO> getUserDetails(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsernameFromToken(token.substring(7));
        ProfileDTO profileDTO = userServiceImpl.loadProfileDetailsByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO(HttpStatus.OK.value(), "Success", profileDTO));
    }

}
