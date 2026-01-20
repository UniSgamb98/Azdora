package com.orodent.azdora.feature.contact;

import com.orodent.azdora.core.database.model.Guest;
import com.orodent.azdora.core.database.repository.GuestRepository;
import com.orodent.azdora.core.database.exception.ValidationException;

import java.text.Normalizer;
import java.util.*;

public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepo;

    public GuestServiceImpl(GuestRepository guestRepo) {
        this.guestRepo = guestRepo;
    }

    @Override
    public List<Guest> findAll() {
        return guestRepo.findAll();
    }

    @Override
    public List<Guest> search(String query, int limit) {
        String q = normalize(query);
        if (q.isBlank()) {
            // se query vuota, mostra i primi N ordinati per cognome/nome
            return guestRepo.findAll().stream()
                    .sorted(Comparator.comparing((Guest g) -> safe(g.lastName()))
                            .thenComparing(g -> safe(g.firstName())))
                    .limit(limit)
                    .toList();
        }

        // (Versione semplice e robusta): carico tutti e ranko in memoria.
        // Quando crescerai (tanti guest), spostiamo su query DB con LIMIT e criteri.
        List<Guest> all = guestRepo.findAll();

        List<ScoredGuest> scored = new ArrayList<>();
        for (Guest g : all) {
            int score = score(g, q);
            if (score > 0) scored.add(new ScoredGuest(g, score));
        }

        scored.sort(Comparator
                .comparingInt(ScoredGuest::score).reversed()
                .thenComparing(sg -> safe(sg.guest().lastName()))
                .thenComparing(sg -> safe(sg.guest().firstName())));

        return scored.stream()
                .limit(Math.max(1, limit))
                .map(ScoredGuest::guest)
                .toList();
    }

    @Override
    public void updateNotes(long guestId, String notes) {
        if (guestId <= 0) {
            throw new ValidationException("ID guest non valido");
        }
        if (notes == null) notes = "";
        guestRepo.updateNotes(guestId, notes);
    }

    @Override
    public Optional<Guest> bestMatch(String query) {
        return search(query, 1).stream().findFirst();
    }

    @Override
    public Guest createGuest(String firstName, String lastName) {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();

        if (fn.isBlank() && ln.isBlank()) {
            throw new ValidationException("Nome e cognome mancanti");
        }

        // qui usi il tuo insert (adatta al tuo Guest record)
        return guestRepo.insert(new Guest(null, fn, ln, null));
    }

    @Override
    public Guest updateGuest(long id, String firstName, String lastName) {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();

        if (id <= 0) throw new ValidationException("ID non valido");
        if (fn.isBlank() && ln.isBlank()) throw new ValidationException("Nome e cognome mancanti");

        guestRepo.updateName(id, fn, ln);  // aggiungi questo metodo al repo se non esiste
        return guestRepo.findById(id);
    }

    @Override
    public void deleteGuest(long id) {
        if (id <= 0) throw new ValidationException("ID non valido");
        guestRepo.deleteById(id); // aggiungi al repo se non esiste
    }

    // ----------------- ranking helpers -----------------

    private int score(Guest g, String q) {
        String full = normalize(safe(g.firstName()) + " " + safe(g.lastName()));
        String rev  = normalize(safe(g.lastName()) + " " + safe(g.firstName()));

        // token query (es. "mar ro")
        List<String> tokens = Arrays.stream(q.split("\\s+"))
                .filter(t -> !t.isBlank())
                .toList();

        int s = 0;

        // match forti: startsWith
        if (full.startsWith(q)) s += 100;
        if (rev.startsWith(q))  s += 95;

        // match medio: contiene
        if (full.contains(q)) s += 60;
        if (rev.contains(q))  s += 55;

        // token-based: ogni token deve comparire (aumenta score)
        int tokenHits = 0;
        for (String t : tokens) {
            if (full.contains(t) || rev.contains(t)) tokenHits++;
        }
        if (tokenHits == tokens.size() && tokenHits > 0) s += 40 + tokenHits * 5;
        else if (tokenHits > 0) s += tokenHits * 8;

        return s;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Lower + trim + rimozione accenti
     */
    private static String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase(Locale.ROOT).trim();
        String nfd = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}+", "");
    }

    private record ScoredGuest(Guest guest, int score) {}
}
