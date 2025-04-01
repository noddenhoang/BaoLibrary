package com.thaihoangbao.BaoLibrary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "INVENTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @EmbeddedId
    private InventoryId id;
    
    @ManyToOne
    @MapsId("branchId")
    @JoinColumn(name = "BranchID")
    private Branch branch;
    
    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "BookID")
    private Book book;
    
    @Column(name = "TongSoBan")
    private Integer totalCopies;
    
    @Column(name = "SoLuongHienCo")
    private Integer availableCopies;
    
    // Thêm phương thức để tương thích với code đã viết
    public Integer getQuantity() {
        return this.availableCopies;
    }
    
    public void setQuantity(Integer quantity) {
        this.availableCopies = quantity;
    }
    
    // Embedded composite key class
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryId implements java.io.Serializable {
        @Column(name = "BranchID")
        private Integer branchId;
        
        @Column(name = "BookID")
        private Integer bookId;
    }
} 