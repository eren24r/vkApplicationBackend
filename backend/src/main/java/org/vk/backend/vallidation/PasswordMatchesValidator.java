package org.vk.backend.vallidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.vk.backend.annotation.PasswordMatches;
import org.vk.backend.load.request.SignupReq;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        SignupReq signupReq = (SignupReq) o;
        return signupReq.getPassword().equals(signupReq.getConfirmPassword());
    }
}
