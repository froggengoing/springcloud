package com.froggengo.practise.validate;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.Validator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidateTest {
    @Autowired
    IsMobileServiceImpl mobileService;
    @Autowired
    private Validator validator;
    @Test
    public void testInvalid() {
        CustomFieldBean customFieldBean = new CustomFieldBean();
        customFieldBean.setMobile("1.2.33");
        //mobileService.doCustomField(customFieldBean);
        System.out.println(validator.validate(customFieldBean));
    }

}
