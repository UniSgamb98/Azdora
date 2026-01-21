module com.orodent.azdora {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires org.apache.derby.server;
    requires java.sql;

    exports com.orodent.azdora.app;
    exports com.orodent.azdora.core.domain.model;
    exports com.orodent.azdora.core.domain.repository;
    exports com.orodent.azdora.feature.reservation.view;
    exports com.orodent.azdora.feature.reservation.view.partials;
    exports com.orodent.azdora.feature.reservation.controller;
    exports com.orodent.azdora.feature.reservation.controller.table;
    exports com.orodent.azdora.feature.reservation.service;
    exports com.orodent.azdora.feature.reservation.service.dto;
    exports com.orodent.azdora.core.domain.exception;

    opens com.orodent.azdora.core.domain.model to com.google.gson;
    exports com.orodent.azdora.feature.contact;
    exports com.orodent.azdora.feature.contact.controller;
    exports com.orodent.azdora.feature.contact.service;
    exports com.orodent.azdora.feature.contact.service.impl;
    exports com.orodent.azdora.feature.contact.view;
    exports com.orodent.azdora.feature.reservation.service.impl;
    exports com.orodent.azdora.feature.reservation.view.model;
    exports com.orodent.azdora.core.persistence.database;
    exports com.orodent.azdora.core.persistence.file;
}