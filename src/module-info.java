module view.atpprojectpartc {
  requires javafx.controls;
  requires javafx.fxml;
  requires ATPProjectJAR;


  opens View to javafx.fxml;
  exports View;
}