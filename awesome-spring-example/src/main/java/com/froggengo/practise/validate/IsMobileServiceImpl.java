package com.froggengo.practise.validate;

import org.springframework.stereotype.Service;

@Service
public class IsMobileServiceImpl {
    private CustomFieldBean bean;
    public void doCustomField(CustomFieldBean bean){
        this.bean=bean;
    }
}
