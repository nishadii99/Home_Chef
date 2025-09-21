package lk.ijse.backend.service;

import lk.ijse.backend.dto.ProfileDTO;
import lk.ijse.backend.dto.ProfileDataDTO;

public interface ProfileService {

    ProfileDTO updateProfile(String email, ProfileDataDTO profileDataDTO);

    ProfileDTO getProfileByEmail(String email);
}
