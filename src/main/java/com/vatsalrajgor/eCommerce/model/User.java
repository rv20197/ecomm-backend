package com.vatsalrajgor.eCommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Size(max = 10, message = "User Name can be of at most of 10 characters.")
    @Size(min = 4, message = "User Name must be at least of 4 characters.")
    @Column(unique = true)
    private String userName;

    @NotBlank
    @Size(min = 6, message = "Password must be at least of 6 characters.")
    @Size(max = 15, message = "Password can be of at most of 15 characters.")
    private String password;

    @NotBlank
    @Size(max = 50, message = "First Name can be of at most of 50 characters.")
    @Size(min = 2, message = "First Name must be at least of 2 characters.")
    private String firstName;

    @NotBlank
    @Size(max = 50, message = "Last Name can be of at most of 50 characters.")
    @Size(min = 2, message = "Last Name must be at least of 2 characters.")
    private String lastName;

    @NotBlank
    @Size(max = 50, message = "Email can be of at most of 50 characters.")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 10, message = "Phone Number can be of at most of 10 characters.")
    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Set<Product> products = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_address", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresses = new ArrayList<>();

    public User(String userName, String password, String firstName, String lastName, String email, String phoneNumber, boolean enabled) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
    }
}
