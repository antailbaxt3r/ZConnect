package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by Lokesh Garg on 20-12-2017.
 */

public class AnonymousMessages {

    private String Message, MessageId, PostedBy;

    public AnonymousMessages() {

    }

    public AnonymousMessages(String message, String messageId, String postedBy)
    {
        this.Message=message;
        this.MessageId=messageId;
        this.PostedBy= postedBy;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(String postedBy) {
        PostedBy = postedBy;
    }

}
