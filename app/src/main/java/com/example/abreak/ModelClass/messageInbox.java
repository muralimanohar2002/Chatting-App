package com.example.abreak.ModelClass;

public class messageInbox {
    private String messageId;
    private String senderId;
    private String message;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;
    private long feelings = -1;
    private long timeofmessage;

    public messageInbox(){

    }

    public messageInbox(String senderId, String message, long timeofmessage) {
        this.senderId = senderId;
        this.message = message;
        this.timeofmessage = timeofmessage;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getFeelings() {
        return feelings;
    }

    public void setFeelings(long feelings) {
        this.feelings = feelings;
    }

    public long getTimeofmessage() {
        return timeofmessage;
    }

    public void setTimeofmessage(long timeofmessage) {
        this.timeofmessage = timeofmessage;
    }
}
