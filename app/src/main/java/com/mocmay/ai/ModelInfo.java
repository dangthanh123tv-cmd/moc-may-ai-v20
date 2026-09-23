package com.mocmay.ai;
public final class ModelInfo {
    public final String name; public final long size; public final String sha256;
    public ModelInfo(String name,long size,String sha256){this.name=name;this.size=size;this.sha256=sha256;}
    public String toString(){return name+" • "+size+" bytes • SHA-256 "+sha256;}
}
