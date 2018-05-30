package courseworkthreefxml;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class SalesDetails {
    private final String _default = "NO DATA";
    private StringProperty Year = new SimpleStringProperty();
    private StringProperty QTR = new SimpleStringProperty();
    private StringProperty Vehicle = new SimpleStringProperty();
    private StringProperty Region = new SimpleStringProperty();
    private IntegerProperty Quantity = new SimpleIntegerProperty();

    public SalesDetails(String year, String qtr, String vehicle, String region, Integer quantity) {
        this.Year.set(year);
        this.QTR.set(qtr);
        this.Vehicle.set(vehicle);
        this.Region.set(region);
        this.Quantity.set(quantity);
    }

    public static Callback<SalesDetails, Observable[]> extractors() {
        return (SalesDetails o) -> new Observable[] { o.quarterProperty(), o.quantityProperty(), o.regionProperty(), o.vehicleProperty(), o.yearProperty()};
    }

    public void setYear(String year) {
        if ((this.Year != null) || (year == null ? _default != null : !year.equals(_default))) {
            yearProperty().set(year);
        }
    }

    public String getYear() {
        return (Year != null)? Year.get() : _default;
    }

    public StringProperty yearProperty() {
        if (Year == null) {
            Year = new SimpleStringProperty(this, "Year", _default);
        }
        return Year;
    }

    public void setQuarter(String qtr) {
        if ((this.QTR != null) || (qtr == null ? _default != null : !qtr.equals(_default))) {
            quarterProperty().set(qtr);
        }
    }

    public String getQuarter() {
        return QTR != null ? QTR.get() : _default;
    }

    public StringProperty quarterProperty() {
        if (QTR == null) {
            QTR = new SimpleStringProperty(this, "QTR", _default);
        }
        return QTR;
    }

    public void setVehicle(String vehicle) {
        if ((this.Vehicle != null) || !(vehicle.equals(_default))) {
            vehicleProperty().set(vehicle);
        }
    }

    public String getVehicle() {
        return Vehicle != null ? Vehicle.get() : _default;
    }

    public StringProperty vehicleProperty() {
        if (Vehicle == null) {
            Vehicle = new SimpleStringProperty(this, "Vehicle", _default);
        }
        return Vehicle;
    }

    public void setRegion(String region) {
        if ((this.Region != null) || !(region.equals(_default))) {
            regionProperty().set(region);
        }
    }

    public String getRegion() {
        return Region != null ? Region.get() : _default;
    }

    public StringProperty regionProperty() {
        if (Region == null) {
            Region = new SimpleStringProperty(this, "Region", _default);
        }
        return Region;
    }

    public void setQuantity(Integer quantity) {
        if ((this.Quantity != null) || !(quantity.equals(0))) {
            quantityProperty().set(quantity);
        }
    }

    public Integer getQuantity() {
        return Quantity != null ? Quantity.get() : 0;
    }

    public IntegerProperty quantityProperty() {
        if (Quantity == null) {
            Quantity = new SimpleIntegerProperty(this, "Quantity", 0);
        }
        return Quantity;
    }

}
