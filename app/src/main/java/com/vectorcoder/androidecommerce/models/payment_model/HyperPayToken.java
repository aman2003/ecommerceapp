package com.vectorcoder.androidecommerce.models.payment_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Muhammad Nabeel on 27/11/2018.
 */
public class HyperPayToken {
    
    
    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("data")
    @Expose
    private String token;
    @SerializedName("message")
    @Expose
    private String message;
    
    public String getSuccess() {
        return success;
    }
    
    public void setSuccess(String success) {
        this.success = success;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
}
