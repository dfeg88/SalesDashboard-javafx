package courseworkthreefxml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Sales {

    final private Integer Quantity;
    final private String Region, Vehicle, Year, QTR;

    public Sales(String qtr, Integer quantity, String region, String vehicle, String year) {
        this.QTR = qtr;
        this.Quantity = quantity;
        this.Region = region;
        this.Vehicle = vehicle;
        this.Year = year;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s", ("Year:" + Year + " "), ("Quarter:" + QTR + " "), ("Region:" + Region + " "), ("Vehicle:" + Vehicle + " "), ("Quantity:" + Quantity + " "));
    }

    public String getQTR() {
        return QTR;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public String getYear() {
        return Year;
    }

    public String getRegion() {
        return Region;
    }

    public String getVehicle() {
        return Vehicle;
    }

}
