package com.zconnect.zutto.zconnect.ItemFormats;

public class CIF2 {
    private String name;
    private String message;
    private long ts;

    public CIF2(String name, String message, long ts) {
        this.name = name;
        this.message = message;
        this.ts = ts;
    }

    public CIF2() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
