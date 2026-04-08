package gui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;

public class MainGUI extends Application {

    // ══════════════════════════════════════════════════════════════════
    // STAGE REFERENCE — shared between pages
    // ══════════════════════════════════════════════════════════════════
    static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("ResumeGenX");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
        stage.setWidth(1220);
        stage.setHeight(780);
        showPage1();
        stage.show();
    }

    static void showPage1() {
        Scene scene = new Scene(new Page1().getRoot(),
                primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    static void showPage2(String fileName) {
        Scene scene = new Scene(new Page2(fileName).getRoot(),
                primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ══════════════════════════════════════════════════════════════════
    // THEME — centralised palette & style helpers
    // ══════════════════════════════════════════════════════════════════
    static class Theme {
        // Background & surfaces
        static final String BG = "#0B0C10";
        static final String CARD = "#16191F";
        static final String CARD2 = "#1C1F28";
        static final String BORDER = "#2A2E3D";
        static final String BORDER2 = "#353A50";
        // Core colours
        static final String RED = "#F13C20";
        static final String RED_DARK = "#B52D18";
        static final String COBALT = "#4056A1";
        static final String COBALT_H = "#4E67BF";
        static final String TEAL = "#45A29E";
        static final String CRIMSON = "#9A1750";
        // Text
        static final String PARCHMENT = "#EFE2BA";
        static final String LAVENDER = "#C5CBE3";
        static final String MUTED = "#3D4258";
        static final String MUTED2 = "#6B7390";
        static final String SILVER_LO = "#4A4F62";
        static final String SILVER_MID = "#7A8299";
        static final String SILVER_HI = "#A8B0C8";

        static String nav() {
            return "-fx-background-color: linear-gradient(to bottom, #0f1116, #0b0c10);"
                    + "-fx-border-color: transparent transparent " + BORDER2 + " transparent;"
                    + "-fx-border-width: 0 0 1 0;"
                    + "-fx-padding: 14 52 14 52;"
                    + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.6),10,0,0,3);";
        }

        static String cardBase() {
            return "-fx-background-color: linear-gradient(to bottom right,#1e2230,#181b24);"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + BORDER + ";"
                    + "-fx-border-radius: 12; -fx-border-width: 1;"
                    + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.4),12,0,0,3);";
        }

        static String cardHover() {
            return "-fx-background-color: linear-gradient(to bottom right,#1e2230,#181b24);"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: rgba(241,60,32,0.5);"
                    + "-fx-border-radius: 12; -fx-border-width: 1;"
                    + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.55),20,0,0,6);";
        }

        static String cardSelected() {
            return "-fx-background-color: linear-gradient(to bottom right,#1e2230,#181b24);"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + RED + ";"
                    + "-fx-border-radius: 12; -fx-border-width: 2;"
                    + "-fx-effect: dropshadow(gaussian,rgba(241,60,32,0.35),22,0,0,0);";
        }

        static String guidePanel() {
            return "-fx-background-color: " + CARD2 + ";"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: " + COBALT + " " + BORDER2 + " " + BORDER2 + " " + COBALT + ";"
                    + "-fx-border-width: 0 1 1 3;"
                    + "-fx-border-radius: 12;"
                    + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.5),24,0,0,6);";
        }

        static String btnPrimary() {
            return "-fx-background-color: linear-gradient(to bottom right," + RED + "," + RED_DARK + ");"
                    + "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"
                    + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 28 12 28;"
                    + "-fx-effect: dropshadow(gaussian,rgba(241,60,32,0.45),14,0,0,4);";
        }

        static String btnPrimaryHover() {
            return "-fx-background-color: linear-gradient(to bottom right,#ff4f32," + RED + ");"
                    + "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"
                    + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 28 12 28;"
                    + "-fx-effect: dropshadow(gaussian,rgba(241,60,32,0.6),20,0,0,6);";
        }

        static String btnSecondary() {
            return "-fx-background-color: linear-gradient(to bottom right," + COBALT + ",#334d8a);"
                    + "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;"
                    + "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
                    + "-fx-effect: dropshadow(gaussian,rgba(64,86,161,0.4),12,0,0,3);";
        }

        static String btnGhost() {
            return "-fx-background-color: rgba(255,255,255,0.03);"
                    + "-fx-text-fill: " + SILVER_MID + "; -fx-font-size: 13px;"
                    + "-fx-background-radius: 9;"
                    + "-fx-border-color: " + BORDER2 + "; -fx-border-radius: 9; -fx-border-width: 1;"
                    + "-fx-cursor: hand; -fx-padding: 10 22 10 22;";
        }

        static String eyebrowText() {
            return "-fx-text-fill: " + SILVER_MID + "; -fx-font-size: 10px; -fx-font-family: 'Courier New';";
        }

        static String sectionLbl() {
            return "-fx-text-fill: " + COBALT_H + "; -fx-font-size: 10px;"
                    + "-fx-font-family: 'Courier New'; -fx-font-weight: bold;";
        }

        static String muted() {
            return "-fx-text-fill:" + MUTED2 + ";-fx-font-size:13px;";
        }

        static String bright() {
            return "-fx-text-fill:" + PARCHMENT + ";-fx-font-size:13px;";
        }

        // Convenience
        static HBox eyebrow(String text) {
            Rectangle line = new Rectangle(24, 2);
            line.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web(RED)), new Stop(1, Color.web(COBALT))));
            Label lbl = new Label(text.toUpperCase());
            lbl.setStyle(eyebrowText());
            HBox row = new HBox(10, line, lbl);
            row.setAlignment(Pos.CENTER_LEFT);
            return row;
        }

        static HBox logo() {
            StackPane icon = new StackPane();
            icon.setPrefSize(32, 32);
            icon.setStyle("-fx-background-color:linear-gradient(to bottom right," + RED + "," + COBALT + ");"
                    + "-fx-background-radius:8;"
                    + "-fx-effect:dropshadow(gaussian,rgba(241,60,32,0.45),10,0,0,2);");
            Text ri = new Text("R");
            ri.setFill(Color.WHITE);
            ri.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 14));
            icon.getChildren().add(ri);

            Text t1 = new Text("Resume");
            t1.setFill(Color.web(SILVER_HI));
            t1.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
            Text t2 = new Text("GenX");
            t2.setFill(Color.web(RED));
            t2.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 20));
            TextFlow tf = new TextFlow(t1, t2);

            HBox box = new HBox(10, icon, tf);
            box.setAlignment(Pos.CENTER_LEFT);
            return box;
        }

        static Label pill(String text, boolean active, boolean done) {
            Label p = new Label(text);
            p.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
            p.setPadding(new Insets(5, 14, 5, 14));
            if (active) {
                p.setStyle("-fx-background-color:linear-gradient(to bottom right," + RED + "," + RED_DARK + ");"
                        + "-fx-background-radius:6;-fx-text-fill:white;"
                        + "-fx-effect:dropshadow(gaussian,rgba(241,60,32,0.45),10,0,0,2);");
            } else if (done) {
                p.setStyle("-fx-background-color:rgba(64,86,161,0.12);-fx-background-radius:6;"
                        + "-fx-text-fill:" + COBALT_H + ";"
                        + "-fx-border-color:rgba(64,86,161,0.4);-fx-border-radius:6;-fx-border-width:1;");
            } else {
                p.setStyle("-fx-background-color:rgba(255,255,255,0.02);-fx-background-radius:6;"
                        + "-fx-text-fill:" + MUTED2 + ";"
                        + "-fx-border-color:" + BORDER2 + ";-fx-border-radius:6;-fx-border-width:1;");
            }
            return p;
        }

        static Label arrow() {
            Label a = new Label("›");
            a.setStyle("-fx-text-fill:" + BORDER2 + ";-fx-font-size:14;");
            return a;
        }

        static VBox infoBox(String text) {
            Label lbl = new Label(text);
            lbl.setStyle("-fx-text-fill:" + LAVENDER + ";-fx-font-size:12px;");
            lbl.setWrapText(true);
            VBox box = new VBox(lbl);
            box.setPadding(new Insets(10, 12, 10, 12));
            box.setStyle("-fx-background-color:rgba(64,86,161,0.08);-fx-background-radius:7;"
                    + "-fx-border-color:rgba(64,86,161,0.25) rgba(64,86,161,0.25) rgba(64,86,161,0.25) " + COBALT + ";"
                    + "-fx-border-width:1 1 1 3;-fx-border-radius:7;");
            return box;
        }

        static VBox warnBox(String text) {
            Label lbl = new Label(text);
            lbl.setStyle("-fx-text-fill:#f09585;-fx-font-size:12px;");
            lbl.setWrapText(true);
            VBox box = new VBox(lbl);
            box.setPadding(new Insets(10, 12, 10, 12));
            box.setStyle("-fx-background-color:rgba(241,60,32,0.07);-fx-background-radius:7;"
                    + "-fx-border-color:rgba(241,60,32,0.22) rgba(241,60,32,0.22) rgba(241,60,32,0.22) " + RED + ";"
                    + "-fx-border-width:1 1 1 3;-fx-border-radius:7;");
            return box;
        }

        static VBox codeBlock(String code) {
            Label lbl = new Label(code);
            lbl.setStyle("-fx-text-fill:" + SILVER_MID + ";-fx-font-family:'Courier New';-fx-font-size:11px;");
            VBox box = new VBox(lbl);
            box.setPadding(new Insets(12, 14, 12, 14));
            box.setStyle("-fx-background-color:rgba(0,0,0,0.45);-fx-background-radius:8;"
                    + "-fx-border-color:" + BORDER2 + " " + BORDER2 + " " + BORDER2 + " " + COBALT + ";"
                    + "-fx-border-width:1 1 1 3;-fx-border-radius:8;");
            return box;
        }

        static HBox stepRow(String num, String text) {
            StackPane badge = new StackPane();
            badge.setPrefSize(22, 22);
            badge.setMinSize(22, 22);
            badge.setStyle("-fx-background-color:rgba(64,86,161,0.15);-fx-background-radius:5;"
                    + "-fx-border-color:rgba(64,86,161,0.35);-fx-border-radius:5;-fx-border-width:1;");
            Label nl = new Label(num);
            nl.setStyle("-fx-text-fill:" + COBALT_H
                    + ";-fx-font-family:'Courier New';-fx-font-size:10;-fx-font-weight:bold;");
            badge.getChildren().add(nl);
            Label tl = new Label(text);
            tl.setStyle("-fx-text-fill:" + MUTED2 + ";-fx-font-size:12px;");
            tl.setWrapText(true);
            HBox.setHgrow(tl, Priority.ALWAYS);
            HBox row = new HBox(10, badge, tl);
            row.setAlignment(Pos.TOP_LEFT);
            return row;
        }

        static VBox guideSection(String title, Node... children) {
            Label h = new Label(title.toUpperCase());
            h.setStyle(sectionLbl());
            h.setPadding(new Insets(0, 0, 8, 0));
            VBox sec = new VBox(8);
            sec.getChildren().add(h);
            sec.getChildren().addAll(children);
            return sec;
        }

        static void fadeIn(Node node) {
            FadeTransition ft = new FadeTransition(Duration.millis(380), node);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            TranslateTransition tt = new TranslateTransition(Duration.millis(380), node);
            tt.setFromY(10);
            tt.setToY(0);
            tt.play();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // PAGE 1 — Upload Screen
    // ══════════════════════════════════════════════════════════════════
    static class Page1 {

        private final BorderPane root;
        private File selectedFile;
        private Label fileNameLabel;
        private HBox fileRow;
        private Button submitBtn;
        private ProgressBar progressBar;

        Page1() {
            root = new BorderPane();
            root.setStyle("-fx-background-color:" + Theme.BG + ";");
            root.setTop(buildNav());
            root.setCenter(buildBody());
        }

        BorderPane getRoot() {
            return root;
        }

        // ── NAV ──────────────────────────────────────────────────────
        private HBox buildNav() {
            HBox nav = new HBox();
            nav.setStyle(Theme.nav());
            nav.setAlignment(Pos.CENTER_LEFT);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            HBox pills = new HBox(6,
                    Theme.pill("01  Upload", true, false), Theme.arrow(),
                    Theme.pill("02  Template", false, false), Theme.arrow(),
                    Theme.pill("03  Generate", false, false));
            pills.setAlignment(Pos.CENTER);

            nav.getChildren().addAll(Theme.logo(), sp, pills);
            return nav;
        }

        // ── BODY ─────────────────────────────────────────────────────
        private HBox buildBody() {
            VBox left = buildLeft();
            VBox right = buildGuide();
            HBox.setHgrow(left, Priority.ALWAYS);
            HBox.setHgrow(right, Priority.ALWAYS);
            left.setMaxWidth(Double.MAX_VALUE);
            right.setMaxWidth(Double.MAX_VALUE);

            HBox body = new HBox(left, right);
            body.setPadding(new Insets(52));
            body.setStyle("-fx-background-color:" + Theme.BG + ";");
            Theme.fadeIn(body);
            return body;
        }

        // ── LEFT — title + upload zone + submit ───────────────────────
        private VBox buildLeft() {
            // Title
            Text l1 = new Text("Upload your\n");
            l1.setFill(Color.web(Theme.PARCHMENT));
            l1.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 34));
            Text l2 = new Text("resume data");
            l2.setFill(Color.web(Theme.RED));
            l2.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 34));
            Text l3 = new Text(" file");
            l3.setFill(Color.web(Theme.PARCHMENT));
            l3.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 34));
            TextFlow title = new TextFlow(l1, l2, l3);
            title.setMaxWidth(460);

            Label sub = new Label("Provide your structured .rdl data file to begin.\n"
                    + "Once uploaded, select a template and export your resume.");
            sub.setStyle(Theme.muted());
            sub.setWrapText(true);
            sub.setMaxWidth(420);
            sub.setLineSpacing(4);

            // Upload zone
            VBox zone = buildUploadZone();

            // File row
            fileRow = buildFileRow();
            fileRow.setVisible(false);
            fileRow.setManaged(false);

            // Progress
            progressBar = new ProgressBar(0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.setStyle("-fx-accent:" + Theme.RED + ";"
                    + "-fx-background-color:" + Theme.BORDER + ";"
                    + "-fx-background-radius:3;-fx-pref-height:4;");
            progressBar.setVisible(false);
            progressBar.setManaged(false);

            // Submit btn
            submitBtn = new Button("Proceed to Template Selection  →");
            submitBtn.setMaxWidth(Double.MAX_VALUE);
            submitBtn.setStyle(Theme.btnPrimary());
            submitBtn.setDisable(true);
            submitBtn.setCursor(Cursor.HAND);
            submitBtn.setOnMouseEntered(e -> {
                if (!submitBtn.isDisabled())
                    submitBtn.setStyle(Theme.btnPrimaryHover());
            });
            submitBtn.setOnMouseExited(e -> {
                if (!submitBtn.isDisabled())
                    submitBtn.setStyle(Theme.btnPrimary());
            });
            submitBtn.setOnAction(e -> {
                if (selectedFile != null)
                    showPage2(selectedFile.getName());
            });

            VBox panel = new VBox(20,
                    Theme.eyebrow("Step 01 — Data Input"),
                    title, sub, zone, fileRow, progressBar, submitBtn);
            panel.setPadding(new Insets(0, 52, 0, 0));
            panel.setAlignment(Pos.TOP_LEFT);
            return panel;
        }

        private VBox buildUploadZone() {
            StackPane iconBox = new StackPane();
            iconBox.setPrefSize(52, 52);
            iconBox.setStyle("-fx-background-color:rgba(241,60,32,0.12);-fx-background-radius:12;"
                    + "-fx-border-color:rgba(241,60,32,0.3);-fx-border-radius:12;-fx-border-width:1;"
                    + "-fx-effect:dropshadow(gaussian,rgba(241,60,32,0.2),10,0,0,2);");
            Label arr = new Label("↑");
            arr.setStyle("-fx-text-fill:" + Theme.RED + ";-fx-font-size:22px;-fx-font-weight:bold;");
            iconBox.getChildren().add(arr);

            Label h3 = new Label("Drag & drop your .rdl file");
            h3.setStyle("-fx-text-fill:" + Theme.SILVER_HI + ";-fx-font-size:15px;-fx-font-weight:bold;");
            Label sb = new Label("or click to browse  ·  max 5 MB");
            sb.setStyle("-fx-text-fill:" + Theme.MUTED2 + ";-fx-font-size:12px;");
            Label bdg = new Label(".rdl");
            bdg.setStyle("-fx-background-color:rgba(241,60,32,0.1);-fx-text-fill:" + Theme.RED + ";"
                    + "-fx-font-family:'Courier New';-fx-font-weight:bold;-fx-font-size:11px;"
                    + "-fx-background-radius:5;-fx-border-color:rgba(241,60,32,0.3);"
                    + "-fx-border-radius:5;-fx-border-width:1;-fx-padding:3 10 3 10;");

            String zNorm = "-fx-background-color:" + Theme.CARD + ";-fx-background-radius:14;"
                    + "-fx-border-color:" + Theme.BORDER2 + ";-fx-border-style:dashed;"
                    + "-fx-border-width:1.5;-fx-border-radius:14;-fx-cursor:hand;";
            String zHov = "-fx-background-color:" + Theme.CARD + ";-fx-background-radius:14;"
                    + "-fx-border-color:" + Theme.RED + ";-fx-border-style:solid;"
                    + "-fx-border-width:1.5;-fx-border-radius:14;-fx-cursor:hand;";
            String zDrag = "-fx-background-color:rgba(64,86,161,0.06);-fx-background-radius:14;"
                    + "-fx-border-color:" + Theme.COBALT + ";-fx-border-style:solid;"
                    + "-fx-border-width:1.5;-fx-border-radius:14;";

            VBox zone = new VBox(12, iconBox, h3, sb, bdg);
            zone.setAlignment(Pos.CENTER);
            zone.setPadding(new Insets(36, 28, 36, 28));
            zone.setStyle(zNorm);

            zone.setOnMouseClicked(e -> openChooser());
            zone.setOnMouseEntered(e -> zone.setStyle(zHov));
            zone.setOnMouseExited(e -> zone.setStyle(zNorm));
            zone.setOnDragOver((DragEvent e) -> {
                if (e.getDragboard().hasFiles()) {
                    e.acceptTransferModes(TransferMode.COPY);
                    zone.setStyle(zDrag);
                }
                e.consume();
            });
            zone.setOnDragDropped((DragEvent e) -> {
                Dragboard db = e.getDragboard();
                if (db.hasFiles())
                    handleFile(db.getFiles().get(0));
                zone.setStyle(zNorm);
                e.setDropCompleted(true);
                e.consume();
            });
            return zone;
        }

        private HBox buildFileRow() {
            Label hex = new Label("⬡");
            hex.setStyle("-fx-text-fill:" + Theme.COBALT_H + ";-fx-font-size:14;");

            fileNameLabel = new Label("—");
            fileNameLabel.setStyle("-fx-text-fill:" + Theme.COBALT_H + ";"
                    + "-fx-font-family:'Courier New';-fx-font-size:12px;");
            HBox.setHgrow(fileNameLabel, Priority.ALWAYS);
            fileNameLabel.setMaxWidth(Double.MAX_VALUE);

            Button rem = new Button("✕");
            rem.setStyle("-fx-background-color:transparent;-fx-text-fill:" + Theme.MUTED2
                    + ";-fx-font-size:13;-fx-cursor:hand;-fx-padding:0 4 0 4;");
            rem.setOnMouseEntered(e -> rem.setStyle("-fx-background-color:transparent;-fx-text-fill:" + Theme.RED
                    + ";-fx-font-size:13;-fx-cursor:hand;"));
            rem.setOnMouseExited(e -> rem.setStyle("-fx-background-color:transparent;-fx-text-fill:" + Theme.MUTED2
                    + ";-fx-font-size:13;-fx-cursor:hand;"));
            rem.setOnAction(e -> {
                selectedFile = null;
                fileRow.setVisible(false);
                fileRow.setManaged(false);
                submitBtn.setDisable(true);
                submitBtn.setStyle(Theme.btnPrimary());
            });

            HBox row = new HBox(10, hex, fileNameLabel, rem);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle("-fx-background-color:rgba(64,86,161,0.1);-fx-background-radius:9;"
                    + "-fx-border-color:rgba(64,86,161,0.3);-fx-border-radius:9;-fx-border-width:1;");
            return row;
        }

        private void openChooser() {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Resume Data File");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("RDL Files", "*.rdl"));
            File f = fc.showOpenDialog(primaryStage);
            if (f != null)
                handleFile(f);
        }

        private void handleFile(File f) {
            if (!f.getName().toLowerCase().endsWith(".rdl")) {
                alert("Invalid File", "Only .rdl files are accepted.");
                return;
            }
            if (f.length() > 5L * 1024 * 1024) {
                alert("Too Large", "File exceeds the 5 MB limit.");
                return;
            }
            selectedFile = f;
            fileNameLabel.setText(f.getName());
            fileRow.setVisible(true);
            fileRow.setManaged(true);
            progressBar.setProgress(0);
            progressBar.setVisible(true);
            progressBar.setManaged(true);
            Timeline tl = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame(Duration.millis(900), new KeyValue(progressBar.progressProperty(), 1)));
            tl.setOnFinished(e -> {
                progressBar.setVisible(false);
                progressBar.setManaged(false);
            });
            tl.play();
            submitBtn.setDisable(false);
            submitBtn.setStyle(Theme.btnPrimary());
        }

        // ── RIGHT — Guide panel ────────────────────────────────────────
        private VBox buildGuide() {
            // Header
            StackPane iconBox = new StackPane();
            iconBox.setPrefSize(28, 28);
            iconBox.setStyle("-fx-background-color:rgba(64,86,161,0.25);-fx-background-radius:7;"
                    + "-fx-border-color:rgba(64,86,161,0.4);-fx-border-radius:7;-fx-border-width:1;");
            Label icon = new Label("⌗");
            icon.setStyle("-fx-text-fill:" + Theme.COBALT_H + ";-fx-font-size:13;");
            iconBox.getChildren().add(icon);

            Label title = new Label("Input File Guide");
            title.setStyle("-fx-text-fill:" + Theme.SILVER_HI + ";-fx-font-size:14px;-fx-font-weight:bold;");
            HBox.setHgrow(title, Priority.ALWAYS);

            Label badge = new Label("DOCS");
            badge.setStyle("-fx-background-color:rgba(64,86,161,0.18);-fx-text-fill:" + Theme.COBALT_H + ";"
                    + "-fx-font-family:'Courier New';-fx-font-weight:bold;-fx-font-size:9px;"
                    + "-fx-background-radius:4;-fx-border-color:rgba(64,86,161,0.35);"
                    + "-fx-border-radius:4;-fx-border-width:1;-fx-padding:3 7 3 7;");

            HBox header = new HBox(10, iconBox, title, badge);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(new Insets(16, 20, 16, 20));
            header.setStyle("-fx-background-color:rgba(64,86,161,0.1);"
                    + "-fx-border-color:transparent transparent " + Theme.BORDER2 + " transparent;"
                    + "-fx-border-width:0 0 1 0;");

            // Body
            VBox body = new VBox(22,
                    Theme.guideSection("About the .rdl format",
                            Theme.infoBox("ℹ  .rdl (Resume Data Layout) is ResumeGenX's custom plain-text format — "
                                    + "structured like a simple key-value file, readable in any text editor."),
                            Theme.stepRow("01",
                                    "Create a plain text file and save with .rdl extension — e.g.  my_resume.rdl"),
                            Theme.stepRow("02",
                                    "Write data as KEY: value pairs. Separate sections with one blank line."),
                            Theme.stepRow("03", "Multi-value fields use a pipe separator — val1 | val2 | val3")),
                    Theme.guideSection("File structure example",
                            Theme.codeBlock(
                                    "# Personal Info\n"
                                            + "NAME: John Doe\n"
                                            + "EMAIL: john@email.com\n"
                                            + "PHONE: +91 99999 99999\n\n"
                                            + "# Skills\n"
                                            + "SKILLS: Python | React | Node.js\n\n"
                                            + "# Education\n"
                                            + "EDU_DEGREE: B.Tech Computer Science\n"
                                            + "EDU_YEAR: 2024\n\n"
                                            + "# Experience\n"
                                            + "EXP_TITLE: Software Intern\n"
                                            + "EXP_COMPANY: Acme Corp\n"
                                            + "EXP_PERIOD: 2023-05 to 2023-08")),
                    Theme.guideSection("Common mistakes to avoid",
                            Theme.warnBox(
                                    "⚠  Do NOT rename a .txt to .rdl — parser validates internal structure, not the extension alone."),
                            Theme.warnBox(
                                    "⚠  Avoid < > & \\ \" ' inside values — these break LaTeX rendering on export."),
                            Theme.stepRow("✓", "Keys must be UPPERCASE with underscores — e.g.  EXP_TITLE"),
                            Theme.stepRow("✓", "Date format: YYYY-MM — e.g.  2023-06"),
                            Theme.stepRow("✓", "Sections separated by exactly one blank line."),
                            Theme.stepRow("✓", "Save as UTF-8 encoding. Max file size: 5 MB.")));
            body.setPadding(new Insets(22, 20, 22, 20));
            body.setStyle("-fx-background-color:" + Theme.CARD2 + ";");

            ScrollPane scroll = new ScrollPane(body);
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setStyle("-fx-background:" + Theme.CARD2 + ";-fx-background-color:transparent;");
            VBox.setVgrow(scroll, Priority.ALWAYS);

            VBox panel = new VBox(0, header, scroll);
            panel.setStyle(Theme.guidePanel());
            panel.setMaxHeight(Double.MAX_VALUE);
            return panel;
        }

        private void alert(String t, String m) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle(t);
            a.setHeaderText(null);
            a.setContentText(m);
            a.showAndWait();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // PAGE 2 — Template Selection
    // ══════════════════════════════════════════════════════════════════
    static class Page2 {

        private static final String[][] TEMPLATES = {
                { "1", "Clean Minimal", "minimal", Theme.RED },
                { "2", "Modern Split", "modern", Theme.COBALT },
                { "3", "Academic Pro", "academic", Theme.TEAL },
                { "4", "Creative Bold", "creative", Theme.RED },
                { "5", "Compact Grid", "minimal,modern", Theme.COBALT },
                { "6", "Tech Focused", "modern,creative", Theme.CRIMSON },
        };

        private final BorderPane root;
        private final String fileName;
        private GridPane grid;
        private String filter = "all";
        private String selectedName = null;
        private Label selLabel;
        private Button btnPdf, btnTex;

        Page2(String fileName) {
            this.fileName = fileName;
            root = new BorderPane();
            root.setStyle("-fx-background-color:" + Theme.BG + ";");
            root.setTop(buildNav());
            root.setCenter(buildBody());
            root.setBottom(buildGenBar());
        }

        BorderPane getRoot() {
            return root;
        }

        // ── NAV ──────────────────────────────────────────────────────
        private HBox buildNav() {
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label done = Theme.pill("✓  Upload", false, true);
            done.setCursor(Cursor.HAND);
            done.setOnMouseClicked(e -> showPage1());

            HBox pills = new HBox(6,
                    done, Theme.arrow(),
                    Theme.pill("02  Template", true, false), Theme.arrow(),
                    Theme.pill("03  Generate", false, false));
            pills.setAlignment(Pos.CENTER);

            HBox nav = new HBox(Theme.logo(), sp, pills);
            nav.setStyle(Theme.nav());
            nav.setAlignment(Pos.CENTER_LEFT);
            return nav;
        }

        // ── BODY ─────────────────────────────────────────────────────
        private ScrollPane buildBody() {
            // Page header
            Text t1 = new Text("Choose a ");
            t1.setFill(Color.web(Theme.PARCHMENT));
            t1.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
            Text t2 = new Text("template");
            t2.setFill(Color.web(Theme.RED));
            t2.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
            TextFlow title = new TextFlow(t1, t2);

            Label sub = new Label(
                    "Select the layout that best represents you. All templates support PDF and LaTeX export.");
            sub.setStyle(Theme.muted());

            VBox header = new VBox(10,
                    Theme.eyebrow("Step 02 — Template Selection"),
                    title, sub);

            // Filter bar
            HBox filterBar = buildFilterBar();

            // Grid
            grid = new GridPane();
            grid.setHgap(18);
            grid.setVgap(18);
            for (int i = 0; i < 3; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(33.33);
                cc.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(cc);
            }
            populateGrid();

            VBox content = new VBox(28, header, filterBar, grid);
            content.setPadding(new Insets(48, 52, 24, 52));
            content.setStyle("-fx-background-color:" + Theme.BG + ";");

            Theme.fadeIn(content);

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setStyle("-fx-background:" + Theme.BG + ";-fx-background-color:" + Theme.BG + ";");
            return scroll;
        }

        private HBox buildFilterBar() {
            HBox bar = new HBox(8);
            bar.setAlignment(Pos.CENTER_LEFT);
            for (String f : new String[] { "All", "Minimal", "Modern", "Academic", "Creative" }) {
                Button btn = new Button(f);
                btn.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
                btn.setCursor(Cursor.HAND);
                btn.setPadding(new Insets(6, 14, 6, 14));
                applyFilter(btn, f.equals("All"));
                btn.setOnAction(e -> {
                    bar.getChildren().forEach(n -> {
                        if (n instanceof Button b)
                            applyFilter(b, false);
                    });
                    applyFilter(btn, true);
                    filter = f.toLowerCase();
                    populateGrid();
                });
                bar.getChildren().add(btn);
            }
            return bar;
        }

        private void applyFilter(Button btn, boolean active) {
            if (active) {
                btn.setStyle("-fx-background-color:linear-gradient(to bottom right," + Theme.COBALT + ",#334d8a);"
                        + "-fx-text-fill:white;-fx-background-radius:6;"
                        + "-fx-effect:dropshadow(gaussian,rgba(64,86,161,0.4),10,0,0,2);");
            } else {
                btn.setStyle("-fx-background-color:" + Theme.CARD + ";-fx-text-fill:" + Theme.MUTED2 + ";"
                        + "-fx-background-radius:6;"
                        + "-fx-border-color:" + Theme.BORDER2 + ";-fx-border-radius:6;-fx-border-width:1;");
                btn.setOnMouseEntered(e -> btn.setStyle(
                        "-fx-background-color:" + Theme.CARD + ";-fx-text-fill:" + Theme.COBALT_H + ";"
                                + "-fx-background-radius:6;"
                                + "-fx-border-color:" + Theme.COBALT + ";-fx-border-radius:6;-fx-border-width:1;"));
                btn.setOnMouseExited(e -> applyFilter(btn, false));
            }
        }

        private void populateGrid() {
            grid.getChildren().clear();
            int col = 0, row = 0;
            for (String[] t : TEMPLATES) {
                if (!filter.equals("all") && !t[2].contains(filter))
                    continue;
                VBox card = buildCard(t[0], t[1], t[2], t[3]);
                grid.add(card, col, row);
                if (++col == 3) {
                    col = 0;
                    row++;
                }
            }
        }

        private VBox buildCard(String id, String name, String tags, String accent) {
            Pane preview = buildPreview(id);
            preview.setPrefHeight(170);
            preview.setMaxWidth(Double.MAX_VALUE);

            Rectangle sep = new Rectangle(1, 1);
            sep.setHeight(1);
            sep.setFill(Color.web(Theme.BORDER));
            sep.widthProperty().bind(preview.widthProperty());

            // Meta
            Label nl = new Label(name);
            nl.setStyle("-fx-text-fill:" + Theme.SILVER_HI + ";-fx-font-size:13px;-fx-font-weight:bold;");
            HBox tagRow = new HBox(6);
            tagRow.setAlignment(Pos.CENTER_LEFT);
            for (String tg : tags.split(",")) {
                Label tl = new Label(tg.trim());
                tl.setStyle("-fx-background-color:rgba(64,86,161,0.14);-fx-text-fill:" + Theme.LAVENDER + ";"
                        + "-fx-font-family:'Courier New';-fx-font-size:9px;-fx-background-radius:4;"
                        + "-fx-border-color:rgba(64,86,161,0.28);-fx-border-radius:4;"
                        + "-fx-border-width:1;-fx-padding:2 6 2 6;");
                tagRow.getChildren().add(tl);
            }
            VBox meta = new VBox(6, nl, tagRow);
            meta.setPadding(new Insets(12, 14, 12, 14));
            meta.setStyle("-fx-background-color:rgba(0,0,0,0.25);-fx-background-radius:0 0 12 12;");

            VBox card = new VBox(0, preview, sep, meta);
            card.setCursor(Cursor.HAND);
            card.setPrefWidth(Double.MAX_VALUE);
            card.setStyle(Theme.cardBase());

            card.setOnMouseEntered(e -> {
                if (!card.getStyle().contains("border-width: 2"))
                    card.setStyle(Theme.cardHover());
            });
            card.setOnMouseExited(e -> {
                if (!card.getStyle().contains("border-width: 2"))
                    card.setStyle(Theme.cardBase());
            });
            card.setOnMouseClicked(e -> {
                grid.getChildren().forEach(n -> {
                    if (n instanceof VBox v)
                        v.setStyle(Theme.cardBase());
                });
                card.setStyle(Theme.cardSelected());
                selectedName = name;
                selLabel.setText(name);
                selLabel.setStyle("-fx-text-fill:" + Theme.SILVER_HI + ";-fx-font-size:15px;-fx-font-weight:bold;");
                btnPdf.setDisable(false);
                btnTex.setDisable(false);
            });
            return card;
        }

        // ── TEMPLATE PREVIEWS ─────────────────────────────────────────
        private Pane buildPreview(String id) {
            Pane p = new Pane();
            String bg = switch (id) {
                case "1", "2", "3", "5" -> "#EFE2BA";
                default -> "#0d1018";
            };
            p.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:12 12 0 0;");
            switch (id) {
                case "1" -> drawMinimal(p);
                case "2" -> drawSplit(p);
                case "3" -> drawAcademic(p);
                case "4" -> drawCreative(p);
                case "5" -> drawGrid(p);
                case "6" -> drawTech(p);
            }
            return p;
        }

        private void r(Pane p, double x, double y, double w, double h, double rx, String fill, double op) {
            Rectangle rect = new Rectangle(x, y, w, h);
            rect.setArcWidth(rx * 2);
            rect.setArcHeight(rx * 2);
            rect.setFill(Color.web(fill, op));
            p.getChildren().add(rect);
        }

        private void t(Pane p, String txt, double x, double y, String col, double sz, boolean bold) {
            Text tx = new Text(txt);
            tx.setX(x);
            tx.setY(y);
            tx.setFill(Color.web(col));
            tx.setFont(Font.font("System", bold ? FontWeight.BOLD : FontWeight.NORMAL, sz));
            p.getChildren().add(tx);
        }

        private void drawMinimal(Pane p) {
            r(p, 0, 0, 360, 40, 0, "#0B0C10", 1);
            r(p, 0, 0, 360, 1, 0, Theme.SILVER_HI, 0.2);
            t(p, "John Doe", 14, 17, Theme.SILVER_HI, 11, true);
            t(p, "Software Engineer  ·  john@email.com", 14, 30, Theme.COBALT_H, 7, false);
            t(p, "EXPERIENCE", 14, 58, Theme.COBALT, 7, true);
            r(p, 14, 63, 50, 4, 2, Theme.RED, 1);
            r(p, 14, 71, 100, 3, 1.5, "#c8b89a", 1);
            r(p, 14, 77, 78, 3, 1.5, "#c8b89a", 1);
            t(p, "EDUCATION", 14, 100, Theme.COBALT, 7, true);
            r(p, 14, 105, 62, 4, 2, Theme.RED, 1);
            r(p, 14, 113, 85, 3, 1.5, "#c8b89a", 1);
            t(p, "SKILLS", 14, 136, Theme.COBALT, 7, true);
            r(p, 14, 141, 26, 9, 4, Theme.LAVENDER, 0.7);
            r(p, 44, 141, 20, 9, 4, Theme.LAVENDER, 0.7);
            r(p, 68, 141, 30, 9, 4, Theme.LAVENDER, 0.7);
        }

        private void drawSplit(Pane p) {
            r(p, 0, 0, 68, 170, 0, "#0B0C10", 1);
            r(p, 67, 0, 1, 170, 0, Theme.COBALT, 0.5);
            t(p, "◈", 22, 35, Theme.COBALT_H, 20, false);
            t(p, "JOHN DOE", 6, 52, Theme.LAVENDER, 5.5, true);
            t(p, "SKILLS", 12, 72, Theme.RED, 6, true);
            r(p, 12, 75, 40, 3, 1.5, "#22252f", 1);
            r(p, 12, 81, 32, 3, 1.5, "#22252f", 1);
            t(p, "John Doe", 80, 17, "#0B0C10", 12, true);
            t(p, "Software Engineer", 80, 30, Theme.CRIMSON, 7, false);
            t(p, "EXPERIENCE", 80, 50, Theme.COBALT, 7, true);
            r(p, 80, 54, 85, 3.5, 1.5, Theme.RED, 0.6);
            r(p, 80, 61, 120, 2.5, 1, "#c8b89a", 1);
            t(p, "EDUCATION", 80, 80, Theme.COBALT, 7, true);
            r(p, 80, 84, 75, 3.5, 1.5, Theme.RED, 0.6);
        }

        private void drawAcademic(Pane p) {
            t(p, "JOHN DOE", 120, 20, "#0B0C10", 13, true);
            t(p, "B.Tech CS  ·  john@email.com", 50, 30, Theme.CRIMSON, 6.5, false);
            r(p, 18, 37, 204, 1.2, 0, Theme.RED, 1);
            r(p, 18, 39, 204, 0.4, 0, Theme.COBALT, 1);
            t(p, "RESEARCH INTERESTS", 18, 52, Theme.COBALT, 8, true);
            r(p, 18, 56, 95, 2.5, 1, Theme.LAVENDER, 0.7);
            r(p, 18, 62, 70, 2.5, 1, Theme.LAVENDER, 0.7);
            t(p, "EDUCATION", 18, 83, Theme.COBALT, 8, true);
            r(p, 18, 87, 90, 3, 1, Theme.TEAL, 0.8);
            r(p, 18, 94, 118, 2.5, 1, Theme.LAVENDER, 0.7);
            t(p, "PUBLICATIONS", 18, 112, Theme.COBALT, 8, true);
            r(p, 18, 116, 130, 3, 1, Theme.TEAL, 0.8);
            r(p, 18, 123, 100, 2.5, 1, Theme.LAVENDER, 0.7);
        }

        private void drawCreative(Pane p) {
            r(p, 0, 0, 360, 170, 0, "#0d1018", 1);
            r(p, 0, 0, 360, 5, 0, Theme.RED, 1);
            r(p, 0, 0, 360, 1, 0, Theme.SILVER_HI, 0.15);
            t(p, "JOHN", 14, 27, Theme.SILVER_HI, 15, true);
            t(p, "DOE.", 14, 45, Theme.RED, 15, true);
            t(p, "Full Stack Developer", 95, 45, "#3d4258", 6, false);
            r(p, 14, 52, 90, 0.8, 0, Theme.COBALT, 0.4);
            t(p, "EXPERIENCE", 14, 64, Theme.COBALT_H, 6.5, false);
            r(p, 14, 68, 3, 16, 1, Theme.RED, 1);
            r(p, 21, 68, 72, 3.5, 1.5, "#1c1f28", 1);
            r(p, 21, 75, 112, 2.5, 1, "#16191f", 1);
            t(p, "SKILLS", 14, 100, Theme.COBALT_H, 6.5, false);
            for (double[] s : new double[][] { { 14, 103, 32 }, { 50, 103, 26 }, { 80, 103, 36 } }) {
                Rectangle rect = new Rectangle(s[0], s[1], s[2], 11);
                rect.setArcWidth(10);
                rect.setArcHeight(10);
                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(Color.web(Theme.RED));
                rect.setStrokeWidth(0.8);
                p.getChildren().add(rect);
            }
        }

        private void drawGrid(Pane p) {
            r(p, 0, 0, 360, 32, 0, Theme.COBALT, 1);
            r(p, 0, 0, 360, 1, 0, Theme.LAVENDER, 0.3);
            t(p, "John Doe", 12, 14, Theme.PARCHMENT, 10, true);
            t(p, "Software Engineer  ·  john@email.com", 12, 26, Theme.LAVENDER, 6, false);
            r(p, 0, 32, 360, 0.5, 0, "#c8b89a", 1);
            r(p, 120, 32, 1, 138, 0, "#c8b89a", 1);
            t(p, "EXPERIENCE", 8, 47, Theme.RED, 7, true);
            r(p, 8, 51, 75, 3.5, 1.5, Theme.RED, 0.55);
            r(p, 8, 58, 88, 2.5, 1, "#c8b89a", 1);
            t(p, "EDUCATION", 128, 47, Theme.COBALT, 7, true);
            r(p, 128, 51, 80, 3.5, 1.5, Theme.COBALT, 0.55);
            r(p, 128, 58, 65, 2.5, 1, "#c8b89a", 1);
            t(p, "SKILLS", 128, 76, Theme.COBALT, 7, true);
            r(p, 128, 80, 28, 8, 4, Theme.LAVENDER, 0.7);
            r(p, 160, 80, 22, 8, 4, Theme.LAVENDER, 0.7);
        }

        private void drawTech(Pane p) {
            r(p, 0, 0, 360, 170, 0, "#111318", 1);
            r(p, 0, 0, 180, 4, 0, Theme.RED, 1);
            r(p, 180, 0, 180, 4, 0, Theme.COBALT, 1);
            r(p, 0, 0, 360, 1, 0, Theme.SILVER_HI, 0.18);
            t(p, "<JohnDoe/>", 14, 21, Theme.SILVER_HI, 9, true);
            t(p, "Full Stack Dev  ·  john@email.com", 14, 32, "#3d4258", 5.5, false);
            r(p, 14, 45, 38, 9, 2, Theme.RED, 1);
            t(p, "EXPERIENCE", 20, 53, "#ffffff", 5, false);
            r(p, 56, 45, 38, 9, 2, Theme.COBALT, 0.45);
            t(p, "EDUCATION", 59, 53, "#6b7390", 5, false);
            r(p, 98, 45, 26, 9, 2, Theme.COBALT, 0.25);
            t(p, "SKILLS", 101, 53, "#6b7390", 5, false);
            r(p, 14, 63, 4, 4, 1, Theme.RED, 1);
            r(p, 22, 63, 85, 4, 1.5, "#1c1f28", 1);
            r(p, 22, 71, 128, 3, 1, "#16191f", 1);
            r(p, 14, 87, 4, 4, 1, Theme.COBALT, 1);
            r(p, 22, 87, 70, 4, 1.5, "#1c1f28", 1);
            t(p, "// tech stack", 14, 110, Theme.CRIMSON, 6, false);
            r(p, 14, 115, 28, 9, 3, Theme.COBALT, 0.5);
            r(p, 46, 115, 23, 9, 3, Theme.RED, 0.4);
            r(p, 73, 115, 34, 9, 3, Theme.COBALT, 0.5);
            r(p, 111, 115, 26, 9, 3, Theme.RED, 0.4);
        }

        // ── GENERATE BAR ─────────────────────────────────────────────
        private HBox buildGenBar() {
            Label lbl = new Label("SELECTED TEMPLATE");
            lbl.setStyle("-fx-text-fill:" + Theme.SILVER_LO
                    + ";-fx-font-family:'Courier New';-fx-font-size:9px;-fx-font-weight:bold;");
            selLabel = new Label("No template selected yet");
            selLabel.setStyle("-fx-text-fill:" + Theme.MUTED2 + ";-fx-font-size:14px;");
            VBox info = new VBox(4, lbl, selLabel);
            HBox.setHgrow(info, Priority.ALWAYS);

            btnTex = new Button("⌨  Export LaTeX");
            btnTex.setFont(Font.font("System", FontWeight.BOLD, 13));
            btnTex.setCursor(Cursor.HAND);
            btnTex.setDisable(true);
            btnTex.setStyle(Theme.btnSecondary());
            btnTex.setOnMouseEntered(e -> {
                if (!btnTex.isDisabled())
                    btnTex.setStyle(Theme.btnSecondary().replace(Theme.COBALT, Theme.COBALT_H));
            });
            btnTex.setOnMouseExited(e -> btnTex.setStyle(Theme.btnSecondary()));
            btnTex.setOnAction(e -> TemplateDialog.show(selectedName, "LaTeX", fileName));

            btnPdf = new Button("↓  Generate PDF");
            btnPdf.setFont(Font.font("System", FontWeight.BOLD, 13));
            btnPdf.setCursor(Cursor.HAND);
            btnPdf.setDisable(true);
            btnPdf.setStyle(Theme.btnPrimary());
            btnPdf.setOnMouseEntered(e -> {
                if (!btnPdf.isDisabled())
                    btnPdf.setStyle(Theme.btnPrimaryHover());
            });
            btnPdf.setOnMouseExited(e -> btnPdf.setStyle(Theme.btnPrimary()));
            btnPdf.setOnAction(e -> TemplateDialog.show(selectedName, "PDF", fileName));

            HBox bar = new HBox(16, info, btnTex, btnPdf);
            bar.setPadding(new Insets(18, 52, 18, 52));
            bar.setAlignment(Pos.CENTER_LEFT);
            bar.setStyle("-fx-background-color:linear-gradient(to right,#1c1f28,#1a1d24);"
                    + "-fx-border-color:" + Theme.RED + " " + Theme.BORDER2 + " " + Theme.BORDER2 + " " + Theme.RED
                    + ";"
                    + "-fx-border-width:0 0 0 3;"
                    + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.5),16,0,0,-4);");
            return bar;
        }
    }
}