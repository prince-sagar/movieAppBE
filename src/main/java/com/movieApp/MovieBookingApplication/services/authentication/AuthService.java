package com.movieApp.MovieBookingApplication.services.authentication;

import com.movieApp.MovieBookingApplication.dto.SignupRequestDTO;
import com.movieApp.MovieBookingApplication.dto.UserDto;
import org.springframework.stereotype.Service;

public interface AuthService {

    UserDto signupClient(SignupRequestDTO signupRequestDTO);
    Boolean presentByEmail(String email);

    UserDto signupCompany(SignupRequestDTO signupRequestDTO);
}
