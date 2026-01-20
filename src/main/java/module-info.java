module com.orodent.azdora {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires org.apache.derby.server;
    requires java.sql;

    exports com.orodent.azdora.app;
    exports com.orodent.azdora.core.database;
    exports com.orodent.azdora.core.database.model;
    exports com.orodent.azdora.core.database.repository;
    exports com.orodent.azdora.feature.reservation.view;
    exports com.orodent.azdora.feature.reservation.view.partials;
    exports com.orodent.azdora.feature.reservation.controller;
    exports com.orodent.azdora.feature.reservation.controller.table;
    exports com.orodent.azdora.feature.reservation.service;
    exports com.orodent.azdora.feature.reservation.ui;

    opens com.orodent.azdora.core.database.model to com.google.gson;
}