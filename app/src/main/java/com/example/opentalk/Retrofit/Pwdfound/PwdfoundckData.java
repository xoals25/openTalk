package com.example.opentalk.Retrofit.Pwdfound;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PwdfoundckData {

    @Expose
    @SerializedName("result") private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
