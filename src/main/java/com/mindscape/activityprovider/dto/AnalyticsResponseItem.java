package com.mindscape.activityprovider.dto;

import java.util.List;

public class AnalyticsResponseItem {

    // Atenção ao nome do campo, seguindo o documento de Activity Providers
    private String inveniraStdID;
    private List<AnalyticItem> quantAnalytics;
    private List<AnalyticItem> qualAnalytics;

    public AnalyticsResponseItem() {
    }

    public AnalyticsResponseItem(String inveniraStdID,
                                 List<AnalyticItem> quantAnalytics,
                                 List<AnalyticItem> qualAnalytics) {
        this.inveniraStdID = inveniraStdID;
        this.quantAnalytics = quantAnalytics;
        this.qualAnalytics = qualAnalytics;
    }

    public String getInveniraStdID() {
        return inveniraStdID;
    }

    public void setInveniraStdID(String inveniraStdID) {
        this.inveniraStdID = inveniraStdID;
    }

    public List<AnalyticItem> getQuantAnalytics() {
        return quantAnalytics;
    }

    public void setQuantAnalytics(List<AnalyticItem> quantAnalytics) {
        this.quantAnalytics = quantAnalytics;
    }

    public List<AnalyticItem> getQualAnalytics() {
        return qualAnalytics;
    }

    public void setQualAnalytics(List<AnalyticItem> qualAnalytics) {
        this.qualAnalytics = qualAnalytics;
    }
}
