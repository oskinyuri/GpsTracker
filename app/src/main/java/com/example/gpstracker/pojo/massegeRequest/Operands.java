package com.example.gpstracker.pojo.massegeRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class Operands {
    @SerializedName("levelup")
    @Expose
    private Boolean levelup;
    @SerializedName("operand")
    @Expose
    private Operand operand;

    public Operands(Operand operand) {
        this.operand = operand;
        levelup = false;
    }

    public void setLevelup(Boolean levelup) {
        this.levelup = levelup;
    }

    public Boolean getLevelup() {
        return levelup;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    public Operand getOperand() {
        return operand;
    }
}