//FINAL BACKUP MAIN
//Ohr Rafaeloff 
package com.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * *********** Declare UI Components and Data Collections ***********
     */
    private BorderPane mainPane;
    private Scene scene;
    private MenuBar menuBar;
    private Menu studentMenu;
    private MenuItem addStuItem;
    private MenuItem editStuItem;
    private MenuItem DisStuItem;
    private MenuItem exitMenuItem;
    private MenuItem viewGradesItem;
    private MenuItem addCourseItem;
    private MenuItem editCourseItem;
    private MenuItem DisCourseItem;
    private MenuItem addEnrollmentItem;
    private MenuItem editEnrollmentItem;
    private MenuItem generateReportItem;
    private MenuItem manageGradesItem;
    private MenuItem showAvailableCoursesItem; // New menu item
    private Menu deleteMenu; // New menu for delete functions
    private MenuItem deleteStudentItem; // New menu item
    private MenuItem deleteCourseItem; // New menu item
    private MenuItem deleteEnrollmentItem; // New menu item
    private Connection dbConnection;

    private LinkedList<Student> students;
    private LinkedList<Course> courses;
    private LinkedList<Enrollment> enrollments;
    private LinkedList<Professor> professors;
    private LinkedList<Department> departments;

    // Tracking next ID info
    private int nextCourseId;
    private int nextStudentId;
    private int nextEnrollmentId;
    private int nextProfessorId;
    private int nextDepartmentId;

    private TextField studentIdField;
    private String currentManageContext = ""; // Keep track of current context ("student" or "course")

    /**
     * *********** Initialization Method for Next IDs ************
     */
    /**
     * Initializes the next available IDs for students, courses, enrollments,
     * professors, and departments. Makes sure the IDs are set to the max
     * current ID plus one, or starts from 1 if the list is empty.
     */
    private void initializeNextIds() {
        nextStudentId = getNextId(students, Student::getStuId);
        nextCourseId = getNextId(courses, Course::getId);
        nextEnrollmentId = getNextId(enrollments, Enrollment::getEnrollmentID);
        nextProfessorId = getNextId(professors, Professor::getId);
        nextDepartmentId = getNextId(departments, Department::getId);
    }

    /**
     * Helper method to determine the next available ID.
     *
     * @param list The list containing the objects (students, courses, etc.).
     * @param idGetter A function to extract the ID from the object as a String.
     * @param <T> The type of the object in the list.
     * @return The next available ID (max ID + 1) or 1 if the list is empty.
     */
    // Using a generic method like we learned in class, this enhances reusability by
    // allowing us to determine the next available ID for any type of list
    // (students, courses, enrollments, etc.). This prevents redundancy by avoiding
    // separate methods for each entity type.
    private <T> int getNextId(List<T> list, Function<T, String> idGetter) {
        return list.stream()
                .mapToInt(item -> Integer.valueOf(idGetter.apply(item)))
                .max()
                .orElse(0) + 1;
    }

    /**
     * *********** End of Initialization Method for Next IDs ************
     */
    /**
     * *********** (End of declaration section) **************
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Connection conn = Database.connect(); // Open connection to the database
            if (conn != null) {
                students = DatabaseHelper.loadStudents(conn);
                courses = DatabaseHelper.loadCourses(conn);
                enrollments = DatabaseHelper.loadEnrollments(conn);
                professors = DatabaseHelper.loadProfessors(conn);
                departments = DatabaseHelper.loadDepartments(conn);

                conn.close(); // Make sure to close the connection
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException ex) {
            System.out.println("Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        // Set the size of the window to the preferred dimensions
        primaryStage.setWidth(1200); // Adjust the width
        primaryStage.setHeight(800); // Adjust the height

        // Center the window on the screen
        primaryStage.centerOnScreen();
        studentIdField = new TextField();

        /**
         * *********** Initialize Data Collections and Load Data ***********
         */
        professors = new LinkedList<>();
        students = new LinkedList<>();
        courses = new LinkedList<>();
        enrollments = new LinkedList<>();
        departments = new LinkedList<>();

        // Initialize the next IDs after loading all data
        initializeNextIds();

        /**
         * *********** (End of initialization section) **************
         */
        /**
         * *********** Setup Initial UI ***********
         */
        Button btn = new Button("Add Student");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.add(btn, 0, 0);

        Scene scene = new Scene(grid, 1200, 800);
        primaryStage.setTitle("Student Management System");

        btn.setOnAction(e -> showAddStudentDialog());
        primaryStage.setScene(scene);

        primaryStage.show();

        /**
         * *********** (End of initial UI setup section) **************
         */
        /**
         * *********** Initialize Menu Items ***********
         */
        addStuItem = new MenuItem("Add Student");
        editStuItem = new MenuItem("Edit Student");
        DisStuItem = new MenuItem("Display Students");
        exitMenuItem = new MenuItem("Exit");
        viewGradesItem = new MenuItem("Manage Grades");
        addCourseItem = new MenuItem("Add Course");
        editCourseItem = new MenuItem("Edit Course");
        DisCourseItem = new MenuItem("Display Courses");
        addEnrollmentItem = new MenuItem("Add Enrollment");
        editEnrollmentItem = new MenuItem("View/Edit Enrollment");
        generateReportItem = new MenuItem("Generate Report");
        manageGradesItem = new MenuItem("Manage Grades");
        showAvailableCoursesItem = new MenuItem("Show Available Courses"); // Initialize new menu item
        deleteStudentItem = new MenuItem("Delete Student"); // Initialize new menu item
        deleteCourseItem = new MenuItem("Delete Course"); // Initialize new menu item
        deleteEnrollmentItem = new MenuItem("Delete Enrollment"); // Initialize new menu item

        // ** MENU BAR ** //
        menuBar = new MenuBar();

        Menu professorMenu = new Menu("Professor");

        MenuItem addProfessorItem = new MenuItem("Add Professor");
        MenuItem editProfessorItem = new MenuItem("Edit Professor");
        MenuItem displayProfessorItem = new MenuItem("Display Professors");

        MenuItem deleteProfessorItem = new MenuItem("Delete Professor");
        MenuItem deleteDepartmentItem = new MenuItem("Delete Department");

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(exitMenuItem);

        Menu coursesMenu = new Menu("Courses");
        coursesMenu.getItems().addAll(addCourseItem, editCourseItem, DisCourseItem, showAvailableCoursesItem); // Add
        // new
        // menu
        // Item
        Menu enrollmentsMenu = new Menu("Enrollments");
        enrollmentsMenu.getItems().addAll(addEnrollmentItem, editEnrollmentItem);

        Menu reportsMenu = new Menu("Reports");
        reportsMenu.getItems().addAll(generateReportItem);

        studentMenu = new Menu("Student");
        studentMenu.getItems().addAll(addStuItem, editStuItem, DisStuItem);

        deleteMenu = new Menu("Delete"); // Initialize new menu
        deleteMenu.getItems().addAll(deleteStudentItem, deleteCourseItem, deleteEnrollmentItem, deleteDepartmentItem,
                deleteProfessorItem); // Add new menu items

        professorMenu.getItems().addAll(addProfessorItem, editProfessorItem, displayProfessorItem);

        Menu departmentMenu = new Menu("Department");

        menuBar.getMenus().addAll(fileMenu, professorMenu, departmentMenu, studentMenu, coursesMenu, enrollmentsMenu,
        reportsMenu, deleteMenu);

        /**
         * *********** (End of menu items initialization section)
         * **************
         */
        /**
         * *********** Set Actions for Menu Items ***********
         */
        addStuItem.setOnAction(e -> showAddStudentDialog());
        editStuItem.setOnAction(e -> showEditStudentDialog());
        DisStuItem.setOnAction(e -> showDisplayStudentsDialog());
        exitMenuItem.setOnAction(e -> exitApplication(primaryStage)); // Update this line
        addCourseItem.setOnAction(e -> showAddCourseDialog());
        editCourseItem.setOnAction(e -> showEditCourseDialog());
        DisCourseItem.setOnAction(e -> showDisplayCoursesDialog());
        addEnrollmentItem.setOnAction(e -> showAddEnrollmentDialog());
        editEnrollmentItem.setOnAction(e -> showDisplayEnrollmentsDialog());
        manageGradesItem.setOnAction(e -> showManageGradesDialog());
        generateReportItem.setOnAction(e -> showGenerateReportDialog());
        showAvailableCoursesItem.setOnAction(e -> showAvailableCoursesDialog()); // Set action for new menu item
        deleteStudentItem.setOnAction(e -> showDeleteStudentDialog()); // Set action for new menu item
        deleteCourseItem.setOnAction(e -> showDeleteCourseDialog()); // Set action for new menu item
        deleteEnrollmentItem.setOnAction(e -> showDeleteEnrollmentDialog()); // Set action for new menu item

        // delete professor and dept
        deleteProfessorItem.setOnAction(e -> showDeleteProfessorDialog());
        deleteDepartmentItem.setOnAction(e -> showDeleteDepartmentDialog());


        addProfessorItem.setOnAction(e -> showAddProfessorDialog());
        editProfessorItem.setOnAction(e -> showEditProfessorDialog());
        displayProfessorItem.setOnAction(e -> showDisplayProfessorsDialog());

        // Department Menu setonaction
        departmentMenu.getItems().addAll(
                new MenuItem("Add Department"),
                new MenuItem("Edit Department"),
                new MenuItem("Display Departments"));

        departmentMenu.getItems().get(0).setOnAction(e -> showAddDepartmentDialog());
        departmentMenu.getItems().get(1).setOnAction(e -> showEditDepartmentDialog());
        departmentMenu.getItems().get(2).setOnAction(e -> showDisplayDepartmentsDialog());

        mainPane = new BorderPane();
        mainPane.setTop(menuBar);

        scene = new Scene(mainPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            // Save data to files
            DatabaseHelper.saveData(students, courses, enrollments, professors, departments);
        });
    }

    /**
     * *********** (End of setting menu item actions section) *******
     */
    /**
     * ******** Start Of Professor Methods *********
     */
    private void showAddProfessorDialog() {
        Label idLabel = new Label("Professor ID:");

        // Fetch the next available professor ID from the database
        try (Connection conn = Database.connect()) {
            if (conn == null) {
                showAlert("Database Error", "Could not connect to the database.");
                return;
            }
            nextProfessorId = DatabaseHelper.getNextAvailableProfessorId(conn);
        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while fetching the next Professor ID: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        TextField idField = new TextField(String.valueOf(nextProfessorId));
        idField.setEditable(false);

        Label nameLabel = new Label("Professor Name:");
        TextField nameField = new TextField();

        Label deptLabel = new Label("Department:");
        ChoiceBox<String> deptChoiceBox = new ChoiceBox<>();

        // Load departments from the database and populate the ChoiceBox
        try (Connection conn = Database.connect()) {
            if (conn == null) {
                showAlert("Error", "Could not connect to the database.");
                return;
            }

            LinkedList<Department> departments = DatabaseHelper.loadDepartments(conn);
            ObservableList<String> departmentNames = FXCollections.observableArrayList(
                    departments.stream().map(Department::getName).collect(Collectors.toList()));
            deptChoiceBox.setItems(departmentNames);
        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while loading department data: " + ex.getMessage());
            ex.printStackTrace();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(deptLabel, 0, 2);
        grid.add(deptChoiceBox, 1, 2);

        Button addButton = new Button("ADD");
        Button cancelButton = new Button("Cancel");

        grid.add(addButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        mainPane.setCenter(grid);

        addButton.setOnAction(e -> {
            String professorId = idField.getText();
            String professorName = nameField.getText();
            String departmentName = deptChoiceBox.getValue();

            // Input validation
            if (professorId.isEmpty() || professorName.isEmpty() || departmentName == null
                    || departmentName.isEmpty()) {
                showAlert("Input Error", "All fields are required.");
                return;
            }

            // Find the department ID corresponding to the selected department name
            String deptId = null;
            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                LinkedList<Department> departments = DatabaseHelper.loadDepartments(conn);
                for (Department department : departments) {
                    if (department.getName().equals(departmentName)) {
                        deptId = department.getId();
                        break;
                    }
                }

                if (deptId == null) {
                    showAlert("Error", "Selected department could not be found.");
                    return;
                }

                Professor professor = new Professor(professorId, professorName, deptId);

                // Check for duplicates in the database and add the professor if no duplicate is
                // found
                Professor existingProfessor = DatabaseHelper.getProfessorById(conn, professorId);
                if (existingProfessor == null) {
                    DatabaseHelper.insertProfessor(professor);

                    // Update the next available Professor ID
                    nextProfessorId = DatabaseHelper.getNextAvailableProfessorId(conn);
                    showAlert("Success", "Professor added successfully!");
                    mainPane.setCenter(null);
                } else {
                    showAlert("Duplicate Professor ID", "A professor with this ID already exists.");
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while adding the professor: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> mainPane.setCenter(null));
    }

    private void showEditProfessorDialog() {
        Label idLabel = new Label("Professor ID:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Professor Name:");
        TextField nameField = new TextField();

        Label deptLabel = new Label("Department:");
        ChoiceBox<String> deptChoiceBox = new ChoiceBox<>();

        // Populate the department choice box dynamically from the database
        try (Connection conn = Database.connect()) {
            if (conn == null) {
                showAlert("Error", "Could not connect to the database.");
                return;
            }

            LinkedList<Department> departments = DatabaseHelper.loadDepartments(conn);
            ObservableList<String> departmentNames = FXCollections.observableArrayList(
                    departments.stream().map(Department::getName).collect(Collectors.toList()));
            deptChoiceBox.setItems(departmentNames);
        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while loading department data: " + ex.getMessage());
            ex.printStackTrace();
        }

        Button searchButton = new Button("Search");
        Button saveButton = new Button("Save");
        Button resetButton = new Button("Reset");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(deptLabel, 0, 2);
        grid.add(deptChoiceBox, 1, 2);
        grid.add(saveButton, 1, 3);
        grid.add(resetButton, 2, 3);

        mainPane.setCenter(grid);

        // Search for Professor by ID
        searchButton.setOnAction(e -> {
            String searchId = idField.getText();
            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                Professor professor = DatabaseHelper.getProfessorById(conn, searchId);
                if (professor != null) {
                    idField.setEditable(false);
                    nameField.setText(professor.getName());

                    // Load the departments from the database
                    LinkedList<Department> departments = DatabaseHelper.loadDepartments(conn);
                    ObservableList<String> departmentNames = FXCollections.observableArrayList(
                            departments.stream().map(Department::getName).collect(Collectors.toList()));
                    deptChoiceBox.setItems(departmentNames);

                    // Find and select the department name in the choice box based on the department
                    // ID of the professor
                    String deptId = professor.getDepartment();
                    for (Department department : departments) {
                        if (department.getId().equals(deptId)) {
                            deptChoiceBox.setValue(department.getName());
                            break;
                        }
                    }
                } else {
                    showAlert("Not Found", "No professor found with the given ID.");
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while fetching professor data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Save changes to professor data
        saveButton.setOnAction(e -> {
            String professorId = idField.getText();
            String professorName = nameField.getText();
            String departmentName = deptChoiceBox.getValue();

            if (professorName.isEmpty() || departmentName == null || departmentName.isEmpty()) {
                showAlert("Input Error", "All fields are required.");
                return;
            }

            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                // Find department ID based on the department name
                LinkedList<Department> departments = DatabaseHelper.loadDepartments(conn);
                String deptId = null;
                for (Department department : departments) {
                    if (department.getName().equals(departmentName)) {
                        deptId = department.getId();
                        break;
                    }
                }

                if (deptId == null) {
                    showAlert("Error", "Selected department could not be found.");
                    return;
                }

                // Update professor details
                Professor updatedProfessor = new Professor(professorId, professorName, deptId);
                DatabaseHelper.updateProfessor(updatedProfessor);
                showAlert("Success", "Professor updated successfully!");
                mainPane.setCenter(null);
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while updating professor data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Reset all fields
        resetButton.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            deptChoiceBox.setValue(null);
            idField.setEditable(true);
        });
    }

    @SuppressWarnings("unchecked")
    private void showDisplayProfessorsDialog() {
        TableView<Professor> tableView = new TableView<>();

        TableColumn<Professor, String> idColumn = new TableColumn<>("Professor ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(150);

        TableColumn<Professor, String> nameColumn = new TableColumn<>("Professor Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<Professor, String> deptColumn = new TableColumn<>("Department");
        deptColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        deptColumn.setPrefWidth(200);

        tableView.getColumns().addAll(idColumn, nameColumn, deptColumn);

        // Load professors from the database
        try (Connection conn = Database.connect()) {
            if (conn == null) {
                showAlert("Error", "Could not connect to the database.");
                return;
            }

            LinkedList<Professor> professors = DatabaseHelper.loadProfessors(conn);
            tableView.setItems(FXCollections.observableArrayList(professors));
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while loading professor data: " + e.getMessage());
            e.printStackTrace();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(tableView, 0, 0, 2, 1);

        mainPane.setCenter(grid);
    }

    // delete method
    private void showDeleteProfessorDialog() {
        Label idLabel = new Label("Professor ID:");
        TextField idField = new TextField();
        Button searchButton = new Button("Search");
        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);
        Button cancelButton = new Button("Cancel");

        Label nameLabel = new Label("Professor Name:");
        TextField nameField = new TextField();
        nameField.setEditable(false);

        Label deptLabel = new Label("Department:");
        TextField deptField = new TextField();
        deptField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(deptLabel, 0, 2);
        grid.add(deptField, 1, 2);
        grid.add(deleteButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        mainPane.setCenter(grid);

        // Action for search button
        searchButton.setOnAction(e -> {
            String searchId = idField.getText().trim();

            if (!searchId.isEmpty()) {
                // Query to get professor details
                String query = "SELECT p.professor_id, p.professor_name, d.department_name FROM professor p "
                        + "JOIN department d ON p.dept_id = d.dept_id WHERE p.professor_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, searchId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        // Populate fields with professor details
                        nameField.setText(rs.getString("professor_name"));
                        deptField.setText(rs.getString("department_name")); // Updated to match the new column name
                        deleteButton.setDisable(false);
                    } else {
                        showAlert("Not Found", "No professor found with the given ID.");
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while fetching professor details: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Action for delete button
        deleteButton.setOnAction(e -> {
            String professorId = idField.getText().trim();

            if (!professorId.isEmpty()) {
                // Query to delete the professor
                String deleteQuery = "DELETE FROM professor WHERE professor_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, professorId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Success", "Professor deleted successfully.");
                        mainPane.setCenter(null); // Close the dialog
                    } else {
                        showAlert("Error", "Failed to delete the professor. Please try again.");
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while deleting the professor: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Action for cancel button
        cancelButton.setOnAction(e -> mainPane.setCenter(null));
    }

    /**
     * ******** End Of Professor Methods *********
     */
    /**
     * ******** Start Of Department Methods *********
     */
    private void showAddDepartmentDialog() {
        Label idLabel = new Label("Department ID:");
        TextField idField = new TextField();

        try (Connection conn = Database.connect()) {
            if (conn == null) {
                showAlert("Error", "Could not connect to the database.");
                return;
            }
            int nextDepartmentId = DatabaseHelper.getNextAvailableDepartmentId(conn);
            idField.setText(String.valueOf(nextDepartmentId));
            idField.setEditable(false);
        } catch (SQLException ex) {
            showAlert("Database Error",
                    "An error occurred while generating the next department ID: " + ex.getMessage());
            ex.printStackTrace();
        }

        Label nameLabel = new Label("Department Name:");
        TextField nameField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);

        Button addButton = new Button("ADD");
        Button cancelButton = new Button("Cancel");

        grid.add(addButton, 0, 2);
        grid.add(cancelButton, 1, 2);

        mainPane.setCenter(grid);

        addButton.setOnAction(e -> {
            String departmentId = idField.getText();
            String departmentName = nameField.getText();

            // Input validation
            if (departmentId == null || departmentName == null || departmentId.isEmpty() || departmentName.isEmpty()) {
                showAlert("Input Error", "All fields are required.");
                return;
            }

            Department department = new Department(departmentId, departmentName);

            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                // Insert the new department into the database
                DatabaseHelper.insertDepartment(department);

                showAlert("Success", "Department added successfully!");
                mainPane.setCenter(null);
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while adding the department: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> mainPane.setCenter(null));
    }

    private void showEditDepartmentDialog() {
        Label idLabel = new Label("Department ID:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Department Name:");
        TextField nameField = new TextField();

        Button searchButton = new Button("Search");
        Button saveButton = new Button("Save");
        Button resetButton = new Button("Reset");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(saveButton, 1, 2);
        grid.add(resetButton, 2, 2);

        mainPane.setCenter(grid);

        // Search for Department by ID
        searchButton.setOnAction(e -> {
            String searchId = idField.getText();
            try {
                Connection conn = Database.connect();
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                Department department = DatabaseHelper.getDepartmentById(conn, searchId);
                if (department != null) {
                    idField.setEditable(false);
                    nameField.setText(department.getName());
                } else {
                    showAlert("Not Found", "No department found with the given ID.");
                }

                conn.close();
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while fetching department data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Save changes to department data
        saveButton.setOnAction(e -> {
            try {
                Connection conn = Database.connect();
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                String searchId = idField.getText();
                Department updatedDepartment = new Department(
                        searchId,
                        nameField.getText());

                DatabaseHelper.updateDepartment(conn, updatedDepartment);
                showAlert("Success", "Department updated successfully!");
                mainPane.setCenter(null);

                conn.close();
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while updating department data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Reset all fields
        resetButton.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            idField.setEditable(true);
        });
    }

    @SuppressWarnings("unchecked")
    private void showDisplayDepartmentsDialog() {
        TableView<Department> tableView = new TableView<>();

        TableColumn<Department, String> idColumn = new TableColumn<>("Department ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(150);

        TableColumn<Department, String> nameColumn = new TableColumn<>("Department Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        tableView.getColumns().addAll(idColumn, nameColumn);

        try {
            Connection conn = Database.connect();
            if (conn == null) {
                showAlert("Error", "Could not connect to the database.");
                return;
            }

            tableView.setItems(FXCollections.observableArrayList(DatabaseHelper.loadDepartments(conn)));
            conn.close();
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while loading department data: " + e.getMessage());
            e.printStackTrace();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(tableView, 0, 0, 2, 1);

        mainPane.setCenter(grid);
    }

    //delete
    private void showDeleteDepartmentDialog() {
        Label idLabel = new Label("Department ID:");
        TextField idField = new TextField();
        Button searchButton = new Button("Search");
        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);
        Button cancelButton = new Button("Cancel");

        Label nameLabel = new Label("Department Name:");
        TextField nameField = new TextField();
        nameField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(deleteButton, 0, 2);
        grid.add(cancelButton, 1, 2);

        mainPane.setCenter(grid);

        // Search button action
        searchButton.setOnAction(e -> {
            String searchId = idField.getText().trim();

            if (!searchId.isEmpty()) {
                String searchQuery = "SELECT department_name FROM department WHERE dept_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

                    stmt.setString(1, searchId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String deptName = rs.getString("department_name");
                        nameField.setText(deptName);
                        deleteButton.setDisable(false);
                    } else {
                        showAlert("Not Found", "No department found with the given ID.");
                        nameField.clear();
                        deleteButton.setDisable(true);
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while searching for the Department: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Please enter a valid Department ID.");
            }
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            String departmentId = idField.getText().trim();

            if (!departmentId.isEmpty()) {
                String deleteQuery = "DELETE FROM department WHERE dept_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, departmentId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Success", "Department deleted successfully.");
                        mainPane.setCenter(null); // Close the dialog
                    } else {
                        showAlert("Error", "Failed to delete the Department. Please try again.");
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while deleting the Department: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Cancel button action
        cancelButton.setOnAction(e -> {
            mainPane.setCenter(null);
            idField.clear();
            nameField.clear();
            deleteButton.setDisable(true);
        });
    }

    /**
     * ******** End Of Department Methods *********
     */
    /**
     * *********** New method to handle application exit ***********
     */
    private void exitApplication(Stage stage) {
        DatabaseHelper.saveData(students, courses, enrollments, professors, departments);
        stage.close();
    }

    /**
     * *********** Methods for Student Operations ***********
     */
    private void showAddStudentDialog() {
        Label idLabel = new Label("Student ID:");
        TextField idField = new TextField();
        idField.setEditable(false);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Label streetAddressLabel = new Label("Street Address:");
        TextField streetAddressField = new TextField();

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        cityField.setEditable(false);

        Label stateLabel = new Label("State:");
        TextField stateField = new TextField();
        stateField.setEditable(false);

        Label zipLabel = new Label("Zip Code:");
        TextField zipField = new TextField();
        Button zipSearchButton = new Button("Search ZIP");

        Button addButton = new Button("ADD");
        addButton.setDisable(true); // Initially disabled before zip search
        Button resetButton = new Button("Reset");
        Button cancelButton = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(firstNameLabel, 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(lastNameLabel, 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(streetAddressLabel, 0, 3);
        grid.add(streetAddressField, 1, 3);
        grid.add(cityLabel, 0, 4);
        grid.add(cityField, 1, 4);
        grid.add(stateLabel, 0, 5);
        grid.add(stateField, 1, 5);
        grid.add(zipLabel, 0, 6);
        grid.add(zipField, 1, 6);
        grid.add(zipSearchButton, 2, 6);
        grid.add(addButton, 0, 7);
        grid.add(resetButton, 1, 7);
        grid.add(cancelButton, 2, 7);

        mainPane.setCenter(grid);

        // Flag to track if ZIP code has been successfully searched
        final boolean[] zipCodeSearched = {false};

        // Fetch the next student ID from the database
        try (Connection conn = Database.connect()) {
            if (conn != null) {
                int nextStudentId = DatabaseHelper.getMaxStudentId(conn) + 1;
                idField.setText(String.valueOf(nextStudentId));
            } else {
                showAlert("Error", "Could not connect to the database to fetch student ID.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while fetching the next student ID: " + ex.getMessage());
            ex.printStackTrace();
        }

        // ZIP Search button logic using ZipCodeClient
        zipSearchButton.setOnAction(e -> {
            String zipCode = zipField.getText();
            if (zipCode == null || zipCode.isEmpty()) {
                showAlert("Error", "ZIP code cannot be empty.");
                return;
            }

            // Validate ZIP code format (assuming US ZIP codes)
            if (!zipCode.matches("\\d{5}")) {
                showAlert("Error", "Invalid ZIP code format. Please enter a 5-digit ZIP code.");
                return;
            }

            try {
                ZipCodeClient.ZipCode zipCodeInfo = ZipCodeClient.getZipCodeInfo(zipCode);
                if (zipCodeInfo != null && zipCodeInfo.getPlaces().length > 0) {
                    ZipCodeClient.ZipCode.Place place = zipCodeInfo.getPlaces()[0];
                    cityField.setText(place.getPlaceName());
                    stateField.setText(place.getStateAbbreviation());
                    zipCodeSearched[0] = true; // Mark ZIP code as searched successfully

                    // Enable the "ADD" button AFTER successful ZIP search
                    addButton.setDisable(false);
                } else {
                    showAlert("Error", "Invalid ZIP code or data not found.");
                    zipCodeSearched[0] = false; // Reset flag
                    addButton.setDisable(true); // Disable the "ADD" button
                }
            } catch (IOException ex) {
                showAlert("Error", "Failed to retrieve data for the ZIP code: " + ex.getMessage());
                zipCodeSearched[0] = false; // Reset flag
                addButton.setDisable(true); // Disable the "ADD" button
                ex.printStackTrace();
            }
        });

        // Add student button action
        addButton.setOnAction(e -> {
            if (!zipCodeSearched[0]) {
                showAlert("Error", "Please search for a valid ZIP code before adding the student.");
                return;
            }

            Student student = new Student(
                    idField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    streetAddressField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    zipField.getText());

            try (Connection conn = Database.connect()) {
                if (conn != null) {
                    DatabaseHelper.insertStudent(student);
                    showAlert("Success", "Student added successfully!");
                    mainPane.setCenter(null); // Clear form after adding
                } else {
                    showAlert("Error", "Could not connect to the database to add student.");
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while adding the student: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Reset button action
        resetButton.setOnAction(e -> {
            // Fetch and reset student ID
            try (Connection conn = Database.connect()) {
                if (conn != null) {
                    int nextStudentId = DatabaseHelper.getMaxStudentId(conn) + 1;
                    idField.setText(String.valueOf(nextStudentId));
                } else {
                    showAlert("Error", "Could not connect to the database to reset student ID.");
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while resetting the student ID: " + ex.getMessage());
                ex.printStackTrace();
            }
            firstNameField.clear();
            lastNameField.clear();
            streetAddressField.clear();
            cityField.clear();
            stateField.clear();
            zipField.clear();
            addButton.setDisable(true);
            zipCodeSearched[0] = false; // Reset the ZIP code searched flag
        });

        // Cancel button action
        cancelButton.setOnAction(e -> mainPane.setCenter(null));

        // Listener for ZIP field changes
        zipField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Whenever the ZIP field is changed, mark the ZIP search as invalid
            zipCodeSearched[0] = false;
            addButton.setDisable(true); // Disable the "ADD" button until a valid ZIP is searched again
        });
    }

    private void showEditStudentDialog() {
        Label idLabel = new Label("Student ID:");
        TextField idField = new TextField();

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Label streetAddressLabel = new Label("Street Address:");
        TextField streetAddressField = new TextField();

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        cityField.setEditable(false);

        Label stateLabel = new Label("State:");
        TextField stateField = new TextField();
        stateField.setEditable(false);

        Label zipLabel = new Label("Zip Code:");
        TextField zipField = new TextField();
        Button zipSearchButton = new Button("Search ZIP");

        Button searchButton = new Button("Search Student");
        Button saveButton = new Button("Save");
        saveButton.setDisable(true); // Initially disable the Save button
        Button resetButton = new Button("Reset");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(firstNameLabel, 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(lastNameLabel, 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(streetAddressLabel, 0, 3);
        grid.add(streetAddressField, 1, 3);
        grid.add(cityLabel, 0, 4);
        grid.add(cityField, 1, 4);
        grid.add(stateLabel, 0, 5);
        grid.add(stateField, 1, 5);
        grid.add(zipLabel, 0, 6);
        grid.add(zipField, 1, 6);
        grid.add(zipSearchButton, 2, 6);
        grid.add(saveButton, 1, 7);
        grid.add(resetButton, 2, 7);

        mainPane.setCenter(grid);

        // Flag to track if ZIP code has been successfully searched
        final boolean[] zipCodeSearched = {false};
        // Flag to track if a student has been successfully searched
        final boolean[] studentSearched = {false};

        // Search for ZIP code and populate city and state
        zipSearchButton.setOnAction(e -> {
            String zipCode = zipField.getText();
            if (zipCode == null || zipCode.isEmpty()) {
                showAlert("Error", "ZIP code cannot be empty.");
                return;
            }

            if (!zipCode.matches("\\d{5}")) {
                showAlert("Error", "ZIP code must be 5 numeric digits.");
                return;
            }

            try {
                // Retrieve ZIP code information using ZipCodeClient
                ZipCodeClient.ZipCode zipCodeInfo = ZipCodeClient.getZipCodeInfo(zipCode);
                if (zipCodeInfo != null && zipCodeInfo.getPlaces().length > 0) {
                    // Set city and state fields based on the ZIP code information
                    ZipCodeClient.ZipCode.Place place = zipCodeInfo.getPlaces()[0];
                    cityField.setText(place.getPlaceName());
                    stateField.setText(place.getStateAbbreviation());
                    zipCodeSearched[0] = true; // Mark ZIP code as searched successfully

                    // Enable the Save button if student is already found
                    if (studentSearched[0]) {
                        saveButton.setDisable(false);
                    }
                } else {
                    showAlert("Error", "Invalid ZIP code or data not found.");
                }
            } catch (IOException ex) {
                showAlert("Error", "Failed to retrieve data for the ZIP code: " + ex.getMessage());
            }
        });

        // Search for Student by ID
        searchButton.setOnAction(e -> {
            String searchId = idField.getText();
            if (searchId == null || searchId.isEmpty()) {
                showAlert("Error", "Student ID cannot be empty.");
                return;
            }

            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                // Use the connection to fetch student data
                Student student = DatabaseHelper.getStudentById(conn, searchId);
                if (student != null) {
                    idField.setEditable(false);
                    firstNameField.setText(student.getFirstName());
                    lastNameField.setText(student.getLastName());
                    streetAddressField.setText(student.getStreetAddress());
                    cityField.setText(student.getCity());
                    stateField.setText(student.getState());
                    zipField.setText(student.getZip());

                    studentSearched[0] = true; // Mark student as found
                    zipCodeSearched[0] = true; // Assume ZIP code is valid initially
                    saveButton.setDisable(false); // Enable save button if student is found

                } else {
                    showAlert("Not Found", "No student found with the given ID.");
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while fetching student data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Save changes to student data
        saveButton.setOnAction(e -> {
            String searchId = idField.getText();
            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                Student student = new Student(
                        searchId,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        streetAddressField.getText(),
                        cityField.getText(),
                        stateField.getText(),
                        zipField.getText());

                DatabaseHelper.updateStudent(student);
                showAlert("Success", "Student data updated successfully!");
                mainPane.setCenter(null); // Clear the center pane after saving

            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while updating the student data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Reset all fields
        resetButton.setOnAction(e -> {
            idField.clear();
            firstNameField.clear();
            lastNameField.clear();
            streetAddressField.clear();
            cityField.clear();
            stateField.clear();
            zipField.clear();
            idField.setEditable(true);
            saveButton.setDisable(true); // Disable the Save button after reset
            zipCodeSearched[0] = false; // Reset the ZIP code searched flag
            studentSearched[0] = false; // Reset the student searched flag
        });
    }

    @SuppressWarnings("unchecked")
    private void showDisplayStudentsDialog() {
        Label searchLabel = new Label("Student ID:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");

        TableView<Student> tableView = new TableView<>();

        TableColumn<Student, String> idColumn = new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("stuId"));
        idColumn.setPrefWidth(100);

        TableColumn<Student, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setPrefWidth(100);

        TableColumn<Student, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setPrefWidth(100);

        TableColumn<Student, String> streetAddressColumn = new TableColumn<>("Street Address");
        streetAddressColumn.setCellValueFactory(new PropertyValueFactory<>("streetAddress"));
        streetAddressColumn.setPrefWidth(150);

        TableColumn<Student, String> cityColumn = new TableColumn<>("City");
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        cityColumn.setPrefWidth(100);

        TableColumn<Student, String> stateColumn = new TableColumn<>("State");
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateColumn.setPrefWidth(100);

        TableColumn<Student, String> zipColumn = new TableColumn<>("Zip Code");
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("zip"));
        zipColumn.setPrefWidth(100);

        tableView.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, streetAddressColumn, cityColumn,
                stateColumn, zipColumn);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(searchLabel, 0, 0);
        grid.add(searchField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(tableView, 0, 1, 3, 1);

        searchButton.setOnAction(e -> {
            String searchId = searchField.getText();
            if (searchId == null || searchId.isEmpty()) {
                showAlert("Error", "Student ID cannot be empty.");
                return;
            }

            try {
                // Create a new connection each time we need to access the database
                Connection conn = Database.connect();
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                // Use the connection to fetch student data
                Student student = DatabaseHelper.getStudentById(conn, searchId);

                if (student != null) {
                    // Set the student into the table view
                    tableView.setItems(FXCollections.observableArrayList(student));
                } else {
                    showAlert("Not Found", "No student found with the given ID.");
                }

                // Close the connection after completing the query
                conn.close();
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while fetching student data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        mainPane.setCenter(grid);
    }

    /**
     * *********** (End of student operations section) **************
     */
    /**
     * *********** Methods for Course Operations ***********
     */
    private void showAddCourseDialog() {
        Label idLabel = new Label("Course ID:");
        TextField idField = new TextField();
        idField.setEditable(false);

        Label nameLabel = new Label("Course Name:");
        TextField nameField = new TextField();

        Label descLabel = new Label("Course Description:");
        TextField descField = new TextField();

        Label deptLabel = new Label("Department:");
        ChoiceBox<String> deptChoiceBox = new ChoiceBox<>();

        Label numLabel = new Label("Course Number:");
        TextField numField = new TextField();

        Label professorLabel = new Label("Professor:");
        ChoiceBox<String> professorChoiceBox = new ChoiceBox<>();

        Button saveButton = new Button("Save");
        Button resetButton = new Button("Reset");
        Button cancelButton = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(descLabel, 0, 2);
        grid.add(descField, 1, 2);
        grid.add(deptLabel, 0, 3);
        grid.add(deptChoiceBox, 1, 3);
        grid.add(numLabel, 0, 4);
        grid.add(numField, 1, 4);
        grid.add(professorLabel, 0, 5);
        grid.add(professorChoiceBox, 1, 5);
        grid.add(saveButton, 1, 6);
        grid.add(resetButton, 2, 6);
        grid.add(cancelButton, 3, 6);

        mainPane.setCenter(grid);

        // Fetch the next available Course ID from the database
        try (Connection conn = Database.connect()) {
            if (conn != null) {
                int nextCourseId = DatabaseHelper.getNextAvailableCourseId(conn);
                idField.setText(String.valueOf(nextCourseId));

                // Load departments
                LinkedList<Department> departmentList = DatabaseHelper.loadDepartments(conn);
                deptChoiceBox.setItems(FXCollections.observableArrayList(
                        departmentList.stream().map(Department::getName).collect(Collectors.toList())
                ));
            } else {
                showAlert("Error", "Could not connect to the database to fetch course ID.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while fetching the next course ID or loading departments: " + ex.getMessage());
            ex.printStackTrace();
        }

        // Listener to update professors based on the selected department
        deptChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try (Connection conn = Database.connect()) {
                    List<Professor> professorsInDept = DatabaseHelper.getProfessorsByDepartmentName(conn, newValue);
                    professorChoiceBox.setItems(FXCollections.observableArrayList(
                            professorsInDept.stream().map(Professor::getName).collect(Collectors.toList())
                    ));
                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while fetching professors: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                professorChoiceBox.getItems().clear();
            }
        });

        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || descField.getText().isEmpty()
                    || deptChoiceBox.getValue() == null || numField.getText().isEmpty() || professorChoiceBox.getValue() == null) {
                showAlert("Error", "Please fill all the fields before saving.");
                return;
            }

            String deptName = deptChoiceBox.getValue();
            String professorName = professorChoiceBox.getValue();

            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                Department department = DatabaseHelper.getDepartmentByName(conn, deptName);
                Professor professor = DatabaseHelper.getProfessorByName(conn, professorName);

                if (department == null || professor == null) {
                    showAlert("Error", "Could not find matching department or professor in the database.");
                    return;
                }

                Course newCourse = new Course(
                        idField.getText(),
                        nameField.getText(),
                        descField.getText(),
                        department.getId(),
                        numField.getText(),
                        professor.getId()
                );

                DatabaseHelper.insertOrUpdateCourse(newCourse);
                showAlert("Success", "Course added successfully!");
                mainPane.setCenter(null);
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while adding the course: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        resetButton.setOnAction(e -> {
            nameField.clear();
            descField.clear();
            deptChoiceBox.setValue(null);
            numField.clear();
            professorChoiceBox.setValue(null);
        });

        cancelButton.setOnAction(e -> mainPane.setCenter(null));
    }

    private void showEditCourseDialog() {
        Label idLabel = new Label("Course ID:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Course Name:");
        TextField nameField = new TextField();

        Label descLabel = new Label("Course Description:");
        TextField descField = new TextField();

        Label deptLabel = new Label("Department:");
        ChoiceBox<String> deptChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                departments.stream().map(Department::getName).collect(Collectors.toList())));

        Label numLabel = new Label("Course Number:");
        TextField numField = new TextField();

        Label professorLabel = new Label("Professor:");
        ChoiceBox<String> professorChoiceBox = new ChoiceBox<>();

        Button searchButton = new Button("Search");
        Button saveButton = new Button("Save");
        Button resetButton = new Button("Reset");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(descLabel, 0, 2);
        grid.add(descField, 1, 2);
        grid.add(deptLabel, 0, 3);
        grid.add(deptChoiceBox, 1, 3);
        grid.add(numLabel, 0, 4);
        grid.add(numField, 1, 4);
        grid.add(professorLabel, 0, 5);
        grid.add(professorChoiceBox, 1, 5);
        grid.add(saveButton, 1, 6);
        grid.add(resetButton, 2, 6);

        mainPane.setCenter(grid);

        searchButton.setOnAction(e -> {
            String searchId = idField.getText();
            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }
                Course course = DatabaseHelper.getCourseById(conn, searchId);
                if (course != null) {
                    idField.setEditable(false);
                    nameField.setText(course.getName());
                    descField.setText(course.getDesc());

                    // Set Department ChoiceBox value
                    Department department = DatabaseHelper.getDepartmentById(conn, course.getDept());
                    if (department != null) {
                        String departmentName = department.getName();
                        if (!deptChoiceBox.getItems().contains(departmentName)) {
                            deptChoiceBox.getItems().add(departmentName);
                        }
                        deptChoiceBox.setValue(departmentName);
                    }

                    // Update professor list based on department and set Professor
                    List<String> professorsInDept = DatabaseHelper.getProfessorsByDepartmentId(conn, course.getDept())
                            .stream()
                            .map(Professor::getName)
                            .collect(Collectors.toList());
                    professorChoiceBox.setItems(FXCollections.observableArrayList(professorsInDept));

                    Professor professor = DatabaseHelper.getProfessorById(conn, course.getProfessor());
                    if (professor != null) {
                        professorChoiceBox.setValue(professor.getName());
                    }

                    numField.setText(course.getNum());
                } else {
                    showAlert("Not Found", "No course found with the given ID.");
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while fetching course data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Listener to update professor list when a department is selected
        deptChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try (Connection conn = Database.connect()) {
                    List<String> professorsInDept = DatabaseHelper.getProfessorsByDepartmentName(conn, newValue)
                            .stream()
                            .map(Professor::getName)
                            .collect(Collectors.toList());
                    professorChoiceBox.setItems(FXCollections.observableArrayList(professorsInDept));
                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while fetching professors: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                professorChoiceBox.getItems().clear();
            }
        });

        saveButton.setOnAction(e -> {
            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Error", "Could not connect to the database.");
                    return;
                }

                String searchId = idField.getText();
                String deptName = deptChoiceBox.getValue();
                String professorName = professorChoiceBox.getValue();

                Department department = DatabaseHelper.getDepartmentByName(conn, deptName);
                Professor professor = DatabaseHelper.getProfessorByName(conn, professorName);

                if (department == null || professor == null) {
                    showAlert("Error", "Could not find matching department or professor in the database.");
                    return;
                }

                Course updatedCourse = new Course(
                        searchId,
                        nameField.getText(),
                        descField.getText(),
                        department.getId(),
                        numField.getText(),
                        professor.getId());

                DatabaseHelper.insertOrUpdateCourse(updatedCourse);
                showAlert("Success", "Course updated successfully!");
                mainPane.setCenter(null);
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while updating course data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        resetButton.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            descField.clear();
            deptChoiceBox.setValue(null);
            numField.clear();
            professorChoiceBox.setValue(null);
            idField.setEditable(true);
        });
    }

    @SuppressWarnings("unchecked")
    private void showDisplayCoursesDialog() {
        Label searchLabel = new Label("Course ID:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");

        TableView<Course> tableView = new TableView<>();

        TableColumn<Course, String> idColumn = new TableColumn<>("Course ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(130);

        TableColumn<Course, String> descColumn = new TableColumn<>("Course Description");
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        descColumn.setPrefWidth(150);

        TableColumn<Course, String> deptColumn = new TableColumn<>("Department");
        deptColumn.setCellValueFactory(course -> {
            String deptId = course.getValue().getDept();
            String deptName = departments.stream()
                    .filter(dept -> dept.getId().equals(deptId))
                    .map(Department::getName)
                    .findFirst()
                    .orElse("Unknown");
            return new SimpleStringProperty(deptName);
        });
        deptColumn.setPrefWidth(120);

        TableColumn<Course, String> numColumn = new TableColumn<>("Course Number");
        numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));
        numColumn.setPrefWidth(120);

        TableColumn<Course, String> professorColumn = new TableColumn<>("Professor");
        professorColumn.setCellValueFactory(course -> {
            String professorId = course.getValue().getProfessor();
            String professorName = professors.stream()
                    .filter(prof -> prof.getId().equals(professorId))
                    .map(Professor::getName)
                    .findFirst()
                    .orElse("Unknown");
            return new SimpleStringProperty(professorName);
        });
        professorColumn.setPrefWidth(120);

        tableView.getColumns().addAll(idColumn, nameColumn, descColumn, deptColumn, numColumn, professorColumn);

        // Do not load the courses initially
        // Only load when the search button is clicked
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(searchLabel, 0, 0);
        grid.add(searchField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(tableView, 0, 1, 3, 1);

        searchButton.setOnAction(e -> {
            String searchId = searchField.getText();

            try (Connection conn = Database.connect()) {
                if (conn == null) {
                    showAlert("Database Error", "Could not connect to the database.");
                    return;
                }

                Course course = DatabaseHelper.getCourseById(conn, searchId);
                List<Course> filteredCourses = new ArrayList<>();

                if (course != null) {
                    filteredCourses.add(course);
                }

                tableView.setItems(FXCollections.observableArrayList(filteredCourses));
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while searching course data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        mainPane.setCenter(grid);
    }

    @SuppressWarnings("unchecked")
    private void showAvailableCoursesDialog() {
        TableView<Course> tableView = new TableView<>();

        TableColumn<Course, String> idColumn = new TableColumn<>("Course ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(130);

        TableColumn<Course, String> descColumn = new TableColumn<>("Course Description");
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        descColumn.setPrefWidth(150);

        TableColumn<Course, String> deptColumn = new TableColumn<>("Department");
        deptColumn.setCellValueFactory(new PropertyValueFactory<>("dept"));
        deptColumn.setPrefWidth(120);

        TableColumn<Course, String> numColumn = new TableColumn<>("Course #");
        numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));
        numColumn.setPrefWidth(80);

        TableColumn<Course, String> professorColumn = new TableColumn<>("Professor");
        professorColumn.setCellValueFactory(new PropertyValueFactory<>("professor"));
        professorColumn.setPrefWidth(120);

        tableView.getColumns().addAll(idColumn, nameColumn, descColumn, deptColumn, numColumn, professorColumn);

        // Set all courses in the table view
        List<Course> allCourses = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            allCourses.add(courses.get(i));
        }
        tableView.setItems(FXCollections.observableArrayList(allCourses));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(tableView, 0, 0, 3, 1);

        mainPane.setCenter(grid);

        try (Connection conn = Database.connect()) {
            if (conn == null) {
                showAlert("Error", "Could not connect to the database.");
                return;
            }
            List<Course> coursesList = DatabaseHelper.loadCourses(conn);
            tableView.setItems(FXCollections.observableArrayList(coursesList));
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while loading course data: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * *********** (End of course operations section) **************
     */
    /**
     * *********** Methods for Enrollment Operations ***********
     */
    private void showAddEnrollmentDialog() {
        // Add label and text field for Enrollment ID, and set it to non-editable
        Label enrollmentIdLabel = new Label("Enrollment ID:");
        TextField enrollmentIdField = new TextField();
        enrollmentIdField.setEditable(false); // Non-editable field

        Label studentIdLabel = new Label("Student ID:");
        TextField studentIdField = new TextField();
        Button searchButton = new Button("Search");

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setEditable(false);

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setEditable(false);

        Label courseLabel = new Label("Course:");
        ChoiceBox<Course> courseChoiceBox = new ChoiceBox<>();

        Label yearLabel = new Label("Year:");
        ChoiceBox<String> yearChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"));

        Label semesterLabel = new Label("Semester:");
        ChoiceBox<String> semesterChoiceBox = new ChoiceBox<>(
                FXCollections.observableArrayList("Fall", "Spring", "Summer", "Winter"));

        // Create the grid layout for form elements
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(enrollmentIdLabel, 0, 0);
        grid.add(enrollmentIdField, 1, 0);
        grid.add(studentIdLabel, 0, 1);
        grid.add(studentIdField, 1, 1);
        grid.add(searchButton, 2, 1);
        grid.add(firstNameLabel, 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(lastNameLabel, 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(courseLabel, 0, 4);
        grid.add(courseChoiceBox, 1, 4);
        grid.add(yearLabel, 0, 5);
        grid.add(yearChoiceBox, 1, 5);
        grid.add(semesterLabel, 0, 6);
        grid.add(semesterChoiceBox, 1, 6);

        Button addButton = new Button("Create Enrollment");
        addButton.setDisable(true); // Initially disable until student is searched
        Button resetButton = new Button("Reset");
        Button cancelButton = new Button("Cancel");

        grid.add(addButton, 0, 7);
        grid.add(resetButton, 1, 7);
        grid.add(cancelButton, 2, 7);

        mainPane.setCenter(grid);

        // Fetch the next available Enrollment ID from the database
        try (Connection conn = Database.connect()) {
            if (conn != null) {
                int nextEnrollmentId = DatabaseHelper.getNextAvailableEnrollmentId(conn);
                enrollmentIdField.setText(String.valueOf(nextEnrollmentId));

                // Load courses from the database
                LinkedList<Course> courseList = DatabaseHelper.loadCourses(conn);
                courseChoiceBox.setItems(FXCollections.observableArrayList(courseList));
            } else {
                showAlert("Error", "Could not connect to the database to fetch enrollment ID or courses.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while fetching the next enrollment ID or loading courses: " + ex.getMessage());
            ex.printStackTrace();
        }

        // Search Button Action to populate First Name and Last Name fields
        searchButton.setOnAction(e -> {
            String studentId = studentIdField.getText();
            boolean studentFound = false;

            try (Connection conn = Database.connect()) {
                Student student = DatabaseHelper.getStudentById(conn, studentId);
                if (student != null) {
                    firstNameField.setText(student.getFirstName());
                    lastNameField.setText(student.getLastName());
                    studentIdField.setEditable(false); // Make student ID non-editable after search
                    addButton.setDisable(false); // Enable the add button after a valid search
                    studentFound = true;
                }

                if (!studentFound) {
                    showAlert("Student Not Found", "No student found with the given ID.");
                    firstNameField.clear();
                    lastNameField.clear();
                }
            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while searching for the student: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Listener for Student ID field changes to disable the add button
        studentIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            studentIdField.setEditable(true); // Allow user to change the student ID
            addButton.setDisable(true); // Disable the add button when the student ID field is modified
        });

        addButton.setOnAction(e -> {
            Course selectedCourse = courseChoiceBox.getValue();
            String studentId = studentIdField.getText();
            String year = yearChoiceBox.getValue();
            String semester = semesterChoiceBox.getValue();

            // Only proceed if student information is filled
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                showAlert("Missing Student Information",
                        "Please search for a valid student ID before adding the enrollment.");
                return;
            }

            if (selectedCourse == null) {
                showAlert("Missing Course Information", "Please select a course before adding the enrollment.");
                return;
            }

            Enrollment enrollment = new Enrollment(
                    enrollmentIdField.getText(),
                    studentId,
                    selectedCourse.getId(),
                    selectedCourse.getName(),
                    year,
                    semester,
                    "N/A",
                    firstNameField.getText(),
                    lastNameField.getText());

            boolean isDuplicate = false;
            for (Enrollment existingEnrollment : enrollments) {
                if (existingEnrollment.getEnrollmentID().equals(enrollment.getEnrollmentID())) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                enrollments.add(enrollment);
                try (Connection conn = Database.connect()) {
                    if (conn != null) {
                        DatabaseHelper.insertEnrollment(conn, enrollment); // Save to the database
                        mainPane.setCenter(null); // Clear the form after adding
                    } else {
                        showAlert("Error", "Could not connect to the database to save enrollment.");
                    }
                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while saving the enrollment: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showAlert("Duplicate Enrollment ID", "An enrollment with this ID already exists.");
            }
        });

        // Reset button action to allow re-entry of information
        resetButton.setOnAction(e -> {
            studentIdField.clear();
            firstNameField.clear();
            lastNameField.clear();
            studentIdField.setEditable(true); // Make student ID editable again
            addButton.setDisable(true); // Disable the add button until a valid student is searched again
        });

        cancelButton.setOnAction(e -> mainPane.setCenter(null));
    }

@SuppressWarnings("unchecked")
private void showDisplayEnrollmentsDialog() {
    TableView<Enrollment> tableView = new TableView<>();

    // Define the columns for the TableView
    TableColumn<Enrollment, String> idColumn = new TableColumn<>("Enrollment ID");
    idColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentID"));
    idColumn.setPrefWidth(120);

    TableColumn<Enrollment, String> studentIdColumn = new TableColumn<>("Student ID");
    studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledStudentID"));
    studentIdColumn.setPrefWidth(120);

    TableColumn<Enrollment, String> courseIdColumn = new TableColumn<>("Course ID");
    courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledCourseID"));
    courseIdColumn.setPrefWidth(120);

    TableColumn<Enrollment, String> courseNameColumn = new TableColumn<>("Course Name");
    courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
    courseNameColumn.setPrefWidth(200);

    TableColumn<Enrollment, String> yearColumn = new TableColumn<>("Enrollment Year");
    yearColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
    yearColumn.setPrefWidth(180);

    TableColumn<Enrollment, String> semesterColumn = new TableColumn<>("Semester");
    semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

    TableColumn<Enrollment, String> gradeColumn = new TableColumn<>("Grade");
    gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

    // Add all columns to the table view
    tableView.getColumns().addAll(idColumn, studentIdColumn, courseIdColumn, courseNameColumn, yearColumn, semesterColumn, gradeColumn);

    // Create the search layout
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    Label studentIdLabel = new Label("Student ID:");
    TextField studentIdField = new TextField();
    Button populateButton = new Button("Populate");

    Label courseIdLabel = new Label("Course ID:");
    TextField courseIdField = new TextField();
    Button dropButton = new Button("Drop Course");

    Button gradeButton = new Button("Grade Enrollment");

    grid.add(studentIdLabel, 0, 0);
    grid.add(studentIdField, 1, 0);
    grid.add(populateButton, 2, 0);
    grid.add(courseIdLabel, 3, 0);
    grid.add(courseIdField, 4, 0);
    grid.add(dropButton, 5, 0);
    grid.add(gradeButton, 6, 0);
    grid.add(tableView, 0, 1, 7, 1);

    // Set the main pane to center the grid
    mainPane.setCenter(grid);

    // Event handler for the "Populate" button
    populateButton.setOnAction(e -> {
        String studentId = studentIdField.getText().trim();

        // Clear previous search results
        tableView.getItems().clear();

        // Fetch enrollments from the database for the given Student ID
        String query = "SELECT e.enrollment_id AS enrollmentID, e.student_id AS enrolledStudentID, e.course_id AS enrolledCourseID, "
                + "c.course_name AS courseName, e.enrollment_year AS enrollmentYear, e.semester, e.grade "
                + "FROM enrollment e "
                + "JOIN course c ON e.course_id = c.course_id "
                + "WHERE e.student_id = ?";

        try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            List<Enrollment> filteredEnrollments = new ArrayList<>();

            // Process the result set
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentID(rs.getString("enrollmentID"));
                enrollment.setEnrolledStudentID(rs.getString("enrolledStudentID"));
                enrollment.setEnrolledCourseID(rs.getString("enrolledCourseID"));
                enrollment.setCourseName(rs.getString("courseName")); // Set course name
                enrollment.setEnrollmentYear(rs.getString("enrollmentYear"));
                enrollment.setSemester(rs.getString("semester"));
                enrollment.setGrade(rs.getString("grade"));

                filteredEnrollments.add(enrollment);
            }

            // Set the items to the table view
            tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));

        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while fetching enrollments: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    // Event handler for the "Drop Course" button
    dropButton.setOnAction(e -> {
        String courseId = courseIdField.getText().trim();
        String studentId = studentIdField.getText().trim();

        if (courseId.isEmpty() || studentId.isEmpty()) {
            showAlert("Input Error", "Please enter both Student ID and Course ID to drop the course.");
            return;
        }

        // Find the enrollment to drop
        Enrollment enrollmentToDrop = null;
        for (Enrollment enrollment : tableView.getItems()) {
            if (enrollment.getEnrolledCourseID().equals(courseId) && enrollment.getEnrolledStudentID().equals(studentId)) {
                enrollmentToDrop = enrollment;
                break;
            }
        }

        if (enrollmentToDrop == null) {
            showAlert("Input Error", "No matching enrollment found for the given Course ID and Student ID.");
            return;
        }

        // Remove the enrollment from the database
        String deleteQuery = "DELETE FROM enrollment WHERE enrollment_id = ?";

        try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            if (conn == null) {
                showAlert("Database Error", "Could not connect to the database.");
                return;
            }

            stmt.setString(1, enrollmentToDrop.getEnrollmentID());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                tableView.getItems().remove(enrollmentToDrop);
                showAlert("Success", "Course dropped successfully.");
            } else {
                showAlert("Error", "Failed to drop the course. Please try again.");
            }

        } catch (SQLException ex) {
            showAlert("Database Error", "An error occurred while dropping the course: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    // Event handler for the "Grade Enrollment" button
    gradeButton.setOnAction(e -> {
        Enrollment selectedEnrollment = tableView.getSelectionModel().getSelectedItem();

        if (selectedEnrollment == null) {
            showAlert("Input Error", "Please select an enrollment to grade.");
            return;
        }

        // Create a custom dialog for grade selection
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Grade Enrollment");
        dialog.setHeaderText("Select a grade for the selected enrollment:");

        // Set the dialog buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create a ChoiceBox for grade selection
        ChoiceBox<String> gradeChoiceBox = new ChoiceBox<>();
        gradeChoiceBox.getItems().addAll("A", "B", "C", "D", "F");
        gradeChoiceBox.setValue("A"); // Set default value

        // Add the ChoiceBox to the dialog pane
        GridPane gradeGrid = new GridPane();
        gradeGrid.setHgap(10);
        gradeGrid.setVgap(10);
        gradeGrid.setPadding(new Insets(20, 150, 10, 10));
        gradeGrid.add(new Label("Grade:"), 0, 0);
        gradeGrid.add(gradeChoiceBox, 1, 0);
        dialog.getDialogPane().setContent(gradeGrid);

        // Handle the result of the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return gradeChoiceBox.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(grade -> {
            // Update the grade in the database
            String updateQuery = "UPDATE enrollment SET grade = ? WHERE enrollment_id = ?";

            try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                stmt.setString(1, grade);
                stmt.setString(2, selectedEnrollment.getEnrollmentID());
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    // Update the grade in the TableView
                    selectedEnrollment.setGrade(grade);
                    tableView.refresh(); // Refresh the table to show the updated grade
                    showAlert("Success", "Grade updated successfully.");
                } else {
                    showAlert("Error", "Failed to update the grade. Please try again.");
                }

            } catch (SQLException ex) {
                showAlert("Database Error", "An error occurred while updating the grade: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    });
}


    /**
     * *********** (End of enrollment operations section) **************
     */
    /**
     * *********** Methods for Grade Management ***********
     */
    @SuppressWarnings("unchecked")
    private void showManageGradesDialog() {
        // Set up the grid layout for input
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create labels and text fields for student ID
        Label studentIdLabel = new Label("Student ID:");
        TextField studentIdField = new TextField();
        studentIdField.setMaxWidth(200); // setMaxWidth to make the input box shorter

        // Create buttons
        Button searchButton = new Button("Search");
        Button cancelButton = new Button("Cancel");

        // Add components to the grid
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(searchButton, 0, 1);
        grid.add(cancelButton, 1, 1);

        // Set up the table view for displaying enrollments
        TableView<Enrollment> tableView = new TableView<>();
        tableView.setPrefHeight(200); // Limit the table height

        TableColumn<Enrollment, String> enrollmentIdColumn = new TableColumn<>("Enrollment ID");
        enrollmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentID"));
        enrollmentIdColumn.setPrefWidth(120);

        TableColumn<Enrollment, String> studentIdColumn = new TableColumn<>("Student ID");
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledStudentID"));
        studentIdColumn.setPrefWidth(120);

        TableColumn<Enrollment, String> courseIdColumn = new TableColumn<>("Course ID");
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledCourseID"));
        courseIdColumn.setPrefWidth(120);

        TableColumn<Enrollment, String> courseNameColumn = new TableColumn<>("Course Name");
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseNameColumn.setPrefWidth(130);

        TableColumn<Enrollment, String> yearColumn = new TableColumn<>("Enrollment Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
        yearColumn.setPrefWidth(180);

        TableColumn<Enrollment, String> semesterColumn = new TableColumn<>("Semester");
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

        TableColumn<Enrollment, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        TableColumn<Enrollment, String> studentFirstNameColumn = new TableColumn<>("First Name");
        studentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentFirstName"));
        studentFirstNameColumn.setPrefWidth(130);

        TableColumn<Enrollment, String> studentLastNameColumn = new TableColumn<>("Last Name");
        studentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentLastName"));
        studentLastNameColumn.setPrefWidth(130);

        tableView.getColumns().addAll(enrollmentIdColumn, studentIdColumn, studentFirstNameColumn,
                studentLastNameColumn, courseIdColumn, courseNameColumn, yearColumn, semesterColumn, gradeColumn);

        // Add button to set grade
        Button setGradeButton = new Button("Set Grade");
        setGradeButton.setDisable(true); // Initially disable

        // Add listener to enable grade editing when a row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setGradeButton.setDisable(false); // Enable button when an item is selected
            } else {
                setGradeButton.setDisable(true); // Disable button when no item is selected
            }
        });

        // Set the action for the search button
        searchButton.setOnAction(e -> {
            String studentId = studentIdField.getText().trim();
            List<Enrollment> filteredEnrollments = new ArrayList<>();

            if (!studentId.isEmpty()) {
                // Filter enrollments by Student ID
                for (Enrollment enrollment : enrollments) {
                    if (enrollment.getEnrolledStudentID().equals(studentId)) {
                        // Find the course name for each enrollment
                        for (Course course : courses) {
                            if (course.getId().equals(enrollment.getEnrolledCourseID())) {
                                enrollment.setCourseName(course.getName());
                                break;
                            }
                        }
                        filteredEnrollments.add(enrollment);
                    }
                }
            }

            // Update the table view with the filtered enrollments
            tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));
        });

        // Set the action for the set grade button
        setGradeButton.setOnAction(e -> {
            Enrollment selectedEnrollment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEnrollment != null) {
                showSetGradeDialog(selectedEnrollment);
                tableView.refresh(); // Refresh table to reflect grade change
            }
        });

        // Set the action for the cancel button
        cancelButton.setOnAction(e -> mainPane.setCenter(null));

        // Add the table view and set grade button to the grid
        grid.add(tableView, 0, 2, 2, 1);
        grid.add(setGradeButton, 0, 3);

        mainPane.setCenter(grid);
    }

    @SuppressWarnings("unchecked")
    private void showManageGradesDialogByStudent() {
        currentManageContext = "student"; // Set the context
        // Set up the grid layout for input
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create labels and text fields for student ID
        Label studentIdLabel = new Label("Student ID:");
        TextField studentIdField = new TextField();
        studentIdField.setMaxWidth(200);

        // Create buttons
        Button searchButton = new Button("Search");
        Button cancelButton = new Button("Cancel");

        // Add components to the grid
        grid.add(studentIdLabel, 0, 0);
        grid.add(studentIdField, 1, 0);
        grid.add(searchButton, 0, 1);
        grid.add(cancelButton, 1, 1);

        // Set up the table view for displaying enrollments
        TableView<Enrollment> tableView = new TableView<>();
        tableView.setPrefHeight(200);

        // Define columns for the TableView
        TableColumn<Enrollment, String> idColumn = new TableColumn<>("Enrollment ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentID"));
        idColumn.setPrefWidth(90); // Adjust width

        TableColumn<Enrollment, String> studentIdColumn = new TableColumn<>("Student ID");
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledStudentID"));
        studentIdColumn.setPrefWidth(80); // Adjust width

        TableColumn<Enrollment, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentFirstName"));
        firstNameColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentLastName"));
        lastNameColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> courseIdColumn = new TableColumn<>("Course ID");
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledCourseID"));
        courseIdColumn.setPrefWidth(80); // Adjust width

        TableColumn<Enrollment, String> courseNameColumn = new TableColumn<>("Course Name");
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseNameColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> yearColumn = new TableColumn<>("Enrollment Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
        yearColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> semesterColumn = new TableColumn<>("Semester");
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        semesterColumn.setPrefWidth(70); // Adjust width

        TableColumn<Enrollment, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeColumn.setPrefWidth(50); // Adjust width

        tableView.getColumns().addAll(idColumn, studentIdColumn, firstNameColumn, lastNameColumn, courseIdColumn,
                courseNameColumn, yearColumn, semesterColumn, gradeColumn);

        // Add button to set grade
        Button setGradeButton = new Button("Set Grade");
        setGradeButton.setDisable(true);

        // Enable the set grade button only if a row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setGradeButton.setDisable(false);
            } else {
                setGradeButton.setDisable(true);
            }
        });

        // Action for search button
        searchButton.setOnAction(e -> {
            String studentId = studentIdField.getText().trim();
            List<Enrollment> filteredEnrollments = new ArrayList<>();

            if (!studentId.isEmpty()) {
                // Filter enrollments by Student ID
                for (Enrollment enrollment : enrollments) {
                    if (enrollment.getEnrolledStudentID().equals(studentId)) {

                        // Find the student's first and last names for each enrollment
                        for (Student student : students) {
                            if (student.getStuId().equals(enrollment.getEnrolledStudentID())) {
                                enrollment.setStudentFirstName(student.getFirstName());
                                enrollment.setStudentLastName(student.getLastName());
                                break;
                            }
                        }

                        filteredEnrollments.add(enrollment);
                    }
                }
            }

            // Update the TableView with the filtered enrollments
            tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));
        });

        // Action for set grade button
        setGradeButton.setOnAction(e -> {
            Enrollment selectedEnrollment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEnrollment != null) {
                showSetGradeDialog(selectedEnrollment);
                tableView.refresh(); // Refresh table to reflect grade change
                DatabaseHelper.saveData(students, courses, enrollments, professors, departments);
            }
        });

        // Action for cancel button
        cancelButton.setOnAction(e -> mainPane.setCenter(null));

        // Add table and set grade button to the grid
        grid.add(tableView, 0, 2, 2, 1);
        grid.add(setGradeButton, 0, 3);

        mainPane.setCenter(grid);
    }

    @SuppressWarnings("unchecked")
    private void showManageGradesDialogByCourse() {
        currentManageContext = "course"; // Set the context

        // Set up the grid layout for input
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create labels and text fields for course ID
        Label courseIdLabel = new Label("Course ID:");
        TextField courseIdField = new TextField();
        courseIdField.setMaxWidth(200);

        // Create buttons
        Button searchButton = new Button("Search");
        Button cancelButton = new Button("Cancel");

        // Add components to the grid
        grid.add(courseIdLabel, 0, 0);
        grid.add(courseIdField, 1, 0);
        grid.add(searchButton, 0, 1);
        grid.add(cancelButton, 1, 1);

        // Set up the table view for displaying enrollments
        TableView<Enrollment> tableView = new TableView<>();
        tableView.setPrefHeight(200);

        // Define columns for the TableView
        TableColumn<Enrollment, String> idColumn = new TableColumn<>("Enrollment ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentID"));
        idColumn.setPrefWidth(90); // Adjust width

        TableColumn<Enrollment, String> studentIdColumn = new TableColumn<>("Student ID");
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledStudentID"));
        studentIdColumn.setPrefWidth(80); // Adjust width

        TableColumn<Enrollment, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentFirstName"));
        firstNameColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentLastName"));
        lastNameColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> courseIdColumn = new TableColumn<>("Course ID");
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledCourseID"));
        courseIdColumn.setPrefWidth(80); // Adjust width

        TableColumn<Enrollment, String> courseNameColumn = new TableColumn<>("Course Name");
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseNameColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> yearColumn = new TableColumn<>("Enrollment Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
        yearColumn.setPrefWidth(100); // Adjust width

        TableColumn<Enrollment, String> semesterColumn = new TableColumn<>("Semester");
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        semesterColumn.setPrefWidth(70); // Adjust width

        TableColumn<Enrollment, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeColumn.setPrefWidth(50); // Adjust width

        tableView.getColumns().addAll(idColumn, studentIdColumn, firstNameColumn, lastNameColumn, courseIdColumn,
                courseNameColumn, yearColumn, semesterColumn, gradeColumn);

        // Add button to set grade
        Button setGradeButton = new Button("Set Grade");
        setGradeButton.setDisable(true);

        // Enable the set grade button only if a row is selected
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setGradeButton.setDisable(false);
            } else {
                setGradeButton.setDisable(true);
            }
        });

        // Action for search button
        searchButton.setOnAction(e -> {
            String courseId = courseIdField.getText().trim();
            List<Enrollment> filteredEnrollments = new ArrayList<>();

            if (!courseId.isEmpty()) {
                // Filter enrollments by Course ID
                for (Enrollment enrollment : enrollments) {
                    if (enrollment.getEnrolledCourseID().equals(courseId)) {

                        // Find the student's first and last names for each enrollment
                        for (Student student : students) {
                            if (student.getStuId().equals(enrollment.getEnrolledStudentID())) {
                                enrollment.setStudentFirstName(student.getFirstName());
                                enrollment.setStudentLastName(student.getLastName());
                                break;
                            }
                        }

                        filteredEnrollments.add(enrollment);
                    }
                }
            }

            // Update the TableView with the filtered enrollments
            tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));
        });

        // Action for set grade button
        setGradeButton.setOnAction(e -> {
            Enrollment selectedEnrollment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEnrollment != null) {
                showSetGradeDialog(selectedEnrollment);
                tableView.refresh(); // Refresh table to reflect grade change
                DatabaseHelper.saveData(students, courses, enrollments, professors, departments);
            }
        });

        // Action for cancel button
        cancelButton.setOnAction(e -> mainPane.setCenter(null));

        // Add table and set grade button to the grid
        grid.add(tableView, 0, 2, 2, 1);
        grid.add(setGradeButton, 0, 3);

        mainPane.setCenter(grid);
    }

    @SuppressWarnings("unchecked")
    private void showSetGradeDialog(Enrollment enrollment) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label gradeLabel = new Label("Grade:");
        ChoiceBox<String> gradeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("A", "B", "C", "D", "F"));
        gradeChoiceBox.setValue(enrollment.getGrade());

        grid.add(gradeLabel, 0, 0);
        grid.add(gradeChoiceBox, 1, 0);

        Button setButton = new Button("Set");
        Button cancelButton = new Button("Cancel");

        grid.add(setButton, 0, 1);
        grid.add(cancelButton, 1, 1);

        mainPane.setCenter(grid);

        setButton.setOnAction(e -> {
            String grade = gradeChoiceBox.getValue();
            enrollment.setGrade(grade);
            DatabaseHelper.saveData(students, courses, enrollments, professors, departments);

            // Automatically return to the appropriate grades view
            if (currentManageContext.equals("student")) {
                String studentId = enrollment.getEnrolledStudentID(); // Get the current student ID
                showManageGradesDialogByStudent(); // Show the student view

                // Directly filter the enrollments to show the same student
                List<Enrollment> filteredEnrollments = enrollments.stream()
                        .filter(enr -> enr.getEnrolledStudentID().equals(studentId))
                        .collect(Collectors.toList());

                TableView<Enrollment> tableView = (TableView<Enrollment>) mainPane.lookup(".table-view");
                if (tableView != null) {
                    tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));
                }
            } else if (currentManageContext.equals("course")) {
                String courseId = enrollment.getEnrolledCourseID(); // Get the current course ID
                showManageGradesDialogByCourse(); // Show the course view

                // Directly filter the enrollments to show the same course
                List<Enrollment> filteredEnrollments = enrollments.stream()
                        .filter(enr -> enr.getEnrolledCourseID().equals(courseId))
                        .collect(Collectors.toList());

                TableView<Enrollment> tableView = (TableView<Enrollment>) mainPane.lookup(".table-view");
                if (tableView != null) {
                    tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));
                }
            }
        });

        cancelButton.setOnAction(e -> mainPane.setCenter(null));
    }

    @SuppressWarnings("unchecked")
    private void showGenerateReportDialog() {
        // Fetch enrollments from the database
        enrollments = DatabaseHelper.loadEnrollments(Database.connect()); // Assuming loadEnrollments() is implemented in DatabaseHelper
    
        TableView<Enrollment> tableView = new TableView<>();
    
        // Constrain resizing to prevent the cutoff
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
        // Define Columns
        TableColumn<Enrollment, String> idColumn = new TableColumn<>("Enrollment ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentID"));
        idColumn.setMinWidth(100);
    
        TableColumn<Enrollment, String> studentIdColumn = new TableColumn<>("Student ID");
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledStudentID"));
        studentIdColumn.setMinWidth(80);
    
        TableColumn<Enrollment, String> studentFirstNameColumn = new TableColumn<>("First Name");
        studentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentFirstName"));
        studentFirstNameColumn.setMinWidth(100);
    
        TableColumn<Enrollment, String> studentLastNameColumn = new TableColumn<>("Last Name");
        studentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentLastName"));
        studentLastNameColumn.setMinWidth(100);
    
        TableColumn<Enrollment, String> courseIdColumn = new TableColumn<>("Course ID");
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrolledCourseID"));
        courseIdColumn.setMinWidth(80);
    
        TableColumn<Enrollment, String> courseNameColumn = new TableColumn<>("Course Name");
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseNameColumn.setMinWidth(120);
    
        TableColumn<Enrollment, String> yearColumn = new TableColumn<>("Enrollment Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
        yearColumn.setMinWidth(100);
    
        TableColumn<Enrollment, String> semesterColumn = new TableColumn<>("Semester");
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        semesterColumn.setMinWidth(100);
    
        TableColumn<Enrollment, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeColumn.setMinWidth(80);
    
        tableView.getColumns().addAll(idColumn, studentIdColumn, studentFirstNameColumn, studentLastNameColumn,
                courseIdColumn, courseNameColumn, yearColumn, semesterColumn, gradeColumn);
    
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        Label searchLabel = new Label("Student ID:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
    
        grid.add(searchLabel, 0, 0);
        grid.add(searchField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(scrollPane, 0, 1, 3, 1); // Adding ScrollPane containing TableView
    
        mainPane.setCenter(grid);
    
        // Ensure that enrollments list is not null or empty
        if (enrollments == null || enrollments.isEmpty()) {
            System.out.println("Enrollments list is empty. Please ensure it is populated.");
        }
    
        // Search button action
        searchButton.setOnAction(e -> {
            String searchId = searchField.getText();
            List<Enrollment> filteredEnrollments = new ArrayList<>();
    
            if (searchId == null || searchId.trim().isEmpty()) {
                System.out.println("Please enter a valid Student ID.");
                return;
            }
    
            // Filter enrollments by Student ID
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getEnrolledStudentID().equalsIgnoreCase(searchId.trim())) {
                    // Debug print to ensure that grade is being set
                    if (enrollment.getGrade() == null) {
                        enrollment.setGrade("N/A"); // Set default if grade is null
                    }
                    System.out.println("Enrollment ID: " + enrollment.getEnrollmentID() + ", Grade: " + enrollment.getGrade());
    
                    filteredEnrollments.add(enrollment);
                }
            }
    
            if (filteredEnrollments.isEmpty()) {
                System.out.println("No enrollments found for Student ID: " + searchId);
            }
    
            // Set the filtered enrollments into the table view
            tableView.setItems(FXCollections.observableArrayList(filteredEnrollments));
    
            // Refresh the table view to ensure updated values are displayed
            tableView.refresh();
        });
    }
    
    
    
    

    /**
     * *********** (End of grade management section) **************
     */
    /**
     * *********** Methods for Delete Operations ***********
     */
    private void showDeleteStudentDialog() {
        Label idLabel = new Label("Student ID:");
        TextField idField = new TextField();
        Button searchButton = new Button("Search");
        Button deleteButton = new Button("Delete");
        Button cancelButton = new Button("Cancel");
        deleteButton.setDisable(true);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setEditable(false);

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setEditable(false);

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        cityField.setEditable(false);

        Label stateLabel = new Label("State:");
        TextField stateField = new TextField();
        stateField.setEditable(false);

        Label zipLabel = new Label("Zip Code:");
        TextField zipField = new TextField();
        zipField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(firstNameLabel, 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(lastNameLabel, 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(cityLabel, 0, 3);
        grid.add(cityField, 1, 3);
        grid.add(stateLabel, 0, 4);
        grid.add(stateField, 1, 4);
        grid.add(zipLabel, 0, 5);
        grid.add(zipField, 1, 5);
        grid.add(deleteButton, 0, 6);
        grid.add(cancelButton, 1, 6);

        mainPane.setCenter(grid);

        // Search button action
        searchButton.setOnAction(e -> {
            String searchId = idField.getText().trim();

            if (!searchId.isEmpty()) {
                String searchQuery = "SELECT first_name, last_name, street_address, city, state, zip FROM student WHERE student_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

                    stmt.setString(1, searchId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        firstNameField.setText(rs.getString("first_name"));
                        lastNameField.setText(rs.getString("last_name"));
                        cityField.setText(rs.getString("city"));
                        stateField.setText(rs.getString("state"));
                        zipField.setText(rs.getString("zip"));
                        deleteButton.setDisable(false);
                    } else {
                        showAlert("Not Found", "No student found with the given ID.");
                        clearFields(firstNameField, lastNameField, cityField, stateField, zipField);
                        deleteButton.setDisable(true);
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while searching for the student: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Please enter a valid Student ID.");
            }
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            String deleteId = idField.getText().trim();

            if (!deleteId.isEmpty()) {
                String deleteQuery = "DELETE FROM student WHERE student_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, deleteId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Success", "Student deleted successfully.");
                        mainPane.setCenter(null); // Clear the dialog after successful deletion
                    } else {
                        showAlert("Error", "Failed to delete the student. Please try again.");
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while deleting the student: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Cancel button action
        cancelButton.setOnAction(e -> {
            mainPane.setCenter(null);
            clearFields(firstNameField, lastNameField, cityField, stateField, zipField);
            deleteButton.setDisable(true);
        });
    }

    private void showDeleteCourseDialog() {
        Label idLabel = new Label("Course ID:");
        TextField idField = new TextField();
        Button searchButton = new Button("Search");
        Button deleteButton = new Button("Delete");
        Button cancelButton = new Button("Cancel");
        deleteButton.setDisable(true);

        Label nameLabel = new Label("Course Name:");
        TextField nameField = new TextField();
        nameField.setEditable(false);

        Label descLabel = new Label("Course Description:");
        TextField descField = new TextField();
        descField.setEditable(false);

        Label deptLabel = new Label("Department:");
        TextField deptField = new TextField();
        deptField.setEditable(false);

        Label numLabel = new Label("Course Number:");
        TextField numField = new TextField();
        numField.setEditable(false);

        Label professorLabel = new Label("Professor:");
        TextField professorField = new TextField();
        professorField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(descLabel, 0, 2);
        grid.add(descField, 1, 2);
        grid.add(deptLabel, 0, 3);
        grid.add(deptField, 1, 3);
        grid.add(numLabel, 0, 4);
        grid.add(numField, 1, 4);
        grid.add(professorLabel, 0, 5);
        grid.add(professorField, 1, 5);
        grid.add(deleteButton, 0, 6);
        grid.add(cancelButton, 1, 6);

        mainPane.setCenter(grid);

        // Search button action
        searchButton.setOnAction(e -> {
            String searchId = idField.getText().trim();

            if (!searchId.isEmpty()) {
                String searchQuery = "SELECT course_name, course_desc, dept_id, professor_id, course_num FROM course WHERE course_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

                    stmt.setString(1, searchId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        nameField.setText(rs.getString("course_name"));
                        descField.setText(rs.getString("course_desc"));
                        deptField.setText(rs.getString("dept_id"));  // Convert department ID to name if needed
                        professorField.setText(rs.getString("professor_id"));  // Convert professor ID to name if needed
                        numField.setText(rs.getString("course_num"));
                        deleteButton.setDisable(false);
                    } else {
                        showAlert("Not Found", "No course found with the given ID.");
                        nameField.clear();
                        descField.clear();
                        deptField.clear();
                        numField.clear();
                        professorField.clear();
                        deleteButton.setDisable(true);
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while searching for the course: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Please enter a valid Course ID.");
            }
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            String deleteId = idField.getText().trim();

            if (!deleteId.isEmpty()) {
                String deleteQuery = "DELETE FROM course WHERE course_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, deleteId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Success", "Course deleted successfully.");
                        mainPane.setCenter(null); // Clear the dialog after successful deletion
                    } else {
                        showAlert("Error", "Failed to delete the course. Please try again.");
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while deleting the course: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Cancel button action
        cancelButton.setOnAction(e -> {
            mainPane.setCenter(null);
            nameField.clear();
            descField.clear();
            deptField.clear();
            numField.clear();
            professorField.clear();
            deleteButton.setDisable(true);
        });
    }

    private void showDeleteEnrollmentDialog() {
        Label idLabel = new Label("Enrollment ID:");
        TextField idField = new TextField();
        Button searchButton = new Button("Search");
        Button deleteButton = new Button("Delete");
        Button cancelButton = new Button("Cancel");
        deleteButton.setDisable(true);

        Label studentIdLabel = new Label("Student ID:");
        TextField studentIdField = new TextField();
        studentIdField.setEditable(false);

        Label courseIdLabel = new Label("Course ID:");
        TextField courseIdField = new TextField();
        courseIdField.setEditable(false);

        Label yearLabel = new Label("Enrollment Year:");
        TextField yearField = new TextField();
        yearField.setEditable(false);

        Label semesterLabel = new Label("Semester:");
        TextField semesterField = new TextField();
        semesterField.setEditable(false);

        Label gradeLabel = new Label("Grade:");
        TextField gradeField = new TextField();
        gradeField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(searchButton, 2, 0);
        grid.add(studentIdLabel, 0, 1);
        grid.add(studentIdField, 1, 1);
        grid.add(courseIdLabel, 0, 2);
        grid.add(courseIdField, 1, 2);
        grid.add(yearLabel, 0, 3);
        grid.add(yearField, 1, 3);
        grid.add(semesterLabel, 0, 4);
        grid.add(semesterField, 1, 4);
        grid.add(gradeLabel, 0, 5);
        grid.add(gradeField, 1, 5);
        grid.add(deleteButton, 0, 6);
        grid.add(cancelButton, 1, 6);

        mainPane.setCenter(grid);

        // Search button action
        searchButton.setOnAction(e -> {
            String searchId = idField.getText().trim();

            if (!searchId.isEmpty()) {
                String searchQuery = "SELECT student_id, course_id, enrollment_year, semester, grade FROM enrollment WHERE enrollment_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

                    stmt.setString(1, searchId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        studentIdField.setText(rs.getString("student_id"));
                        courseIdField.setText(rs.getString("course_id"));
                        yearField.setText(rs.getString("enrollment_year"));
                        semesterField.setText(rs.getString("semester"));
                        gradeField.setText(rs.getString("grade"));
                        deleteButton.setDisable(false);
                    } else {
                        showAlert("Not Found", "No enrollment found with the given ID.");
                        clearFields(studentIdField, courseIdField, yearField, semesterField, gradeField);
                        deleteButton.setDisable(true);
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while searching for the enrollment: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Please enter a valid Enrollment ID.");
            }
        });

        // Delete button action
        deleteButton.setOnAction(e -> {
            String deleteId = idField.getText().trim();

            if (!deleteId.isEmpty()) {
                String deleteQuery = "DELETE FROM enrollment WHERE enrollment_id = ?";

                try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, deleteId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        showAlert("Success", "Enrollment deleted successfully.");
                        mainPane.setCenter(null); // Clear the dialog after successful deletion
                    } else {
                        showAlert("Error", "Failed to delete the enrollment. Please try again.");
                    }

                } catch (SQLException ex) {
                    showAlert("Database Error", "An error occurred while deleting the enrollment: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Cancel button action
        cancelButton.setOnAction(e -> {
            mainPane.setCenter(null);
            clearFields(studentIdField, courseIdField, yearField, semesterField, gradeField);
            deleteButton.setDisable(true);
        });
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    /**
     * *********** (End of delete operations section) **************
     */
    /**
     * *********** Utility Method for Alert ***********
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
