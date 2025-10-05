package hsb.lol.lolfight.ui;

import hsb.lol.lolfight.config.Config;
import hsb.lol.lolfight.data.Summoner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeroPriorityConfigWindow {

    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("配置英雄优先级");
        window.setMinWidth(650);
        window.setMinHeight(450);

        // --- 1. Data Initialization ---

        // Source list for the right side (priority heroes)
        ObservableList<String> priorityHeroes = FXCollections.observableArrayList(Config.heroNames);

        // Source list for the left side (all available heroes)
        ObservableList<String> availableHeroes = FXCollections.observableArrayList();
        boolean allChampionsAvailable = (Summoner.allChampions != null && !Summoner.allChampions.isEmpty());
        if (allChampionsAvailable) {
            List<String> allHeroNames = new ArrayList<>(Summoner.allChampions.keySet());
            // Remove heroes that are already in the priority list
            allHeroNames.removeAll(priorityHeroes);
            availableHeroes.addAll(allHeroNames);
        }
        // Always sort the source list so additions are inserted in the correct place
        FXCollections.sort(availableHeroes);


        // --- 2. UI Components ---

        // Left Pane: All available heroes
        Label availableLabel = new Label("所有英雄");
        TextField searchField = new TextField();
        searchField.setPromptText("搜索英雄...");

        // ** Use a FilteredList for live searching **
        FilteredList<String> filteredAvailableHeroes = new FilteredList<>(availableHeroes, p -> true);

        // Wrap the FilteredList in a SortedList to keep it alphabetically ordered
        SortedList<String> sortedAvailableHeroes = new SortedList<>(filteredAvailableHeroes, Comparator.naturalOrder());

        ListView<String> availableListView = new ListView<>(sortedAvailableHeroes);
        VBox availableBox = new VBox(10, availableLabel, searchField, availableListView);
        availableBox.setPadding(new Insets(10));
        VBox.setVgrow(availableListView, Priority.ALWAYS);
        availableBox.setDisable(!allChampionsAvailable); // Disable if hero data isn't loaded

        // Right Pane: Current priority list
        Label priorityLabel = new Label("优先级列表 (上 > 下)");
        ListView<String> priorityListView = new ListView<>(priorityHeroes);
        VBox priorityBox = new VBox(10, priorityLabel, priorityListView);
        priorityBox.setPadding(new Insets(10));
        VBox.setVgrow(priorityListView, Priority.ALWAYS);

        // Center Pane: Action Buttons
        Button addButton = new Button(" > ");
        Button removeButton = new Button(" < ");
        Button addAllButton = new Button(">>");
        Button removeAllButton = new Button("<<");
        VBox addRemoveBox = new VBox(10, addButton, removeButton, addAllButton, removeAllButton);
        addRemoveBox.setAlignment(Pos.CENTER);
        addRemoveBox.setPadding(new Insets(10));
        addRemoveBox.setDisable(!allChampionsAvailable);

        // Far Right Pane: Reorder Buttons
        Button moveUpButton = new Button("上移");
        Button moveDownButton = new Button("下移");
        VBox reorderBox = new VBox(10, moveUpButton, moveDownButton);
        reorderBox.setAlignment(Pos.CENTER);
        reorderBox.setPadding(new Insets(0, 10, 0, 0));

        // Bottom Pane: Save/Cancel Buttons
        Button saveButton = new Button("保存并关闭");
        Button cancelButton = new Button("取消");
        HBox bottomBox = new HBox(10, saveButton, cancelButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(10));

        // --- 3. Event Handling ---

        // Search field listener updates the FilteredList's predicate
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAvailableHeroes.setPredicate(hero -> {
                // If filter text is empty, display all heroes.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare hero name with filter text, ignoring case.
                String lowerCaseFilter = newValue.toLowerCase();
                return hero.toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Add selected hero to priority list
        addButton.setOnAction(e -> {
            String selected = availableListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                availableHeroes.remove(selected);
                priorityHeroes.add(selected);
            }
        });

        // Remove selected hero from priority list
        removeButton.setOnAction(e -> {
            String selected = priorityListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                priorityHeroes.remove(selected);
                availableHeroes.add(selected);
                FXCollections.sort(availableHeroes); // Keep the source list sorted
            }
        });

        // Add all visible heroes to priority list
        addAllButton.setOnAction(e -> {
            List<String> heroesToAdd = new ArrayList<>(availableListView.getItems());
            priorityHeroes.addAll(heroesToAdd);
            availableHeroes.removeAll(heroesToAdd);
        });

        // Remove all heroes from priority list
        removeAllButton.setOnAction(e -> {
            availableHeroes.addAll(priorityHeroes);
            priorityHeroes.clear();
            FXCollections.sort(availableHeroes);
        });

        // Move selected hero up in priority
        moveUpButton.setOnAction(e -> {
            int index = priorityListView.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                String selected = priorityHeroes.remove(index);
                priorityHeroes.add(index - 1, selected);
                priorityListView.getSelectionModel().select(index - 1);
            }
        });

        // Move selected hero down in priority
        moveDownButton.setOnAction(e -> {
            int index = priorityListView.getSelectionModel().getSelectedIndex();
            if (index != -1 && index < priorityHeroes.size() - 1) {
                String selected = priorityHeroes.remove(index);
                priorityHeroes.add(index + 1, selected);
                priorityListView.getSelectionModel().select(index + 1);
            }
        });

        // Save changes to config and close
        saveButton.setOnAction(e -> {
            Config.heroNames = new ArrayList<>(priorityHeroes);
            window.close();
        });

        // Close without saving
        cancelButton.setOnAction(e -> window.close());

        // --- 4. Layout Assembly ---
        HBox centerContent = new HBox(5, priorityBox, reorderBox);
        HBox.setHgrow(priorityBox, Priority.ALWAYS);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(availableBox);
        mainLayout.setCenter(addRemoveBox);
        mainLayout.setRight(centerContent);
        mainLayout.setBottom(bottomBox);

        // --- 5. Scene and Stage Setup ---
        Scene scene = new Scene(mainLayout);
        window.setScene(scene);
        window.showAndWait();
    }
}