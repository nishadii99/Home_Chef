package lk.ijse.backend.service.imple;

import jakarta.servlet.annotation.MultipartConfig;
import lk.ijse.backend.dto.ProfileDTO;
import lk.ijse.backend.dto.ProfileDataDTO;
import lk.ijse.backend.dto.UserDTO;
import lk.ijse.backend.entity.User;
import lk.ijse.backend.repository.ProfileRepo;
import lk.ijse.backend.repository.UserRepo;
import lk.ijse.backend.service.UserService;
import lk.ijse.backend.util.ImageUploader;
import lk.ijse.backend.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProfileRepo profileRepo;

    @Autowired
    private JwtUtil jwtUtil;

    // Base directory
    private static final String BASE_DIR = "C:\\Users\\DELL\\Desktop\\HomeChefProfiles\\";
    private static final String ITEM_DIR = BASE_DIR + "Items\\";
    private static final String PROFILE_DIR = BASE_DIR + "Profile\\";
    private static final String FRONTEND_DIR = "HomeChefProfiles\\";

    static {
        createIfNotExistDirectory(ITEM_DIR);
        createIfNotExistDirectory(PROFILE_DIR);
    }

    // ----------------- IMAGE SAVING -----------------

    @Override
    public String saveItemImage(MultipartFile image) {
        return saveImage(image, ITEM_DIR, "Items");
    }

    @Override
    public String saveProfileImage(MultipartFile image) {
        return saveImage(image, PROFILE_DIR, "Profile");
    }

    private String saveImage(MultipartFile image, String savingDir, String folderName) {
        if (image == null || image.isEmpty()) return null;

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(savingDir, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            image.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + fileName, e);
        }

        return FRONTEND_DIR + folderName + "\\" + fileName;
    }

    private static void createIfNotExistDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
    }

    // ----------------- USER METHODS -----------------

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email);
        return user != null ? modelMapper.map(user, UserDTO.class) : null;
    }

    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) return HttpStatus.NOT_ACCEPTABLE.value();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        userRepository.save(modelMapper.map(userDTO, User.class));
        return HttpStatus.CREATED.value();
    }

    @Override
    public int updateUser(UserDTO userDTO) {
        if (!userRepository.existsByEmail(userDTO.getEmail())) return HttpStatus.NOT_FOUND.value();

        User existingUser = userRepository.findByEmail(userDTO.getEmail());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Only encode password if changed
        if (!userDTO.getPassword().equals(existingUser.getPassword())) {
            userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        }

        userRepository.save(modelMapper.map(userDTO, User.class));
        return HttpStatus.CREATED.value();
    }

    @Override
    public UserDTO getUserEmailByToken(String token) {
        String email = jwtUtil.getUsernameFromToken(token);
        return getUserByEmail(email);
    }

    // ----------------- PROFILE METHODS -----------------

    @Override
    public ProfileDTO updateUserProfile(String email, ProfileDataDTO profileDataDTO) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        String url = ImageUploader.uploadImage(profileDataDTO.getImage());
        user.setImage(url);

        user.setFirstName(profileDataDTO.getFirstName());
        user.setLastName(profileDataDTO.getLastName());
        user.setAddress(profileDataDTO.getAddress());
        user.setContact(profileDataDTO.getContact());

        userRepository.save(user);

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
    public User getUserProfile(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDTO getUserDTOByToken(String token) {
        String email = jwtUtil.getUsernameFromToken(token);
        return getUserByEmail(email);
    }

    // ----------------- SPRING SECURITY -----------------

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthority(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    @Override
    public UserDTO loadUserDetailsByUsername(String username) {
        User user = userRepository.findByEmail(username);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public ProfileDTO loadProfileDetailsByUsername(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) throw new UsernameNotFoundException("User not found");
        return modelMapper.map(user, ProfileDTO.class);
    }
}
