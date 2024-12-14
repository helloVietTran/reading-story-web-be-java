package com.viettran.reading_story_web.validator;

import com.viettran.reading_story_web.dto.request.UserCreationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatchesConstraint, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserCreationRequest) {
            UserCreationRequest userCreationRequest = (UserCreationRequest) obj;
            return userCreationRequest.getPassword().equals(userCreationRequest.getConfirmPassword());
        }
        return false;
    }
}
