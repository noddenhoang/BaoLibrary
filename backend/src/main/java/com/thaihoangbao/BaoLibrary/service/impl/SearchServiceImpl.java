package com.thaihoangbao.BaoLibrary.service.impl;

import com.thaihoangbao.BaoLibrary.dto.AuthorDto;
import com.thaihoangbao.BaoLibrary.dto.BookResponseDto;
import com.thaihoangbao.BaoLibrary.dto.CategoryDto;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.SearchCriteriaDto;
import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.repository.BookRepository;
import com.thaihoangbao.BaoLibrary.service.SearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private BookRepository bookRepository;

    @Override
    public PagedResponse<BookResponseDto> advancedSearch(SearchCriteriaDto criteria, int pageNo, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Search by keyword in title
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("tuaSach")), "%" + criteria.getKeyword().toLowerCase() + "%"));
        }
        
        // Filter by year range
        if (criteria.getYearFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("namXuatBan"), criteria.getYearFrom()));
        }
        
        if (criteria.getYearTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("namXuatBan"), criteria.getYearTo()));
        }
        
        // Filter by authors
        if (criteria.getAuthorIds() != null && !criteria.getAuthorIds().isEmpty()) {
            Join<Object, Object> authorsJoin = root.join("authors");
            predicates.add(authorsJoin.get("authorId").in(criteria.getAuthorIds()));
        }
        
        // Filter by categories
        if (criteria.getCategoryIds() != null && !criteria.getCategoryIds().isEmpty()) {
            Join<Object, Object> categoriesJoin = root.join("categories");
            predicates.add(categoriesJoin.get("categoryId").in(criteria.getCategoryIds()));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        query.distinct(true); // Avoid duplicates due to joins
        
        // Count query for total results
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Book> countRoot = countQuery.from(Book.class);
        
        // Apply the same predicates to count query
        countQuery.select(cb.countDistinct(countRoot));
        if (!predicates.isEmpty()) {
            List<Predicate> countPredicates = new ArrayList<>();
            
            // Recreate predicates for count query
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                countPredicates.add(cb.like(cb.lower(countRoot.get("tuaSach")), "%" + criteria.getKeyword().toLowerCase() + "%"));
            }
            
            if (criteria.getYearFrom() != null) {
                countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("namXuatBan"), criteria.getYearFrom()));
            }
            
            if (criteria.getYearTo() != null) {
                countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("namXuatBan"), criteria.getYearTo()));
            }
            
            if (criteria.getAuthorIds() != null && !criteria.getAuthorIds().isEmpty()) {
                Join<Object, Object> authorsJoin = countRoot.join("authors");
                countPredicates.add(authorsJoin.get("authorId").in(criteria.getAuthorIds()));
            }
            
            if (criteria.getCategoryIds() != null && !criteria.getCategoryIds().isEmpty()) {
                Join<Object, Object> categoriesJoin = countRoot.join("categories");
                countPredicates.add(categoriesJoin.get("categoryId").in(criteria.getCategoryIds()));
            }
            
            countQuery.where(countPredicates.toArray(new Predicate[0]));
        }
        
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        
        // Apply pagination
        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageNo * pageSize);
        typedQuery.setMaxResults(pageSize);
        
        List<Book> books = typedQuery.getResultList();
        
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        // Convert to DTOs
        List<BookResponseDto> bookResponseDtos = books.stream()
                .map(this::convertToBookResponseDto)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                bookResponseDtos,
                pageNo,
                pageSize,
                totalElements,
                totalPages,
                pageNo >= totalPages - 1
        );
    }
    
    private BookResponseDto convertToBookResponseDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setBookId(book.getBookId());
        dto.setTuaSach(book.getTuaSach());
        dto.setMoTa(book.getMoTa());
        dto.setNamXuatBan(book.getNamXuatBan());
        dto.setHinhAnhSach(book.getHinhAnhSach());
        
        // Convert authors
        List<AuthorDto> authorDtos = book.getAuthors().stream()
                .map(author -> {
                    AuthorDto authorDto = new AuthorDto();
                    authorDto.setAuthorId(author.getAuthorId());
                    authorDto.setTenTacGia(author.getTenTacGia());
                    return authorDto;
                })
                .collect(Collectors.toList());
        dto.setAuthors(authorDtos);
        
        // Convert categories
        List<CategoryDto> categoryDtos = book.getCategories().stream()
                .map(category -> {
                    CategoryDto categoryDto = new CategoryDto();
                    categoryDto.setCategoryId(category.getCategoryId());
                    categoryDto.setCategoryName(category.getCategoryName());
                    categoryDto.setDescription(category.getDescription());
                    return categoryDto;
                })
                .collect(Collectors.toList());
        dto.setCategories(categoryDtos);
        
        return dto;
    }
}
