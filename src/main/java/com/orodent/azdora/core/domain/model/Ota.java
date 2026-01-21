package com.orodent.azdora.core.domain.model;

public record Ota(
        Long id,
        String name
) {
    @Override
    public String toString(){
        return name;
    }
}