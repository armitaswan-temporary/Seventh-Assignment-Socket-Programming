module com.example.seventhassignmentsocketprogrammingfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.seventhassignmentsocketprogrammingfx to javafx.fxml;
    exports com.example.seventhassignmentsocketprogrammingfx;
}