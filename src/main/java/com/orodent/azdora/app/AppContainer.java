package com.orodent.azdora.app;

import com.orodent.azdora.core.database.Database;
import com.orodent.azdora.core.database.implementation.GuestContactRepositoryImpl;
import com.orodent.azdora.core.database.implementation.GuestRepositoryImpl;
import com.orodent.azdora.core.database.implementation.OtaRepositoryImpl;
import com.orodent.azdora.core.database.implementation.ReservationRepositoryImpl;
import com.orodent.azdora.core.database.repository.GuestContactRepository;
import com.orodent.azdora.core.database.repository.GuestRepository;
import com.orodent.azdora.core.database.repository.OtaRepository;
import com.orodent.azdora.core.database.repository.ReservationRepository;

public class AppContainer {

    // --- Repositories ---
    private final GuestRepository guestRepository;
    private final GuestContactRepository guestContactRepository;
    private final ReservationRepository reservationRepository;
    private final OtaRepository otaRepository;

    // --- Database ---
    protected final Database database;

    protected AppContainer() {

        // DATABASE
        this.database = new Database();
        database.start();

        // LOAD CSV PATHS

        // REPOSITORIES
        this.otaRepository = new OtaRepositoryImpl(database.getConnection());
        this.guestRepository = new GuestRepositoryImpl(database.getConnection());
        this.guestContactRepository = new GuestContactRepositoryImpl(database.getConnection());
        this.reservationRepository = new ReservationRepositoryImpl(database.getConnection());


        System.out.println("Caricati le repository.");
    }

    // --- PUBLIC GETTERS ---
    public OtaRepository OtaRepo() {
        return otaRepository;
    }
    public GuestRepository getGuestRepo() {
        return guestRepository;
    }
    public GuestContactRepository getGuestContactRepo() {
        return guestContactRepository;
    }
    public ReservationRepository getReservationRepo() {
        return reservationRepository;
    }
}
