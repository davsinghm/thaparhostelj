package com.temp.jhostelapp.ui;

import com.temp.jhostelapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DSM_ on 1/31/16.
 */
public class Complaint {

    private String category;
    private String complaintID;
    private String complaint;
    private String status;
    private long startTimestamp;
    private long currentTimestamp;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void setCurrentTimestamp(long currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public Complaint fromJSON(JSONObject jsonObject) {
        try {

            setCategory(jsonObject.getString("category"));
            setComplaintID(jsonObject.getString("complaintId"));
            setComplaint(jsonObject.getString("complaint"));
            setStatus(jsonObject.getString("status"));
            setStartTimestamp(Utils.parseLong(jsonObject.getString("startTimestamp"), 0));
            setCurrentTimestamp(Utils.parseLong(jsonObject.getString("currentTimestamp"), 0));

            return this;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("category", getCategory());
            jsonObject.put("complaintId", getComplaintID());
            jsonObject.put("complaint", getComplaint());
            jsonObject.put("status", getStatus());
            jsonObject.put("startTimestamp", String.valueOf(getStartTimestamp()));
            jsonObject.put("currentTimestamp", String.valueOf(getCurrentTimestamp()));

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Complaint complaint = (Complaint) o;

        return !(complaintID != null ? !complaintID.equals(complaint.complaintID) : complaint.complaintID != null);

    }

}
