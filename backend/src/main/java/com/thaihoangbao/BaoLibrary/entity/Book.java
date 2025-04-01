package com.thaihoangbao.BaoLibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "BOOK")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookID")
    @EqualsAndHashCode.Include
    private Integer bookId;
    
    @Column(name = "TuaSach", nullable = false)
    private String tuaSach;
    
    @Column(name = "MoTa")
    private String moTa;
    
    @Column(name = "NamXuatBan")
    private Integer namXuatBan;
    
    @Column(name = "HinhAnhSach")
    private String hinhAnhSach;
    
    @Column(name = "SoLuong")
    private Integer soLuong = 0;
    
    @ManyToMany
    @JoinTable(
        name = "BOOK_AUTHOR",
        joinColumns = @JoinColumn(name = "BookID"),
        inverseJoinColumns = @JoinColumn(name = "AuthorID")
    )
    private Set<Author> authors = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "BOOK_CATEGORY",
        joinColumns = @JoinColumn(name = "BookID"),
        inverseJoinColumns = @JoinColumn(name = "CategoryID")
    )
    private Set<Category> categories = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }
}