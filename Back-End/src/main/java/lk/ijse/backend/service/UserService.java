package lk.ijse.backend.service;

import lk.ijse.backend.dto.ProfileDTO;
import lk.ijse.backend.dto.ProfileDataDTO;
import lk.ijse.backend.dto.UserDTO;
import lk.ijse.backend.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    int saveUser(UserDTO userDTO);
    UserDTO getCurrentUser(String email);
    UserDTO getUserByEmail(String email);
    int updateUser(UserDTO userDTO);
    UserDTO getUserEmailByToken(String token);
    ProfileDTO updateUserProfile(String email, ProfileDataDTO profileDataDto);
    public String saveItemImage(MultipartFile image);
    User getUserProfile(String email);
    String saveProfileImage(MultipartFile image);
    UserDTO getUserDTOByToken(String token);

    UserDTO loadUserDetailsByUsername(String username);

    ProfileDTO loadProfileDetailsByUsername(String username);
}
