package courseworkthreefxml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class CourseworkThreeController implements Initializable {
    private static String json;
    private static LinkedList<Sales> salesData;
    private SalesService service;

    private CheckBox[] yearCheckBoxes;
    private CheckBox[] quarterCheckBoxes;
    private CheckBox[] regionCheckBoxes;
    private RadioButton[] radioButton1;
    private RadioButton[] yearlySalesRadioButtons;
    private ProgressIndicator ProgressIndicator1;

    @FXML
    private AnchorPane anchorPane1;

    @FXML
    private HBox HBox2, HBox3, HBox4, hBoxQuarters, hBoxRegions;


    @FXML
    private BarChart<?, ?> BarChart1;

    @FXML
    private PieChart PieChart1;

    @FXML
    private TableView<SalesDetails> TableView1;

    @FXML
    private StackedBarChart stackedBarChart;

    @FXML
    private TableColumn tcYear, tcQuarter, tcRegion, tcVehicle, tcQuantity;

    final ToggleGroup group1 = new ToggleGroup();
    final ToggleGroup group2 = new ToggleGroup();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = new SalesService();
        // Removed URL
        service.setUrl("xxxxxxxxxxxxxxxxxxxx");
        service.setOnSucceeded((WorkerStateEvent e) -> {
            json = e.getSource().getValue().toString();
            salesData = (new Gson()).fromJson(json, new TypeToken<LinkedList<Sales>>() {}.getType());
            List<String> years = salesData.stream().map(o -> o.getYear()).distinct().collect(Collectors.toList());
            List<String> quarters = salesData.stream().map(o -> o.getQTR()).distinct().collect(Collectors.toList());
            List<String> regions = salesData.stream().map(o -> o.getRegion()).distinct().collect(Collectors.toList());

            // Init controls
            yearCheckBoxes = new CheckBox[years.size()];
            quarterCheckBoxes = new CheckBox[quarters.size()];
            regionCheckBoxes = new CheckBox[regions.size()];
            radioButton1 = new RadioButton[years.size()];

            yearlySalesRadioButtons = new RadioButton[2];
            yearlySalesRadioButtons[0] = new RadioButton("Vehicle");
            yearlySalesRadioButtons[1] = new RadioButton("Region");
            yearlySalesRadioButtons[0].setSelected(true);

            // Listeners for yearlySalesRadioButtons and add to HBox3
            for (int i = 0; i < yearlySalesRadioButtons.length; i++) {
                yearlySalesRadioButtons[i].setToggleGroup(group2);
                yearlySalesRadioButtons[i].setOnAction((ActionEvent a) -> {
                    constructTotalSales();
                });
                HBox3.getChildren().add(yearlySalesRadioButtons[i]);
            }

            /*
            VEHICLE SALES BY YEAR
            Dynamically add checkboxes based on distinct year values from data.
            Set a convenience method event handler for checkboxes to update chart after user interaction
            */
            for (int i = 0; i < years.size(); i++) {
                yearCheckBoxes[i] = new CheckBox(years.get(i));
                yearCheckBoxes[i].setSelected(true);
                yearCheckBoxes[i].setOnAction((ActionEvent a) -> {
                    constructTotalSales();
                });
                HBox4.getChildren().add(yearCheckBoxes[i]);
            }

            /*
            QUARTERLY SALES FOR EACH YEAR
            Dynamically add checkboxes based on distinct year values from data.
            Set a convenience method event handler for checkboxes to update chart after user interaction
            */
            for (int i = 0; i < years.size(); i++) {
                radioButton1[i] = new RadioButton(years.get(i));
                radioButton1[i].setToggleGroup(group1);
                radioButton1[i].setOnAction((ActionEvent a) -> {
                    constructQuarterlySalesPieChart();
                });
                radioButton1[0].setSelected(true);
                HBox2.getChildren().add(radioButton1[i]);
            }

            /*
            Quarter checkboxes for StackedBarChart
            */
            for (int i = 0; i < quarters.size(); i++) {
                quarterCheckBoxes[i] = new CheckBox(quarters.get(i));
                quarterCheckBoxes[i].setSelected(true);
                quarterCheckBoxes[i].setOnAction((ActionEvent a) -> {
                    constructStackedBarChart();
                });
                hBoxQuarters.getChildren().add(quarterCheckBoxes[i]);
            }

            /*
            Region checkboxes for StackedBarChart
            */
            for (int i = 0; i < regions.size(); i++) {
                regionCheckBoxes[i] = new CheckBox(regions.get(i));
                regionCheckBoxes[i].setSelected(true);
                regionCheckBoxes[i].setOnAction((ActionEvent a) -> {
                    constructStackedBarChart();
                });
                hBoxRegions.getChildren().add(regionCheckBoxes[i]);
            }
            constructTotalSales();
            constructQuarterlySalesPieChart();
            constructStackedBarChart();
            constructIndividualSalesRecordsTable();
        });
        service.start();

        // Loading gif, aligned center to root anchor pane
        ProgressIndicator1 = new ProgressIndicator();
        ProgressIndicator1.translateXProperty().bind(anchorPane1.widthProperty().subtract(ProgressIndicator1.widthProperty()).divide(2));
        ProgressIndicator1.translateYProperty().bind(anchorPane1.heightProperty().subtract(ProgressIndicator1.heightProperty()).divide(2));
        ProgressIndicator1.visibleProperty().bind(service.runningProperty());
        anchorPane1.getChildren().add(ProgressIndicator1);
    }

    private void constructTotalSales() {
        BarChart1.getData().clear();

        if (yearlySalesRadioButtons[0].isSelected()) {
            for (CheckBox checkBox : yearCheckBoxes) {
                if (checkBox.isSelected()) {
                    XYChart.Series series = new XYChart.Series();
                    series.setName(checkBox.getText());

                    Map<String,Integer> vehicleTotals = salesData.stream()
                        .filter(o -> o.getYear().equals(checkBox.getText()))
                        .collect(Collectors.groupingBy(Sales::getVehicle, Collectors.reducing(0, Sales::getQuantity, Integer::sum)));

                    vehicleTotals.entrySet().forEach((vehicleTotal) -> {
                        series.getData().add(new XYChart.Data(vehicleTotal.getKey(), vehicleTotal.getValue()));
                    });
                    BarChart1.getData().add(series);
                }
            }
        } else {
            for (CheckBox checkBox : yearCheckBoxes) {
                if (checkBox.isSelected()) {
                    XYChart.Series series = new XYChart.Series();
                    series.setName(checkBox.getText());

                    Map<String,Integer> regionTotals = salesData.stream()
                        .filter(o -> o.getYear().equals(checkBox.getText()))
                        .collect(Collectors.groupingBy(Sales::getRegion, Collectors.reducing(0, Sales::getQuantity, Integer::sum)));

                    regionTotals.entrySet().forEach((regionTotal) -> {
                        series.getData().add(new XYChart.Data(regionTotal.getKey(), regionTotal.getValue()));
                    });

                    BarChart1.getData().add(series);
                }
            }
        }
        // Dynamically change chart title and X axis label to match selected filter
        for (RadioButton rb: yearlySalesRadioButtons) {
            if (rb.isSelected()) {
                BarChart1.setTitle("Total Annual Sales by " + rb.getText());
                BarChart1.getXAxis().setLabel(rb.getText());
            }
        }
    }

    private void constructQuarterlySalesPieChart() {
        PieChart1.getData().clear();
        int[] quarterTotals = new int[4];

        for (RadioButton radioButton : radioButton1) {
            if (radioButton.isSelected()) {
                PieChart1.setTitle("Quarterly Sales for " + radioButton.getText());
                salesData.stream()
                    .filter((sale) -> (sale.getYear().equals(radioButton.getText())))
                    .forEach((Sales sale) -> {
                    // Aggregate quarterly sales amounts for selected year
                    int quarter = Integer.valueOf(sale.getQTR());
                    quarterTotals[quarter - 1] += sale.getQuantity();
                });
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Q1", quarterTotals[0]),
            new PieChart.Data("Q2", quarterTotals[1]),
            new PieChart.Data("Q3", quarterTotals[2]),
            new PieChart.Data("Q4", quarterTotals[3])
        );

        // Display total amounts for each quarter
        pieChartData.forEach(data ->
            data.nameProperty().bind(
                Bindings.concat(
                    data.getName(), " - ", data.pieValueProperty().intValue()
                )
            )
        );
        PieChart1.setData(pieChartData);
    }

    /*
    Bottom left GridPane cell
    */
    private void constructStackedBarChart() {
        stackedBarChart.getData().clear();
        List<String> years = salesData.stream().map(o -> o.getYear()).distinct().collect(Collectors.toList());

        years.forEach((year) -> {
            for (CheckBox quarter: quarterCheckBoxes) {
                XYChart.Series chartSeries = new XYChart.Series();
                if (quarter.isSelected()) {
                    for (CheckBox region : regionCheckBoxes) {
                        if (region.isSelected()) {
                            Map<String, Integer> vehicleTotalsByYearQuarterAndRegion = salesData.stream()
                                .filter(o -> o.getYear().equals(year))
                                .filter(o -> o.getQTR().equals(quarter.getText()))
                                .filter(o -> o.getRegion().equals(region.getText()))
                                .collect(Collectors.groupingBy(Sales::getVehicle, Collectors.reducing(0, Sales::getQuantity, Integer::sum)));

                            vehicleTotalsByYearQuarterAndRegion.entrySet().forEach((vehicleGrouping) -> {
                                chartSeries.setName("Q" + quarter.getText() + "-" + year);
                                chartSeries.getData().add(new XYChart.Data(vehicleGrouping.getKey() + " " + year, vehicleGrouping.getValue()));
                            });
                        }
                    }
                    stackedBarChart.getData().add(chartSeries);
                }
            }
        });
    }

    /*
    Populate TableView with all sales data
    */
    private void constructIndividualSalesRecordsTable() {
        ObservableList<SalesDetails> sdList = FXCollections.observableArrayList(SalesDetails.extractors());
        salesData.forEach((s) -> {
            sdList.add(new SalesDetails(s.getYear(), s.getQTR(), s.getVehicle(), s.getRegion(), s.getQuantity()));
        });

        TableView1.setItems(sdList);
        tcYear.setCellValueFactory(new PropertyValueFactory("Year"));
        tcQuarter.setCellValueFactory(new PropertyValueFactory("Quarter"));
        tcRegion.setCellValueFactory(new PropertyValueFactory("Region"));
        tcVehicle.setCellValueFactory(new PropertyValueFactory("Vehicle"));
        tcQuantity.setCellValueFactory(new PropertyValueFactory("Quantity"));
    }

    /*
    Retrieve sales data in background thread
    */
    private static class SalesService extends Service<String> {
        private final StringProperty url = new SimpleStringProperty();

        public final void setUrl(String val) {
            this.url.set(val);
        }

        public final String getUrl() {
            return url.get();
        }

        public final StringProperty urlProperty() {
            return url;
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                private URL url;
                private HttpURLConnection connect;
                private String json = "";

                @Override
                protected String call() throws MalformedURLException, IOException {
                    try {
                        url = new URL(getUrl());
                        connect = (HttpURLConnection)url.openConnection();
                        connect.setRequestMethod("GET");
                        connect.setRequestProperty("Accept", "application/json");
                        connect.setRequestProperty("Content-Type", "application/json");

                        json = (new BufferedReader(new InputStreamReader(connect.getInputStream()))).readLine();
                    } finally {
                        if (connect != null) {
                            connect.disconnect();
                        }
                    }
                    return json;
                }
            };
        }
    }

}
