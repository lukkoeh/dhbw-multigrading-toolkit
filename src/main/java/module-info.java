module org.grp8.dhbwmultigradingtoolkit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.io;
    requires org.apache.poi.ooxml;
    requires odfdom.java;
    requires java.xml;

    opens org.grp8.dhbwmultigradingtoolkit to javafx.fxml;
    exports org.grp8.dhbwmultigradingtoolkit;
}