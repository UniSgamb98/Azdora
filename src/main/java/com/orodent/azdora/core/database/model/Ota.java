package com.orodent.azdora.core.database.model;

public record Ota(
        Long id,
        String name
) {
    @Override
    public String toString(){
        return name;
    }
}