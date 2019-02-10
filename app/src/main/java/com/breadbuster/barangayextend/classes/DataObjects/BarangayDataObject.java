package com.breadbuster.barangayextend.classes.DataObjects;

public class BarangayDataObject {
    String barangayID,barangayName,barangayDescription,barangayLogo;

    public BarangayDataObject(String barangayID, String barangayName, String barangayDescription, String barangayLogo) {
        this.barangayID = barangayID;
        this.barangayName = barangayName;
        this.barangayDescription = barangayDescription;
        this.barangayLogo = barangayLogo;
    }

    public String getBarangayID() {
        return barangayID;
    }

    public String getBarangayName() {
        return barangayName;
    }

    public String getBarangayDescription() {
        return barangayDescription;
    }

    public String getBarangayLogo() {
        return barangayLogo;
    }
}
