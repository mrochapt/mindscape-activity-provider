package com.mindscape.activityprovider.dto;

public class AnalyticItem {

    private String name;   // ex: "TimeSpent"
    private String type;   // ex: "integer", "text/plain"
    private Object value;  // ex: 120, "texto..."

    public AnalyticItem() {}

    public AnalyticItem(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public Object getValue() { return value; }

    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setValue(Object value) { this.value = value; }
}
