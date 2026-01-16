package com.orodent.azdora.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orodent.azdora.core.database.model.Ota;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileService {
    private static final Gson gson = new Gson();
    private static final Path PRODUCTS_FILE_PATH = Path.of("ota.json");

    /*
    questa classe si occupa di caricare e salvare i dati in file Json e non chiedetemi nulla perchè li ho copiati
    pari paro da internet.
     */

    public static void saveProducts(List<Ota> products) {
        try (FileWriter writer = new FileWriter(PRODUCTS_FILE_PATH.toFile())) {
            gson.toJson(products, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Ota> loadProducts() {
        try {
            if (!Files.exists(PRODUCTS_FILE_PATH)) return List.of();

            Type listType = new TypeToken<List<Ota>>() {}.getType();
            FileReader reader = new FileReader(PRODUCTS_FILE_PATH.toFile());
            return gson.fromJson(reader, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
