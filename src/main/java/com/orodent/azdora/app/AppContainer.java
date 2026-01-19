package com.orodent.azdora.app;

import com.orodent.azdora.core.database.Database;
import com.orodent.azdora.core.database.FileService;
import com.orodent.azdora.core.database.TransactionManager;
import com.orodent.azdora.core.database.implementation.GuestContactRepositoryImpl;
import com.orodent.azdora.core.database.implementation.GuestRepositoryImpl;
import com.orodent.azdora.core.database.implementation.OtaRepositoryImpl;
import com.orodent.azdora.core.database.implementation.ReservationRepositoryImpl;
import com.orodent.azdora.core.database.repository.GuestContactRepository;
import com.orodent.azdora.core.database.repository.GuestRepository;
import com.orodent.azdora.core.database.repository.OtaRepository;
import com.orodent.azdora.core.database.repository.ReservationRepository;

import java.sql.Connection;

public class AppContainer {

    // --- Repositories ---
    private final GuestRepository guestRepository;
    private final GuestContactRepository guestContactRepository;
    private final ReservationRepository reservationRepository;
    private final OtaRepository otaRepository;
    private final TransactionManager transactionManager;

    // --- Database ---
    protected final Database database;

    protected AppContainer() {

        // DATABASE
        this.database = new Database();
        database.start();
        Connection connection = database.getConnection();

        // LOAD CSV PATHS

        // REPOSITORIES
        transactionManager = new TransactionManager(connection);
        this.otaRepository = new OtaRepositoryImpl(connection);
        this.guestRepository = new GuestRepositoryImpl(connection);
        this.guestContactRepository = new GuestContactRepositoryImpl(connection);
        this.reservationRepository = new ReservationRepositoryImpl(connection);
        System.out.println("Caricati le repository.");

        // SEED UNA SOLA VOLTA
        seedDatabase(otaRepository);

    }

    private void seedDatabase(OtaRepository otaRepo) {
        if (otaRepo.findAll().isEmpty()) {
            otaRepo.insertAll(FileService.loadOtas());
        }
    }

    // --- PUBLIC GETTERS ---
    public OtaRepository OtaRepo() { return otaRepository; }
    public GuestRepository getGuestRepo() { return guestRepository; }
    public GuestContactRepository getGuestContactRepo() { return guestContactRepository; }
    public ReservationRepository getReservationRepo() { return reservationRepository; }
    public TransactionManager getTransactionManager() { return transactionManager; }
}
