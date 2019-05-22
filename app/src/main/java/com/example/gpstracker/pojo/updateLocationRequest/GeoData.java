package com.example.gpstracker.pojo.updateLocationRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class GeoData {
    @SerializedName("entityName")
    @Expose
    private String entityName;
    @SerializedName("schemaName")
    @Expose
    private String schemaName;
    @SerializedName("fields")
    @Expose
    private Coordinates fields;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("key")
    @Expose
    private String key;

    public GeoData(Coordinates fields, String carNumber) {
        this.fields = fields;
        value = carNumber;

        entityName = "ts_map";
        schemaName = "monitoring";
        key = "ts_key";
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setFields(Coordinates fields) {
        this.fields = fields;
    }

    public Coordinates getFields() {
        return fields;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}