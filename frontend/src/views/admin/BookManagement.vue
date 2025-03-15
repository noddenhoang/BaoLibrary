<template>
  <div class="book-management-page py-6">
    <v-container>
      <div class="d-flex justify-space-between align-center mb-6">
        <h1 class="text-h4 font-weight-bold">Quản lý sách</h1>
        <v-btn 
          color="primary" 
          prepend-icon="mdi-plus" 
          @click="openBookDialog()"
        >
          Thêm sách mới
        </v-btn>
      </div>

      <!-- Search and Filters -->
      <v-card class="mb-6" variant="outlined">
        <v-card-text>
          <v-row>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="search"
                label="Tìm kiếm sách"
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                density="compact"
                hide-details
                @update:model-value="debouncedSearch"
                clearable
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="3">
              <v-select
                v-model="filters.categoryId"
                :items="categories"
                item-title="categoryName"
                item-value="categoryId"
                label="Danh mục"
                variant="outlined"
                density="compact"
                hide-details
                clearable
                @update:model-value="fetchBooks"
              ></v-select>
            </v-col>
            <v-col cols="12" md="3">
              <v-select
                v-model="filters.authorId"
                :items="authors"
                item-title="tenTacGia"
                item-value="authorId"
                label="Tác giả"
                variant="outlined"
                density="compact"
                hide-details
                clearable
                @update:model-value="fetchBooks"
              ></v-select>
            </v-col>
            <v-col cols="12" md="2">
              <v-btn 
                color="secondary" 
                variant="text" 
                block 
                @click="resetFilters"
                prepend-icon="mdi-filter-remove"
              >
                Xóa lọc
              </v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
      
      <!-- Loading State -->
      <div v-if="loading" class="d-flex justify-center align-center pa-10">
        <v-progress-circular indeterminate color="primary" size="50"></v-progress-circular>
      </div>

      <!-- Error State -->
      <v-alert
        v-else-if="error"
        type="error"
        class="mb-6"
      >
        {{ error }}
      </v-alert>

      <!-- Empty State -->
      <v-card
        v-else-if="books.length === 0"
        class="d-flex flex-column justify-center align-center pa-10 text-center"
        variant="outlined"
      >
        <v-icon size="64" color="grey">mdi-bookshelf</v-icon>
        <h3 class="text-h6 mt-4 text-grey-darken-1">Không tìm thấy sách nào</h3>
        <p class="text-body-1 mt-2 text-grey">Thêm sách mới hoặc điều chỉnh bộ lọc tìm kiếm</p>
      </v-card>

      <!-- Books Table -->
      <v-card v-else variant="outlined">
        <v-data-table
          :headers="headers"
          :items="books"
          :items-per-page="10"
          :loading="loading"
          class="elevation-0"
        >
          <!-- Book Cover -->
          <template v-slot:item.hinhAnhSach="{ item }">
            <div class="d-flex align-center py-2">
              <v-img
                :src="item.hinhAnhSach || '/placeholder-book.png'"
                :alt="item.tuaSach"
                width="60"
                height="80"
                cover
                class="rounded"
                @error="handleImageError($event, item)"
              ></v-img>
            </div>
          </template>

          <!-- Authors Column -->
          <template v-slot:item.authors="{ item }">
            <div v-if="item.authors && item.authors.length">
              <v-chip
                v-for="author in item.authors"
                :key="author.authorId"
                size="small"
                class="mr-1 mb-1"
              >
                {{ author.tenTacGia }}
              </v-chip>
            </div>
            <span v-else class="text-grey">Chưa có tác giả</span>
          </template>

          <!-- Categories Column -->
          <template v-slot:item.categories="{ item }">
            <div v-if="item.categories && item.categories.length">
              <v-chip
                v-for="category in item.categories"
                :key="category.categoryId"
                size="small"
                color="primary"
                class="mr-1 mb-1"
              >
                {{ category.categoryName }}
              </v-chip>
            </div>
            <span v-else class="text-grey">Chưa phân loại</span>
          </template>

          <!-- Actions Column -->
          <template v-slot:item.actions="{ item }">
            <div class="d-flex">
              <v-tooltip text="Chỉnh sửa">
                <template v-slot:activator="{ props }">
                  <v-btn
                    icon
                    variant="text"
                    size="small"
                    v-bind="props"
                    @click="openBookDialog(item)"
                  >
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip text="Xóa">
                <template v-slot:activator="{ props }">
                  <v-btn
                    icon
                    variant="text"
                    size="small"
                    color="error"
                    v-bind="props"
                    @click="confirmDelete(item)"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>
            </div>
          </template>
        </v-data-table>

        <!-- Pagination -->
        <v-card-actions class="justify-center pa-4">
          <v-pagination
            v-model="pagination.pageNo"
            :length="pagination.totalPages"
            @update:model-value="handlePageChange"
          ></v-pagination>
        </v-card-actions>
      </v-card>
    </v-container>

    <!-- Book Dialog (Add/Edit) -->
    <v-dialog v-model="bookDialog.show" max-width="800px" persistent>
      <v-card>
        <v-card-title class="text-h5 bg-primary text-white pa-4">
          {{ bookDialog.isEdit ? 'Chỉnh sửa sách' : 'Thêm sách mới' }}
        </v-card-title>

        <v-card-text class="pa-4">
          <v-form ref="bookForm" @submit.prevent="saveBook">
            <v-container>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="bookDialog.book.tuaSach"
                    label="Tên sách"
                    :rules="[(v) => !!v || 'Tên sách là bắt buộc']"
                    required
                    variant="outlined"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="bookDialog.book.namXuatBan"
                    type="number"
                    label="Năm xuất bản"
                    hint="Nhập năm xuất bản của sách"
                    variant="outlined"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="bookDialog.book.hinhAnhSach"
                    label="URL hình ảnh"
                    hint="Nhập URL hình ảnh của sách hoặc tải lên từ máy tính"
                    variant="outlined"
                    readonly
                  ></v-text-field>
                  
                  <div class="d-flex align-center mt-2">
                    <v-file-input
                      accept="image/*"
                      label="Tải ảnh lên"
                      variant="outlined"
                      density="compact"
                      prepend-icon="mdi-camera"
                      @update:model-value="onFileSelected"
                      :disabled="bookDialog.loading"
                      hide-details
                    ></v-file-input>
                    
                    <v-btn
                      v-if="selectedFile"
                      color="primary"
                      class="ml-2"
                      size="small"
                      :loading="uploadingImage"
                      @click="uploadImage"
                    >
                      Tải lên
                    </v-btn>
                  </div>
                  
                  <div v-if="bookDialog.book.hinhAnhSach" class="mt-2">
                    <v-img
                      :src="bookDialog.book.hinhAnhSach"
                      height="100"
                      width="75"
                      cover
                      class="rounded"
                    ></v-img>
                  </div>
                </v-col>

                <v-col cols="12" md="6">
                  <v-autocomplete
                    v-model="bookDialog.book.categoryIds"
                    :items="categories"
                    item-title="categoryName"
                    item-value="categoryId"
                    label="Danh mục"
                    multiple
                    chips
                    closable-chips
                    variant="outlined"
                  ></v-autocomplete>
                </v-col>

                <v-col cols="12" md="6">
                  <v-autocomplete
                    v-model="bookDialog.book.authorIds"
                    :items="authors"
                    item-title="tenTacGia"
                    item-value="authorId"
                    label="Tác giả"
                    multiple
                    chips
                    closable-chips
                    variant="outlined"
                  ></v-autocomplete>
                </v-col>

                <v-col cols="12">
                  <v-textarea
                    v-model="bookDialog.book.moTa"
                    label="Mô tả"
                    hint="Mô tả nội dung của sách"
                    auto-grow
                    variant="outlined"
                    rows="3"
                  ></v-textarea>
                </v-col>
              </v-row>
            </v-container>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="bookDialog.show = false"
          >
            Hủy
          </v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            :loading="bookDialog.loading"
            @click="saveBook"
          >
            {{ bookDialog.isEdit ? 'Cập nhật' : 'Thêm mới' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog.show" max-width="500">
      <v-card>
        <v-card-title class="text-h5 bg-error text-white pa-4">
          Xác nhận xóa sách
        </v-card-title>
        <v-card-text class="pa-4 pt-6">
          <p>Bạn có chắc chắn muốn xóa sách "<strong>{{ deleteDialog.book?.tuaSach }}</strong>"?</p>
          <p class="text-grey mt-2">Hành động này không thể hoàn tác.</p>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="deleteDialog.show = false"
          >
            Hủy
          </v-btn>
          <v-btn
            color="error"
            variant="elevated"
            :loading="deleteDialog.loading"
            @click="deleteBook"
          >
            Xác nhận xóa
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import apiService from '@/services/api.service';

export default {
  name: 'BookManagementPage',
  
  data() {
    return {
      books: [],
      categories: [],
      authors: [],
      loading: false,
      error: null,
      search: '',
      selectedFile: null,
      uploadingImage: false,
      
      // Filters
      filters: {
        categoryId: null,
        authorId: null,
        search: ''
      },
      
      // Pagination
      pagination: {
        pageNo: 0,
        pageSize: 10,
        totalItems: 0,
        totalPages: 0
      },
      
      // Book Dialog
      bookDialog: {
        show: false,
        isEdit: false,
        loading: false,
        book: this.getEmptyBookObject()
      },
      
      // Delete Dialog
      deleteDialog: {
        show: false,
        loading: false,
        book: null
      },
      
      // Table Headers
      headers: [
        { title: 'Hình ảnh', key: 'hinhAnhSach', sortable: false, width: '80px' },
        { title: 'Tên sách', key: 'tuaSach', sortable: true },
        { title: 'Năm xuất bản', key: 'namXuatBan', sortable: true, width: '150px' },
        { title: 'Tác giả', key: 'authors', sortable: false },
        { title: 'Danh mục', key: 'categories', sortable: false },
        { title: 'Thao tác', key: 'actions', sortable: false, align: 'end', width: '120px' }
      ]
    };
  },
  
  created() {
    this.debouncedSearch = debounce(this.handleSearchChange, 500);
    this.fetchCategories();
    this.fetchAuthors();
    this.fetchBooks();
  },
  
  methods: {
    async fetchBooks() {
      this.loading = true;
      this.error = null;
      
      try {
        // Prepare query parameters
        const params = {
          pageNo: this.pagination.pageNo,
          pageSize: this.pagination.pageSize,
          sortBy: 'tuaSach',
          sortDir: 'asc'
        };
        
        // Add filters if they exist
        if (this.filters.search) params.keyword = this.filters.search;
        
        let response;
        
        // Check if we need to filter by category or author
        if (this.filters.categoryId) {
          response = await apiService.books.getByCategory(this.filters.categoryId, params);
        } else if (this.filters.authorId) {
          response = await apiService.books.getByAuthor(this.filters.authorId, params);
        } else if (this.filters.search) {
          response = await apiService.books.search(this.filters.search, params);
        } else {
          response = await apiService.books.getAll(params);
        }
        
        // Update books and pagination data
        this.books = response.data.content;
        this.pagination.totalItems = response.data.totalElements;
        this.pagination.totalPages = response.data.totalPages;
      } catch (error) {
        console.error('Error fetching books:', error);
        this.error = 'Không thể tải danh sách sách. Vui lòng thử lại sau.';
      } finally {
        this.loading = false;
      }
    },
    
    async fetchCategories() {
      try {
        const response = await apiService.categories.getAll();
        this.categories = response.data;
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    },
    
    async fetchAuthors() {
      try {
        const response = await apiService.authors.getAll();
        this.authors = response.data;
      } catch (error) {
        console.error('Error fetching authors:', error);
      }
    },
    
    handleSearchChange() {
      this.filters.search = this.search;
      this.pagination.pageNo = 0; // Reset to first page
      this.fetchBooks();
    },
    
    resetFilters() {
      this.search = '';
      this.filters = {
        categoryId: null,
        authorId: null,
        search: ''
      };
      this.pagination.pageNo = 0;
      this.fetchBooks();
    },
    
    handlePageChange(page) {
      this.pagination.pageNo = page - 1; // Convert from 1-based to 0-based
      this.fetchBooks();
      // Scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    
    // Open dialog to add or edit a book
    openBookDialog(book = null) {
      if (book) {
        // Edit mode - clone the book to avoid direct mutation
        this.bookDialog.isEdit = true;
        this.bookDialog.book = {
          bookId: book.bookId,
          tuaSach: book.tuaSach,
          moTa: book.moTa,
          namXuatBan: book.namXuatBan,
          hinhAnhSach: book.hinhAnhSach,
          authorIds: book.authors?.map(a => a.authorId) || [],
          categoryIds: book.categories?.map(c => c.categoryId) || []
        };
      } else {
        // Add mode
        this.bookDialog.isEdit = false;
        this.bookDialog.book = this.getEmptyBookObject();
      }
      this.bookDialog.show = true;
    },
    
    // Save (create or update) book
    async saveBook() {
      // Form validation
      if (!this.bookDialog.book.tuaSach) {
        this.$toast.error('Tên sách là trường bắt buộc.');
        return;
      }
      
      this.bookDialog.loading = true;
      this.error = null; // Clear previous errors
      
      try {
        if (this.bookDialog.isEdit) {
          // Update existing book
          await apiService.books.update(this.bookDialog.book.bookId, this.bookDialog.book);
          this.$toast.success('Cập nhật sách thành công!');
        } else {
          // Create new book
          await apiService.books.create(this.bookDialog.book);
          this.$toast.success('Thêm sách mới thành công!');
        }
        
        // Close dialog and refresh books
        this.bookDialog.show = false;
        this.fetchBooks();
        return true; // Indicate success
      } catch (error) {
        console.error('Error saving book:', error);
        this.error = error.response?.data?.message || 'Không thể lưu thông tin sách. Vui lòng thử lại sau.';
        this.$toast.error(this.error);
        return false; // Indicate failure
      } finally {
        this.bookDialog.loading = false;
      }
    },
    
    // Show delete confirmation dialog
    confirmDelete(book) {
      this.deleteDialog.book = book;
      this.deleteDialog.show = true;
    },
    
    // Delete book
    async deleteBook() {
      this.deleteDialog.loading = true;
      this.error = null; // Clear previous errors
      
      try {
        await apiService.books.delete(this.deleteDialog.book.bookId);
        this.$toast.success('Xóa sách thành công!');
        
        // Close dialog and refresh books
        this.deleteDialog.show = false;
        this.fetchBooks();
        return true; // Indicate success
      } catch (error) {
        console.error('Error deleting book:', error);
        this.error = error.response?.data?.message || 'Không thể xóa sách. Vui lòng thử lại sau.';
        this.$toast.error(this.error);
        return false; // Indicate failure
      } finally {
        this.deleteDialog.loading = false;
      }
    },
    
    // Create an empty book object for the form
    getEmptyBookObject() {
      return {
        tuaSach: '',
        moTa: '',
        namXuatBan: new Date().getFullYear(),
        hinhAnhSach: '',
        authorIds: [],
        categoryIds: []
      };
    },
    
    // Handle file selection
    onFileSelected(file) {
      this.selectedFile = file;
      // Auto-upload when file is selected
      if (file) {
        this.uploadImage();
      }
    },
    
    // Upload image to server
    async uploadImage() {
      if (!this.selectedFile) {
        return;
      }
      
      this.uploadingImage = true;
      
      try {
        // Create form data for file upload
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        
        // Upload the file to the server
        const response = await apiService.files.upload(formData);
        
        // Update the image URL field with the returned URL
        if (response.data && response.data.fileDownloadUri) {
          this.bookDialog.book.hinhAnhSach = response.data.fileDownloadUri;
          // Show success message
          this.$toast.success('Tải ảnh lên thành công');
        }
      } catch (error) {
        console.error('Error uploading image:', error);
        this.$toast.error('Lỗi khi tải ảnh lên: ' + (error.response?.data?.message || error.message));
      } finally {
        this.uploadingImage = false;
        this.selectedFile = null;
      }
    },
    
    // Handle image loading errors
    handleImageError(event, item) {
      console.warn(`Failed to load image for book: ${item.tuaSach}`);
      // Set a default placeholder image
      event.target.src = '/placeholder-book.png';
    }
  }
};
</script>

<style scoped>
/* Add any custom styles here */
.v-data-table :deep(th) {
  font-weight: bold;
  background-color: #f5f5f5;
}

.v-card-title.text-h5 {
  font-weight: bold;
}
</style>