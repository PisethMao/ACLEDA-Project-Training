package com.acleda.training.studentmanagement.features.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalUserResponse(
        Long id,
        String name,
        String username,
        String email,
        Address address,
        String phone,
        String website,
        Company company
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(
            String street,
            String suite,
            String city,
            String zipcode,
            Geo geo
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geo(
            String lat,
            String lng
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Company(
            String name,
            String catchPhrase,
            String bs
    ) {
    }
}