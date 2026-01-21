package com.orodent.azdora.app;


import com.orodent.azdora.feature.reservation.controller.ReservationMainController;
import com.orodent.azdora.feature.reservation.view.ReservationMainView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


public class AppController {
    /*
    Qua salvo i modelli dell'applicazione e tutte le variabili che servono all'intera applicazione e non alle
    singole pagine.
     */
    private final Stage stage;
    private final AppContainer app;
    private final String cssPath;

    /*
    In questo progetto l'applicazione è state-less. Che significa che tutte le View vengono create da zero sempre.
    Questo significa che i riferimenti dei controller qui sotto non sono necessari. Sarebbero tornati utili nel caso:
    - i controller mantenessero uno stato per ricordare dei filtri impostati dall'utente, il testo inserito in
    un form oppure una selezione di qualche genere.
    - il controller gestisce qualcosa di continuo come Thread, Timer, connessioni TCP. Insomma tutto ciò che ha bisogno
    di essere fermato in un secondo momento oppure se sta leggendo un flusso di dati.
     */

    public AppController(Stage stage) {
        this.stage = stage;
        this.app = new AppContainer();
        this.cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();

        showHome();
        stage.setOnCloseRequest(e -> shutdown());
        stage.show();
    }

    /*
    -------------------------------------------------------------------------------------------------------------------
    Creo un metodo per ogni view che devo mostrare. Ogni metodo chiama configureHeader per assegnare le funzioni dei
    tasti dello header qua e non nei singoli controller di tutte le view.
     */

    public void showHome() {
        ReservationMainView view = new ReservationMainView();
        new ReservationMainController(view, app.getGuestRepo(), app.getReservationRepo(), app.OtaRepo(), app.getGuestContactRepo(), app.getTransactionManager());
      //  configureHeader(view.getHeader());

        stage.setScene(createSceneWithCSS(view));
        stage.setTitle("TON - Home");
    }

    /*
    Creando le Scenes con questo metodo vengono collegate al CSS che può essere scritto tutto in un unico file.
     */
    private Scene createSceneWithCSS(Parent root) {
        Scene scene = new Scene(root, 1160, 850);
        scene.getStylesheets().add(cssPath);
        return scene;
    }

    public void shutdown() {
        app.database.stop();
    }
}
