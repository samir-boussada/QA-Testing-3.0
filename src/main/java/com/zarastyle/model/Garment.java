package com.zarastyle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "garments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Garment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String refCode; // e.g. ZARA-8421-BLK

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 200)
    private String composition; // e.g. 100% Organic Wool

    @Column(length = 20)
    private String season; // e.g. FW26

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Category category;

    @Column(length = 350)
    private String imageUrl;

    @OneToMany(mappedBy = "garment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GarmentStock> stockList = new ArrayList<>();

    public Garment() {}

    public Garment(String refCode, String name, String composition, String season, BigDecimal basePrice, Category category, String imageUrl) {
        this.refCode = refCode;
        this.name = name;
        this.composition = composition;
        this.season = season;
        this.basePrice = basePrice;
        this.discountedPrice = basePrice;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public void addStock(GarmentStock stock) {
        stockList.add(stock);
        stock.setGarment(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRefCode() { return refCode; }
    public void setRefCode(String refCode) { this.refCode = refCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getComposition() { return composition; }
    public void setComposition(String composition) { this.composition = composition; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<GarmentStock> getStockList() { return stockList; }
    public void setStockList(List<GarmentStock> stockList) { this.stockList = stockList; }
}
