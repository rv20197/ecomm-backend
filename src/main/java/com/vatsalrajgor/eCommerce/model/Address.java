package com.vatsalrajgor.eCommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street must be at least of 5 characters.")
    private String street;

    @NotBlank
    @Size(min = 1, message = "Street must be at least of 1 characters.")
    private String houseNumber;

    @NotBlank
    @Size(min = 1, message = "Floor must be at least of 1 character.")
    private String floor;

    @NotBlank
    @Size(min = 5, message = "Apartment must be at least of 5 characters.")
    private String apartment;

    @NotBlank
    @Size(min = 4, message = "Street must be at least of 5 characters.")
    private String city;

    @NotBlank
    @Size(min = 2, message = "Street must be at least of 2 characters.")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Street must be at least of 2 characters.")
    private String country;

    @NotBlank
    @Size(min = 6, message = "Street must be at least of 6 characters.")
    private String pincode;

    @ManyToMany(mappedBy = "addresses")
    @ToString.Exclude
    private List<User> users = new ArrayList<>();

    public Address(Long addressId, String street, String houseNumber, String floor, String apartment, String city, String state, String country, String pincode) {
        this.addressId = addressId;
        this.street = street;
        this.houseNumber = houseNumber;
        this.floor = floor;
        this.apartment = apartment;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
