package com.zarastyle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 35)
    private String transferRef;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garment_id", nullable = false)
    private Garment garment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Size size;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_branch_id", nullable = false)
    private StoreBranch fromBranch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_branch_id", nullable = false)
    private StoreBranch toBranch;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status = TransferStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedDate = LocalDateTime.now();

    public StockTransfer() {}

    public StockTransfer(String transferRef, Garment garment, Size size, StoreBranch fromBranch, StoreBranch toBranch, int quantity) {
        this.transferRef = transferRef;
        this.garment = garment;
        this.size = size;
        this.fromBranch = fromBranch;
        this.toBranch = toBranch;
        this.quantity = quantity;
        this.status = TransferStatus.PENDING;
        this.requestedDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransferRef() { return transferRef; }
    public void setTransferRef(String transferRef) { this.transferRef = transferRef; }

    public Garment getGarment() { return garment; }
    public void setGarment(Garment garment) { this.garment = garment; }

    public Size getSize() { return size; }
    public void setSize(Size size) { this.size = size; }

    public StoreBranch getFromBranch() { return fromBranch; }
    public void setFromBranch(StoreBranch fromBranch) { this.fromBranch = fromBranch; }

    public StoreBranch getToBranch() { return toBranch; }
    public void setToBranch(StoreBranch toBranch) { this.toBranch = toBranch; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }

    public LocalDateTime getRequestedDate() { return requestedDate; }
}
