module view.atpprojectpartc {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.media;
  requires ATPProjectJAR;
    requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;

  opens View to javafx.fxml;
  exports View;
}