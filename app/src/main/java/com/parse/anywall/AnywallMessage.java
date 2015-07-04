package com.parse.anywall;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Rohit on 5/1/2015.
 * Data model for a Message.
 */
@ParseClassName("Messages")
public class AnywallMessage extends ParseObject {

    public static ParseQuery<AnywallMessage> getQuery() {
        return ParseQuery.getQuery(AnywallMessage.class);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public ParseUser getSender() {
        return getParseUser("sender");
    }

    public void setSender(ParseUser value) {
        put("sender", value);
    }

    public ParseUser getRecipient() {
        return getParseUser("recipient");
    }

    public void setRecipient(ParseUser value) {
        //replaced arguments with string instead of ParseUser
        put("recipient", value);
    }
}
