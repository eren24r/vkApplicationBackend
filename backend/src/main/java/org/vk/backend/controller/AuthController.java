package org.vk.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.vk.backend.load.request.LoginReq;
import org.vk.backend.load.request.SignupReq;
import org.vk.backend.load.response.JWTTokenSuccessResponse;
import org.vk.backend.load.response.MessageResponse;
import org.vk.backend.security.JWTProvider;
import org.vk.backend.security.SecurityConst;
import org.vk.backend.service.UserService;
import org.vk.backend.vallidation.ResponseErrorValidator;

@CrossOrigin("http://localhost:8080/")
@RestController
@RequestMapping("/vk/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ResponseErrorValidator responseErrorValidator;
    @Autowired
    private UserService userService;

    public static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signing")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginReq loginReq, BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return new ResponseEntity<>(new MessageResponse("Validation Error"), HttpStatus.BAD_REQUEST);;

        Authentication authentication = null;

        try {
           authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginReq.getUsername(),
                    loginReq.getPassword()
            ));
        }catch (Exception e){
            LOG.error("df " + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("user not found"), HttpStatus.BAD_REQUEST);
        }

        LOG.info(loginReq.getUsername());
        LOG.info(loginReq.getPassword());

        String jwt = null;
        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = SecurityConst.TOKEN_PREFIX + jwtProvider.genToken(authentication);
        }catch (Exception e){
            LOG.error("djwtErroer: " + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("Token Gen Error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOG.info("Logged user: " + loginReq.getUsername());
        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupReq signupReq, BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

        userService.createUser(signupReq);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
