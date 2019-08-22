package com.vectorcoder.androidecommerce.models.address_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddressDetails {
    
    @SerializedName("address_id")
    @Expose
    private int addressId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("suburb")
    @Expose
    private String suburb;
    @SerializedName("postcode")
    @Expose
    private String postcode;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("countries_id")
    @Expose
    private int countriesId;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("zone_id")
    @Expose
    private int zoneId;
    @SerializedName("zone_code")
    @Expose
    private String zoneCode;
    @SerializedName("zone_name")
    @Expose
    private String zoneName;
    @SerializedName("default_address")
    @Expose
    private int defaultAddress;
    @SerializedName("delivery_phone")
    @Expose
    private String deliveryPhone;
    
    public String getDeliveryPhone() {
        return deliveryPhone;
    }
    
    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }
    
    
    /**
     *
     * @return
     *     The addressId
     */
    public int getAddressId() {
        return addressId;
    }
    
    /**
     *
     * @param addressId
     *     The address_id
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
    
    /**
     *
     * @return
     *     The gender
     */
    public String getGender() {
        return gender;
    }
    
    /**
     *
     * @param gender
     *     The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     *
     * @return
     *     The company
     */
    public String getCompany() {
        return company;
    }
    
    /**
     *
     * @param company
     *     The company
     */
    public void setCompany(String company) {
        this.company = company;
    }
    
    /**
     *
     * @return
     *     The firstname
     */
    public String getFirstname() {
        return firstname;
    }
    
    /**
     *
     * @param firstname
     *     The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    /**
     *
     * @return
     *     The lastname
     */
    public String getLastname() {
        return lastname;
    }
    
    /**
     *
     * @param lastname
     *     The lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    /**
     *
     * @return
     *     The street
     */
    public String getStreet() {
        return street;
    }
    
    /**
     *
     * @param street
     *     The street
     */
    public void setStreet(String street) {
        this.street = street;
    }
    
    /**
     *
     * @return
     *     The suburb
     */
    public String getSuburb() {
        return suburb;
    }
    
    /**
     *
     * @param suburb
     *     The suburb
     */
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }
    
    /**
     *
     * @return
     *     The postcode
     */
    public String getPostcode() {
        return postcode;
    }
    
    /**
     *
     * @param postcode
     *     The postcode
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    
    /**
     *
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }
    
    /**
     *
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**
     *
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }
    
    /**
     *
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**
     *
     * @return
     *     The countriesId
     */
    public int getCountriesId() {
        return countriesId;
    }
    
    /**
     *
     * @param countriesId
     *     The countries_id
     */
    public void setCountriesId(int countriesId) {
        this.countriesId = countriesId;
    }
    
    /**
     *
     * @return
     *     The countryName
     */
    public String getCountryName() {
        return countryName;
    }
    
    /**
     *
     * @param countryName
     *     The country_name
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    /**
     *
     * @return
     *     The zoneId
     */
    public int getZoneId() {
        return zoneId;
    }
    
    /**
     *
     * @param zoneId
     *     The zone_id
     */
    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }
    
    /**
     *
     * @return
     *     The zoneCode
     */
    public String getZoneCode() {
        return zoneCode;
    }
    
    /**
     *
     * @param zoneCode
     *     The zone_code
     */
    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }
    
    /**
     *
     * @return
     *     The zoneName
     */
    public String getZoneName() {
        return zoneName;
    }
    
    /**
     *
     * @param zoneName
     *     The zone_name
     */
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }
    
    /**
     *
     * @return
     * The defaultAddress
     */
    public int getDefaultAddress() {
        return defaultAddress;
    }
    
    /**
     *
     * @param defaultAddress
     * The default_address
     */
    public void setDefaultAddress(int defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
    
}
