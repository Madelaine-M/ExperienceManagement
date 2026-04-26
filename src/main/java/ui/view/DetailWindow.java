package ui.view;

public class DetailWindow {
    private void openCustomerDetailWindow(Customer c) {

        Stage stage = new Stage();
        stage.setTitle("Customer Detail — " + c.getName());

        // ========== TOP: Photo + Basic Info ==========
        ImageView photo = new ImageView(
                new Image(
                        getClass().getResourceAsStream("/avatar-placeholder.png")
                )
        );
        photo.setFitWidth(80);
        photo.setFitHeight(80);
        photo.setPreserveRatio(true);

        Label name = new Label(c.getName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Label meta = new Label(
                "Type: " + c.getCustomerType()
                        + " | Package: " + c.getPackageType()
                        + " | Risk: " + c.getRiskPriority()
        );

        VBox infoBox = new VBox(6, name, meta);
        HBox header = new HBox(15, photo, infoBox);
        header.setPadding(new Insets(15));

        // ========== CENTER: Current Issue ==========
        VBox issueCard = new VBox(6);
        issueCard.setPadding(new Insets(12));
        issueCard.setStyle("""
        -fx-background-color: #ffe6e6;
        -fx-background-radius: 8;
    """);

        issueCard.getChildren().addAll(
                new Label("Current Issue"),
                new Label(c.getCurrentIssue().summary()),
                new Label(c.getCurrentIssue().explanation()),
                new Label("Consequence: " + c.getCurrentIssue().consequence())
        );

        // ========== LEFT-BOTTOM: Charts ==========
        TabPane charts = new TabPane();

        charts.getTabs().addAll(
                new Tab("Helped", createImpactChart(80, 65)),
                new Tab("Not Helped", createImpactChart(40, 20))
        );

        charts.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // ========== RIGHT-BOTTOM: Recommendation ==========
        VBox recommendationBox = new VBox(10);
        recommendationBox.setPadding(new Insets(12));
        recommendationBox.setStyle("""
        -fx-background-color: #f4f7fb;
        -fx-background-radius: 8;
    """);

        recommendationBox.getChildren().addAll(
                new Label("System Recommendation"),
                new Label("Offer £200 gift card"),
                new Label("Predicted rebooking: +22%"),
                new Label("Predicted recommendation: +18%"),
                new Label("Experience score: +0.6")
        );

        // ========== Layout ==========
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(issueCard);
        root.setLeft(charts);
        root.setRight(recommendationBox);

        BorderPane.setMargin(charts, new Insets(10));
        BorderPane.setMargin(recommendationBox, new Insets(10));

        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }
    private LineChart<String, Number> createImpactChart(int helped, int notHelped) {

        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();

        LineChart<String, Number> chart = new LineChart<>(x, y);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Before", notHelped));
        series.getData().add(new XYChart.Data<>("After", helped));

        chart.getData().add(series);
        return chart;
    }
}
