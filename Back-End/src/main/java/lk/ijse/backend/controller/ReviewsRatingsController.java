package lk.ijse.backend.controller;

import jakarta.validation.Valid;
import lk.ijse.backend.dto.ResponseDTO;
import lk.ijse.backend.dto.ReviewsRatingsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/reviewsRatings")
@CrossOrigin
public class ReviewsRatingsController {

    @PostMapping(path = "save")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    ResponseEntity<ResponseDTO>saveReviewsRatings(@RequestBody @Valid ReviewsRatingsDTO reviewsRatingsDTO) {
        ResponseDTO responseDTO = new ResponseDTO(
                HttpStatus.CREATED.value(),
                "ReviewsRatings Saved Successfully",
                reviewsRatingsDTO
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping(path = "get")
    @PreAuthorize("hasAnyAuthority('SELLER','BUYER')")
    ResponseEntity<ResponseDTO>getReviewsRatings(@RequestBody ReviewsRatingsDTO reviewsRatingsDTO) {
        ResponseDTO responseDTO = new ResponseDTO(
                HttpStatus.OK.value(),
                "Success",
                reviewsRatingsDTO
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
