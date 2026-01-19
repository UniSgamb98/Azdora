package com.orodent.azdora.core.database.repository;

import com.orodent.azdora.core.database.model.Ota;

import java.util.List;

public interface OtaRepository {
    List<Ota> findAll();
    Ota findById(long id);
    Ota insert(Ota ota);
    void insertAll(List<Ota> otas);
}
