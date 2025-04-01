package com.thaihoangbao.BaoLibrary.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoangbao.BaoLibrary.dto.AuthorDto;
import com.thaihoangbao.BaoLibrary.dto.BookDto;
import com.thaihoangbao.BaoLibrary.dto.BookResponseDto;
import com.thaihoangbao.BaoLibrary.dto.CategoryDto;
import com.thaihoangbao.BaoLibrary.dto.InventoryDto;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.entity.Author;
import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.entity.Branch;
import com.thaihoangbao.BaoLibrary.entity.Category;
import com.thaihoangbao.BaoLibrary.entity.Inventory;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.AuthorRepository;
import com.thaihoangbao.BaoLibrary.repository.BookRepository;
import com.thaihoangbao.BaoLibrary.repository.BranchRepository;
import com.thaihoangbao.BaoLibrary.repository.CategoryRepository;
import com.thaihoangbao.BaoLibrary.repository.InventoryRepository;
import com.thaihoangbao.BaoLibrary.service.BookService;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Override
    @Transactional
    public BookResponseDto createBook(BookDto bookDto) {
        // Tạo mới sách
        Book book = new Book();
        book.setTuaSach(bookDto.getTuaSach());
        book.setMoTa(bookDto.getMoTa());
        book.setNamXuatBan(bookDto.getNamXuatBan());
        book.setHinhAnhSach(bookDto.getHinhAnhSach());
        book.setSoLuong(bookDto.getSoLuong());
        
        // Thêm tác giả
        Set<Author> authors = new HashSet<>();
        if (bookDto.getAuthorIds() != null && !bookDto.getAuthorIds().isEmpty()) {
            for (Integer authorId : bookDto.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + authorId));
                authors.add(author);
            }
        }
        book.setAuthors(authors);
        
        // Thêm danh mục
        Set<Category> categories = new HashSet<>();
        if (bookDto.getCategoryIds() != null && !bookDto.getCategoryIds().isEmpty()) {
            for (Integer categoryId : bookDto.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + categoryId));
                categories.add(category);
            }
        }
        book.setCategories(categories);
        
        // Lưu sách vào cơ sở dữ liệu
        Book savedBook = bookRepository.save(book);
        
        // Nếu có dữ liệu số lượng theo chi nhánh, cập nhật inventory
        if (bookDto.getInventories() != null && !bookDto.getInventories().isEmpty()) {
            updateInventories(savedBook.getBookId(), bookDto.getInventories());
        } else {
            // Nếu không có dữ liệu theo chi nhánh, phân bổ đồng đều cho các chi nhánh
            // Lưu ý: Cơ sở dữ liệu có trigger sẽ tự động xử lý phân bổ
        }
        
        // Chuyển đổi Entity thành DTO để trả về
        return convertToBookResponseDto(savedBook);
    }

    @Override
    public BookResponseDto getBookById(Integer id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        return convertToBookResponseDto(book);
    }

    @Override
    public PagedResponse<BookResponseDto> getAllBooks(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        // Tạo Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        
        // Lấy danh sách sách với phân trang
        Page<Book> bookPage = bookRepository.findAll(pageable);
        
        // Lấy nội dung
        List<Book> books = bookPage.getContent();
        
        // Chuyển đổi sang list DTO
        List<BookResponseDto> content = books.stream()
            .map(this::convertToBookResponseDto)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content, 
            bookPage.getNumber(), 
            bookPage.getSize(), 
            bookPage.getTotalElements(), 
            bookPage.getTotalPages(), 
            bookPage.isLast()
        );
    }

    @Override
    public PagedResponse<BookResponseDto> searchBooks(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Book> bookPage = bookRepository.findByTuaSachContainingIgnoreCase(keyword, pageable);
        
        List<BookResponseDto> content = bookPage.getContent().stream()
            .map(this::convertToBookResponseDto)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content, 
            bookPage.getNumber(), 
            bookPage.getSize(), 
            bookPage.getTotalElements(), 
            bookPage.getTotalPages(), 
            bookPage.isLast()
        );
    }

    @Override
    public PagedResponse<BookResponseDto> getBooksByCategory(Integer categoryId, int pageNo, int pageSize) {
        // Kiểm tra danh mục tồn tại
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + categoryId));
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Book> bookPage = bookRepository.findByCategoryId(categoryId, pageable);
        
        List<BookResponseDto> content = bookPage.getContent().stream()
            .map(this::convertToBookResponseDto)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content, 
            bookPage.getNumber(), 
            bookPage.getSize(), 
            bookPage.getTotalElements(), 
            bookPage.getTotalPages(), 
            bookPage.isLast()
        );
    }

    @Override
    public PagedResponse<BookResponseDto> getBooksByAuthor(Integer authorId, int pageNo, int pageSize) {
        // Kiểm tra tác giả tồn tại
        authorRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + authorId));
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Book> bookPage = bookRepository.findByAuthorId(authorId, pageable);
        
        List<BookResponseDto> content = bookPage.getContent().stream()
            .map(this::convertToBookResponseDto)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content, 
            bookPage.getNumber(), 
            bookPage.getSize(), 
            bookPage.getTotalElements(), 
            bookPage.getTotalPages(), 
            bookPage.isLast()
        );
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Integer id, BookDto bookDto) {
        // Tìm sách cần cập nhật
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        
        // Cập nhật thông tin
        book.setTuaSach(bookDto.getTuaSach());
        book.setMoTa(bookDto.getMoTa());
        book.setNamXuatBan(bookDto.getNamXuatBan());
        book.setHinhAnhSach(bookDto.getHinhAnhSach());
        book.setSoLuong(bookDto.getSoLuong());
        
        // Cập nhật tác giả
        if (bookDto.getAuthorIds() != null) {
            Set<Author> authors = new HashSet<>();
            for (Integer authorId : bookDto.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + authorId));
                authors.add(author);
            }
            book.setAuthors(authors);
        }
        
        // Cập nhật danh mục
        if (bookDto.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>();
            for (Integer categoryId : bookDto.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + categoryId));
                categories.add(category);
            }
            book.setCategories(categories);
        }
        
        // Lưu thay đổi
        Book updatedBook = bookRepository.save(book);
        
        // Nếu có dữ liệu số lượng theo chi nhánh, cập nhật inventory
        if (bookDto.getInventories() != null && !bookDto.getInventories().isEmpty()) {
            updateInventories(updatedBook.getBookId(), bookDto.getInventories());
        }
        
        return convertToBookResponseDto(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        
        bookRepository.delete(book);
    }
    
    // Hàm tiện ích để chuyển đổi Entity thành DTO
    private BookResponseDto convertToBookResponseDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setBookId(book.getBookId());
        dto.setTuaSach(book.getTuaSach());
        dto.setMoTa(book.getMoTa());
        dto.setNamXuatBan(book.getNamXuatBan());
        dto.setHinhAnhSach(book.getHinhAnhSach());
        dto.setSoLuong(book.getSoLuong());
        
        // Chuyển đổi tác giả
        List<AuthorDto> authorDtos = book.getAuthors().stream()
            .map(author -> {
                AuthorDto authorDto = new AuthorDto();
                authorDto.setAuthorId(author.getAuthorId());
                authorDto.setTenTacGia(author.getTenTacGia());
                return authorDto;
            })
            .collect(Collectors.toList());
        dto.setAuthors(authorDtos);
        
        // Chuyển đổi danh mục
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
        
        // Thêm thông tin số lượng sách theo chi nhánh
        try {
            List<InventoryDto> inventoryDtos = inventoryRepository.findByBookBookId(book.getBookId())
                .stream()
                .map(inventory -> {
                    InventoryDto inventoryDto = new InventoryDto();
                    inventoryDto.setBookId(inventory.getBook().getBookId());
                    inventoryDto.setBranchId(inventory.getBranch().getBranchId());
                    inventoryDto.setTenChiNhanh(inventory.getBranch().getTenChiNhanh());
                    inventoryDto.setTongSoBan(inventory.getTotalCopies());
                    inventoryDto.setSoLuongHienCo(inventory.getAvailableCopies());
                    return inventoryDto;
                })
                .collect(Collectors.toList());
            dto.setInventories(inventoryDtos);
        } catch (Exception e) {
            // Xử lý trường hợp không lấy được inventory
            dto.setInventories(new ArrayList<>());
            System.err.println("Lỗi khi lấy inventory cho sách ID " + book.getBookId() + ": " + e.getMessage());
        }
        
        return dto;
    }

    /**
     * Cập nhật thông tin số lượng sách theo chi nhánh
     * @param bookId ID của sách
     * @param inventories Danh sách thông tin số lượng theo chi nhánh
     */
    private void updateInventories(Integer bookId, List<InventoryDto> inventories) {
        try {
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId));
            
            for (InventoryDto inventoryDto : inventories) {
                // Đảm bảo bookId đúng
                inventoryDto.setBookId(bookId);
                
                Branch branch = branchRepository.findById(inventoryDto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh với ID: " + inventoryDto.getBranchId()));
                
                // Tìm inventory tương ứng hoặc tạo mới
                Inventory inventory;
                try {
                    inventory = inventoryRepository.findByBookBookIdAndBranchBranchId(bookId, inventoryDto.getBranchId())
                        .orElse(new Inventory());
                    
                    if (inventory.getId() == null) {
                        inventory.setId(new Inventory.InventoryId(inventoryDto.getBranchId(), bookId));
                        inventory.setBook(book);
                        inventory.setBranch(branch);
                    }
                    
                    // Cập nhật số lượng
                    inventory.setTotalCopies(inventoryDto.getTongSoBan());
                    inventory.setAvailableCopies(inventoryDto.getSoLuongHienCo());
                    
                    inventoryRepository.save(inventory);
                } catch (Exception e) {
                    System.err.println("Lỗi khi cập nhật inventory cho sách ID " + bookId + 
                                      " tại chi nhánh ID " + inventoryDto.getBranchId() + ": " + e.getMessage());
                }
            }
            
            // Cập nhật tổng số lượng sách
            updateTotalBookQuantity(bookId);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật inventory cho sách ID " + bookId + ": " + e.getMessage());
        }
    }

    /**
     * Cập nhật tổng số lượng sách trong bảng Book
     * @param bookId ID của sách
     */
    private void updateTotalBookQuantity(Integer bookId) {
        try {
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId));
            
            // Tính tổng số lượng từ tất cả các chi nhánh
            Integer totalQuantity = 0;
            try {
                totalQuantity = inventoryRepository.findByBookBookId(bookId)
                    .stream()
                    .mapToInt(Inventory::getAvailableCopies)
                    .sum();
            } catch (Exception e) {
                System.err.println("Lỗi khi tính tổng số lượng sách cho ID " + bookId + ": " + e.getMessage());
                // Giữ nguyên số lượng hiện tại nếu có lỗi
                totalQuantity = book.getSoLuong();
            }
            
            // Cập nhật số lượng sách
            book.setSoLuong(totalQuantity);
            bookRepository.save(book);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật tổng số lượng sách cho ID " + bookId + ": " + e.getMessage());
        }
    }
}