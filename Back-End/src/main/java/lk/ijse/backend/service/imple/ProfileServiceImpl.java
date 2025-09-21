package lk.ijse.backend.service.imple;

import jakarta.servlet.annotation.MultipartConfig;
import lk.ijse.backend.dto.ProfileDTO;
import lk.ijse.backend.dto.ProfileDataDTO;
import lk.ijse.backend.entity.User;
import lk.ijse.backend.repository.ProfileRepo;
import lk.ijse.backend.repository.UserRepo;
import lk.ijse.backend.service.ProfileService;
import lk.ijse.backend.service.UserService;
import lk.ijse.backend.util.ImageUploader;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Transactional
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepo profileRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    // Base directories for images
    private static final String BASE_DIR = "C:\\Users\\DELL\\Desktop\\HomeChefProfiles\\";
    private static final String PROFILE_DIR = BASE_DIR + "Profile\\";
    private static final String FRONTEND_DIR = "HomeChefProfiles/";

    static {
        createIfNotExistDirectory(PROFILE_DIR);
    }

    private static void createIfNotExistDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
    }

    // ----------------- IMAGE HANDLING -----------------
    private String saveProfileImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(PROFILE_DIR, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            image.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile image: " + fileName, e);
        }

        return FRONTEND_DIR + "Profile/" + fileName;
    }

    // ----------------- PROFILE METHODS -----------------

    @Override
    public ProfileDTO updateProfile(String email, ProfileDataDTO profileDataDTO) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        // Update basic fields
        user.setFirstName(profileDataDTO.getFirstName());
        user.setLastName(profileDataDTO.getLastName());
        user.setAddress(profileDataDTO.getAddress());
        user.setContact(profileDataDTO.getContact());

        // Handle image upload via ImageUploader utility
        if (profileDataDTO.getImage() != null) {
            // Upload image using your utility (like the old method)
            String uploadedUrl = ImageUploader.uploadImage(profileDataDTO.getImage());
            user.setImage(uploadedUrl);
        }

        // Save user
        userRepo.save(user);

        // Map to ProfileDTO
        ProfileDTO dto = new ProfileDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setContact(user.getContact());
        dto.setImage(user.getImage());

        return dto;
    }


    @Override
    public ProfileDTO getProfileByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        ProfileDTO dto = modelMapper.map(user, ProfileDTO.class);
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }
}
