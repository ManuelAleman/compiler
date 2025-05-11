package com.compiler.MVC.model;

import com.compiler.utils.LowLevelTemplate;

public class ObjectCode {
    private StringBuilder objectCode;

    public ObjectCode(){
        this.objectCode = new StringBuilder();
    }

    public String getObjectCode(){
        objectCode.append(LowLevelTemplate.objectCode.toString());
        return objectCode.toString();
    }

    public void cleanObjectCode(){
        this.objectCode = new StringBuilder();
    }
}
