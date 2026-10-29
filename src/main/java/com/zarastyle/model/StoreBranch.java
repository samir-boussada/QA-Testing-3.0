package com.zarastyle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "store_branches")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StoreBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String branchCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false)
    private boolean warehouse = false;

    public StoreBranch() {}

    public StoreBranch(String branchCode, String name, String city, boolean warehouse) {
        this.branchCode = branchCode;
        this.name = name;
        this.city = city;
        this.warehouse = warehouse;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isWarehouse() { return warehouse; }
    public void setWarehouse(boolean warehouse) { this.warehouse = warehouse; }
}
