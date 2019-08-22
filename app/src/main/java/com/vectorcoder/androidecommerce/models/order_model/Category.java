
package com.vectorcoder.androidecommerce.models.order_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("categories_id")
    @Expose
    private Integer categoriesId;
    @SerializedName("categories_name")
    @Expose
    private String categoriesName;
    @SerializedName("categories_image")
    @Expose
    private String categoriesImage;
    @SerializedName("categories_icon")
    @Expose
    private String categoriesIcon;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;

    public Integer getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(Integer categoriesId) {
        this.categoriesId = categoriesId;
    }

    public String getCategoriesName() {
        return categoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }

    public String getCategoriesImage() {
        return categoriesImage;
    }

    public void setCategoriesImage(String categoriesImage) {
        this.categoriesImage = categoriesImage;
    }

    public String getCategoriesIcon() {
        return categoriesIcon;
    }

    public void setCategoriesIcon(String categoriesIcon) {
        this.categoriesIcon = categoriesIcon;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
