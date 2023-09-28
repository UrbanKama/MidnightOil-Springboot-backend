//package com.devrassicpark.midnightoil.Controllers;
//
//import com.devrassicpark.midnightoil.DTO.JwtAuthenticationResponse;
//import com.devrassicpark.midnightoil.DTO.SignInRequest;
//import com.devrassicpark.midnightoil.DTO.SignUpRequest;
//import com.devrassicpark.midnightoil.services.AuthenticationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1")
//@RequiredArgsConstructor
//public class AuthenticationController {
//
//    private final AuthenticationService authenticationService;
//
//    @PostMapping("/signup")
//    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request){
//        return authenticationService.signup(request);
//    }
//
//    @PostMapping("/signin")
//    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request){
//        return authenticationService.signin(request);
//    }
//}
