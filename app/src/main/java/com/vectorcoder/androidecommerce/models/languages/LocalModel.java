package com.vectorcoder.androidecommerce.models.languages;

/**
 * Created by Muhammad Nabeel on 19/11/2018.
 */
public class LocalModel {
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public int getCountry_icon() {
        return country_icon;
    }
    
    public void setCountry_icon(int country_icon) {
        this.country_icon = country_icon;
    }
    
    public String getLanguage_code() {
        return language_code;
    }
    
    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }
    
    public String language;
    public int country_icon;
    public String language_code;
    
}
