package com.bitaam.gyankicharcha.modals;

public class ChatViewTypeModel {

    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int IMAGE_VIDEO_TYPE=2;
    public static final int DOCUMENT_IMAGE_TYPE=3;
    //public static final int TEXT_HEADING_TYPE=4;

    public int type;
    public String dataUrl;
    public String text;
    public  String dateTime;
    public String authId;

    public ChatViewTypeModel() {
    }

    public ChatViewTypeModel(int type, String dataUrl, String text, String dateTime, String authId) {
        this.type = type;
        this.dataUrl = dataUrl;
        this.text = text;
        this.dateTime = dateTime;
        this.authId = authId;
    }
}
