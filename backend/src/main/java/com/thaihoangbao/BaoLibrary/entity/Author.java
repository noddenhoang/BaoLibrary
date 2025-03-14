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
@Table(name = "AUTHOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuthorID")
    @EqualsAndHashCode.Include
    private Integer authorId;
    
    @Column(name = "TenTacGia", nullable = false)
    private String tenTacGia;
    
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(authorId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(authorId, author.authorId);
    }
}