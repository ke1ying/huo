package com.naruto.huo.model;

import javafx.beans.property.adapter.JavaBeanObjectProperty;

public class TextMessage extends BaseMessage {
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
