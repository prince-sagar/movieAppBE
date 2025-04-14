package com.movieApp.MovieBookingApplication.controller;

import com.movieApp.MovieBookingApplication.dto.AuthenticationRequest;
import com.movieApp.MovieBookingApplication.dto.SignupRequestDTO;
import com.movieApp.MovieBookingApplication.dto.UserDto;
import com.movieApp.MovieBookingApplication.entity.User;
import com.movieApp.MovieBookingApplication.repository.UserRepository;
import com.movieApp.MovieBookingApplication.services.authentication.AuthService;
import com.movieApp.MovieBookingApplication.services.jwt.UserDetailsServiceImpl;
import com.movieApp.MovieBookingApplication.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AuthenticationController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public static final String TOKEN_PREFIX = "Bearer";

    public static final String HEADER_STRING = "Authorization";

    @PostMapping("/client/sign-up")

    public ResponseEntity<?> signupClient(@RequestBody SignupRequestDTO signupRequestDTO){

        if(authService.presentByEmail(signupRequestDTO.getEmail())){
            return new ResponseEntity<>("Client already exits with this Email", HttpStatus.NOT_ACCEPTABLE);
        }

        UserDto createUser = authService.signupClient(signupRequestDTO);

        return new ResponseEntity<>(createUser, HttpStatus.OK);
    }

    @PostMapping("/company/sign-up")
    public ResponseEntity<?> signupCompany(@RequestBody SignupRequestDTO signupRequestDTO){

        if(authService.presentByEmail(signupRequestDTO.getEmail())){
            return new ResponseEntity<>("Company already exits with this Email", HttpStatus.NOT_ACCEPTABLE);
        }

        UserDto createUser = authService.signupCompany(signupRequestDTO);

        return new ResponseEntity<>(createUser, HttpStatus.OK);
    }

    @PostMapping({"/authenticate"})

    public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                          HttpServletResponse response) throws IOException, JSONException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        User user = userRepository.findFirstByEmail(authenticationRequest.getUsername());

        response.getWriter().write(new JSONObject()
                .put("userId", user.getId())
                .put("role", user.getRole())
                .toString());

        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader("Access-Control-Allow-Headers", "Authorization," +
                "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept, X-Custom-Header");

        response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt);

    }
}
