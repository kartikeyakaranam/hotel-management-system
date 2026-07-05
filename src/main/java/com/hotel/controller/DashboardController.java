package com.hotel.controller;

import com.hotel.dao.DashboardDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ── Stat cards ────────────────────────────────────────────────────────
    @FXML private Label lblTotalRooms;
    @FXML private Label lblOccupied;
    @FXML private Label lblAvailable;
    @FXML private Label lblActiveBookings;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblRevenue;

    // ── Occupancy bar ─────────────────────────────────────────────────────
    @FXML private ProgressBar pbOccupancy;
    @FXML private Label       lblOccupancyPct;

    // ── Per-type breakdown ────────────────────────────────────────────────
    @FXML private VBox vboxBreakdown;

    private final DashboardDAO dao = new DashboardDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refresh();
        // Auto-refresh every 30 seconds
        Timeline tl = new Timeline(new KeyFrame(
            Duration.seconds(30), e -> refresh()
        ));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    @FXML
    public void refresh() {
        int total     = dao.getTotalRooms();
        int occupied  = dao.getOccupiedRooms();
        int available = dao.getAvailableRooms();
        int active    = dao.getActiveBookings();
        int customers = dao.getTotalCustomers();
        double revenue = dao.getTotalRevenue();

        lblTotalRooms.setText(String.valueOf(total));
        lblOccupied.setText(String.valueOf(occupied));
        lblAvailable.setText(String.valueOf(available));
        lblActiveBookings.setText(String.valueOf(active));
        lblTotalCustomers.setText(String.valueOf(customers));
        lblRevenue.setText(String.format("₹ %,.2f", revenue));

        double pct = (total > 0) ? (double) occupied / total : 0.0;
        pbOccupancy.setProgress(pct);
        lblOccupancyPct.setText(String.format("%.0f%% Occupied  (%d / %d rooms)",
            pct * 100, occupied, total));

        buildBreakdown();
    }

    private void buildBreakdown() {
        vboxBreakdown.getChildren().clear();

        Map<String, Integer> avail    = dao.getAvailableByType();
        Map<String, Integer> occupied = dao.getOccupiedByType();

        // Merge all room types
        java.util.Set<String> types = new java.util.LinkedHashSet<>();
        types.addAll(avail.keySet());
        types.addAll(occupied.keySet());

        for (String type : types) {
            int av = avail.getOrDefault(type, 0);
            int oc = occupied.getOrDefault(type, 0);
            int total = av + oc;

            // Row label
            Label lbl = new Label(type);
            lbl.getStyleClass().add("dash-type-label");

            // Progress bar for this type
            ProgressBar bar = new ProgressBar(total > 0 ? (double) oc / total : 0);
            bar.setPrefWidth(200);
            bar.getStyleClass().add(oc == total && total > 0 ? "bar-full" : "bar-partial");

            Label stat = new Label(oc + " occ  /  " + av + " free");
            stat.getStyleClass().add("dash-type-stat");

            HBox row = new HBox(12, lbl, bar, stat);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.getStyleClass().add("dash-type-row");

            vboxBreakdown.getChildren().add(row);
        }

        if (types.isEmpty()) {
            Label empty = new Label("No rooms in database yet.");
            empty.getStyleClass().add("empty-label");
            vboxBreakdown.getChildren().add(empty);
        }
    }
}
