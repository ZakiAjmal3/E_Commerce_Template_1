package com.examatlas.crownpublication.Models;

public class DashboardExamCategoryModel {
    String examName,image;

    public DashboardExamCategoryModel(String examName, String image) {
        this.examName = examName;
        this.image = image;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
