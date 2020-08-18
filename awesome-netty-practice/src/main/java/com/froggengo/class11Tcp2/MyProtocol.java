package com.froggengo.class11Tcp2;

public class MyProtocol {
    private int length;
    private byte[] context;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }
}
