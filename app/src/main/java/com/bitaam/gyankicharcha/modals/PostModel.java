package com.bitaam.gyankicharcha.modals;

import java.io.Serializable;
import java.util.ArrayList;

public class PostModel implements Serializable {

    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int IMAGE_VIDEO_TYPE=2;
    public static final int DOCUMENT_IMAGE_TYPE=3;
    public static final int GOOGLE_ADS_TYPE=4;
    public static final int CUSTOM_ADS_TYPE=5;
    public static final int VISIBILITY_PUBLIC = 1;
    public static final int VISIBILITY_PRIVATE = 0;
    public static final int CATEGORY_EDUCATION = 1;
    public static final int CATEGORY_GENERAL = 1;

    String profileName,postDate,postText,postAuthId,postLocation,postDevice,profileImageUrl;
    String dataUrls;
    String offlineDataUrls;
    int postType,postVisibility,postCategory;

    public PostModel() {
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostAuthId() {
        return postAuthId;
    }

    public void setPostAuthId(String postAuthId) {
        this.postAuthId = postAuthId;
    }

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public String getPostDevice() {
        return postDevice;
    }

    public void setPostDevice(String postDevice) {
        this.postDevice = postDevice;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDataUrls() {
        return dataUrls;
    }

    public void setDataUrls(String dataUrls) {
        this.dataUrls = dataUrls;
    }

    public String getOfflineDataUrls() {
        return offlineDataUrls;
    }

    public void setOfflineDataUrls(String offlineDataUrls) {
        this.offlineDataUrls = offlineDataUrls;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public int getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(int postCategory) {
        this.postCategory = postCategory;
    }

    public int getPostVisibility() {
        return postVisibility;
    }

    public void setPostVisibility(int postVisibility) {
        this.postVisibility = postVisibility;
    }
}
