package com.example.gpstracker.pojo.massegeRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class MessageRequest {
    @SerializedName("predicate")
    @Expose
    private Predicate predicate;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("entityName")
    @Expose
    private String entityName;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("schemaName")
    @Expose
    private String schemaName;

    public MessageRequest(Predicate predicate) {
        this.predicate = predicate;
        limit = 1;
        offset = 0;
        entityName = "ts_map";
        schemaName = "monitoring";
    }

    @SerializedName("fields")
    @Expose


    private Fields fields;

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public Fields getFields() {
        return fields;
    }
}