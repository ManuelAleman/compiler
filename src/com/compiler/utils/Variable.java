package com.compiler.utils;

public class Variable {
    private String type;
    private String name;
    private String value;
    private byte bits;

    public Variable(String type, String name, String value, byte bits) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.bits = bits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte getBits() {
        return bits;
    }

    public void setBits(byte bits) {
        this.bits = bits;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", bits=" + bits +
                '}';
    }
}
