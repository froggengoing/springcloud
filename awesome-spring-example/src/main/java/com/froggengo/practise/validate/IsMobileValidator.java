package com.froggengo.practise.validate;



import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    IsMobile isMobile;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        this.isMobile=constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(isMobile.required()){
            boolean isMobile = IsMobileUtil.isMobile(value);
        }else {
            if (StringUtils.isBlank(value)) {
                return true;
            }else{
                return IsMobileUtil.isMobile(value);
            }
        }
        return false;
    }
}
