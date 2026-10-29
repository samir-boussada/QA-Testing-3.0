package com.zarastyle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "garment_stocks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GarmentStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garment_id", nullable = false)
    @JsonIgnore
    private Garment garment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    private StoreBranch branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Size size;

    @Min(0)
    @Column(nullable = false)
    private int floorStock = 0; // Front sales floor

    @Min(0)
    @Column(nullable = false)
    private int backroomStock = 0; // Stockroom storage

    @Column(nullable = false, unique = true, length = 40)
    private String rfidBarcode; // Barcode SKU for RFID scanning

    public GarmentStock() {}

    public GarmentStock(Garment garment, StoreBranch branch, Size size, int floorStock, int backroomStock, String rfidBarcode) {
        this.garment = garment;
        this.branch = branch;
        this.size = size;
        this.floorStock = floorStock;
        this.backroomStock = backroomStock;
        this.rfidBarcode = rfidBarcode;
    }

    public int getTotalStock() {
        return floorStock + backroomStock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Garment getGarment() { return garment; }
    public void setGarment(Garment garment) { this.garment = garment; }

    public StoreBranch getBranch() { return branch; }
    public void setBranch(StoreBranch branch) { this.branch = branch; }

    public Size getSize() { return size; }
    public void setSize(Size size) { this.size = size; }

    public int getFloorStock() { return floorStock; }
    public void setFloorStock(int floorStock) { this.floorStock = floorStock; }

    public int getBackroomStock() { return backroomStock; }
    public void setBackroomStock(int backroomStock) { this.backroomStock = backroomStock; }

    public String getRfidBarcode() { return rfidBarcode; }
    public void setRfidBarcode(String rfidBarcode) { this.rfidBarcode = rfidBarcode; }
}
