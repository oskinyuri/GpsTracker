package com.example.gpstracker.pojo.massegeRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Awesome Pojo Generator
 */
public class Predicate {
    @SerializedName("operands")
    @Expose
    private List<Operands> operands;
    @SerializedName("strict")
    @Expose
    private Boolean strict;

    public Predicate(List<Operands> operands) {
        this.operands = operands;
        strict = true;
    }

    public void setOperands(List<Operands> operands) {
        this.operands = operands;
    }

    public List<Operands> getOperands() {
        return operands;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public Boolean getStrict() {
        return strict;
    }
}