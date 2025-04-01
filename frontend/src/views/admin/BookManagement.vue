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
                :src="getImageUrl(item.hinhAnhSach) || '/placeholder-book.png'"
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
            v-model="pagination.currentPage"
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
                    hint="URL hình ảnh trên Cloudinary (tự động cập nhật khi tải lên)"
                    variant="outlined"
                    readonly
                    disabled
                  ></v-text-field>
                  
                  <div class="d-flex align-center mt-2">
                    <v-file-input
                      accept="image/*"
                      label="Tải ảnh lên"
                      variant="outlined"
                      density="compact"
                      prepend-icon="mdi-camera"
                      @update:model-value="onFileSelected"
                      :loading="uploadingImage"
                      :disabled="uploadingImage"
                      hide-details
                    ></v-file-input>
                    
                    <v-progress-circular
                      v-if="uploadingImage"
                      indeterminate
                      color="primary"
                      size="24"
                      class="ml-2"
                    ></v-progress-circular>
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
                  <label class="v-label text-subtitle-1 pb-1">Mô tả</label>
                  <div class="text-editor-container">
                    <div class="editor-tools pa-2 d-flex align-center">
                      <v-btn
                        icon
                        variant="text"
                        size="small"
                        title="In đậm"
                        @click="formatText('bold')"
                        class="mr-2"
                      >
                        <v-icon>mdi-format-bold</v-icon>
                      </v-btn>
                      <v-btn
                        icon
                        variant="text"
                        size="small"
                        title="In nghiêng"
                        @click="formatText('italic')"
                        class="mr-2"
                      >
                        <v-icon>mdi-format-italic</v-icon>
                      </v-btn>
                      <v-btn
                        icon
                        variant="text"
                        size="small"
                        title="Xuống dòng"
                        @click="formatText('lineBreak')"
                      >
                        <v-icon>mdi-keyboard-return</v-icon>
                      </v-btn>
                    </div>
                    <div
                      ref="editor"
                      id="book-description-editor"
                      contenteditable="true"
                      class="content-editor pa-3"
                      @input="updateDescription"
                    ></div>
                    <div class="text-caption text-grey-darken-1 mt-1">
                      Nhập mô tả chi tiết của sách. Sử dụng các công cụ định dạng phía trên để tạo nội dung phong phú.
                    </div>
                  </div>
                </v-col>
                
                <!-- Số lượng sách theo chi nhánh -->
                <v-col cols="12">
                  <v-divider class="my-2"></v-divider>
                  <h3 class="text-h6 mb-3">Số lượng sách theo chi nhánh</h3>
                  
                  <v-row v-if="branches.length > 0">
                    <template v-for="(branch, index) in branches" :key="branch.branchId">
                      <v-col cols="12" md="6">
                        <v-card flat border class="pa-3 mb-2">
                          <div class="d-flex align-center mb-2">
                            <v-icon class="mr-2">mdi-store</v-icon>
                            <strong>{{ branch.tenChiNhanh }}</strong>
                          </div>
                          
                          <v-text-field
                            v-model="branchQuantities[branch.branchId]"
                            label="Số lượng sách tại chi nhánh"
                            type="number"
                            min="0"
                            variant="outlined"
                            density="compact"
                            hint="Số lượng sách có sẵn tại chi nhánh này"
                            class="mt-2"
                            @update:model-value="updateTotalQuantity"
                          ></v-text-field>
                        </v-card>
                      </v-col>
                    </template>
                  </v-row>
                  
                  <p v-else class="text-body-2">
                    Chưa có chi nhánh nào. Sách sẽ được thêm vào với số lượng mặc định.
                  </p>
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
import { mapActions, mapGetters } from 'vuex';

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
      branchQuantities: {}, // Lưu số lượng sách theo từng chi nhánh
      
      // Filters
      filters: {
        categoryId: null,
        authorId: null,
        search: ''
      },
      
      // Pagination
      pagination: {
        pageNo: 0,          // Cho backend (0-based)
        currentPage: 1,     // Cho frontend (1-based)
        pageSize: 10,
        totalItems: 0,
        totalPages: 0
      },
      
      // Dialog cho thêm/sửa sách
      bookDialog: {
        show: false,
        isEdit: false,
        loading: false,
        book: this.getEmptyBookObject()
      },
      
      // Dialog xác nhận xóa
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
  
  computed: {
    ...mapGetters('branches', ['branches']),
    ...mapGetters('inventory', ['inventories']),
  },
  
  methods: {
    ...mapActions('branches', ['fetchBranches']),
    ...mapActions('inventory', ['fetchInventoriesByBookId']),
    
    // Format text in the editor
    formatText(command) {
      // Focus the editor if not already focused
      this.$refs.editor.focus();
      
      // Execute different commands based on the button clicked
      if (command === 'bold') {
        document.execCommand('bold', false, null);
      } else if (command === 'italic') {
        document.execCommand('italic', false, null);
      } else if (command === 'lineBreak') {
        document.execCommand('insertHTML', false, '<br>');
      }
    },
    
    // Update the book description when the content of the editor changes
    updateDescription() {
      if (this.$refs.editor) {
        // Format and save the content to the book model
        this.bookDialog.book.moTa = this.formatContentForSaving(this.$refs.editor.innerHTML);
      }
    },
    
    // Format HTML content from editor to save in database (matching Test/script.js)
    formatContentForSaving(html) {
      // Create a temporary div to work with the HTML
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = html;
      
      // Replace <br> and <div> (new lines in contenteditable) with \n
      let text = tempDiv.innerHTML
          .replace(/<br\s*\/?>/gi, '\\n')
          .replace(/<div\s*\/?>/gi, '\\n')
          .replace(/<\/div>/gi, '');
      
      // Replace <i> and <em> with *
      text = text.replace(/<(i|em)>(.*?)<\/(i|em)>/gi, function(match, p1, p2) {
          return '*' + p2 + '*';
      });
      
      // Replace <b> and <strong> with ** (after handling italics to avoid conflicts)
      text = text.replace(/<(b|strong)>(.*?)<\/(b|strong)>/gi, function(match, p1, p2) {
          return '**' + p2 + '**';
      });
      
      // Clean up any leftover HTML tags
      text = text
          .replace(/<[^>]*>/g, '')
          .replace(/&nbsp;/g, ' ')
          .replace(/\\n\\n/g, '\\n') // Fix double line breaks
          .trim();
      
      return text;
    },
    
    // Format text from database format to HTML display (matching Test/script.js)
    formatContentForDisplay(text) {
      if (!text) return '';
      
      // Replace \n with <br> for line breaks
      let formattedText = text.replace(/\\n/g, '<br>');
      
      // Process bold and italic formatting in a specific order to avoid conflicts
      
      // 1. Replace text between **** with bold tags
      formattedText = formattedText.replace(/\*\*\*\*(.*?)\*\*\*\*/g, '<strong>$1</strong>');
      
      // 2. Replace text between ** with bold tags
      formattedText = formattedText.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
      
      // 3. Replace text between _ _ with italic tags
      formattedText = formattedText.replace(/_(.*?)_/g, '<em>$1</em>');
      
      // 4. Replace text between single * * with italic tags (after handling ** for bold)
      formattedText = formattedText.replace(/\*(.*?)\*/g, '<em>$1</em>');
      
      return formattedText;
    },
    
    async fetchBooks() {
      console.log(`Fetching books: backend pageNo=${this.pagination.pageNo}, frontend page=${this.pagination.currentPage}`);
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
        
        // Đảm bảo currentPage và pageNo đồng bộ
        if (this.pagination.currentPage !== this.pagination.pageNo + 1) {
          this.pagination.currentPage = this.pagination.pageNo + 1;
        }
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
      this.pagination.currentPage = 1;
      this.fetchBooks();
    },
    
    handlePageChange(page) {
      console.log(`Page changed: frontend page=${page}`);
      // Convert từ 1-based (frontend) sang 0-based (backend)
      this.pagination.pageNo = page - 1;
      this.pagination.currentPage = page;
      this.fetchBooks();
      // Scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    
    // Open dialog to add or edit a book
    async openBookDialog(book = null) {
      // Reset branch quantities
      this.branchQuantities = {};
      
      // Fetch branches if not loaded
      if (this.branches.length === 0) {
        await this.fetchBranches();
      }
      
      if (book) {
        // Edit mode - clone the book to avoid direct mutation
        this.bookDialog.isEdit = true;
        this.bookDialog.book = {
          bookId: book.bookId,
          tuaSach: book.tuaSach,
          moTa: book.moTa,
          namXuatBan: book.namXuatBan,
          hinhAnhSach: book.hinhAnhSach,
          soLuong: book.soLuong || 0,
          authorIds: book.authors?.map(a => a.authorId) || [],
          categoryIds: book.categories?.map(c => c.categoryId) || []
        };
        
        // Fetch inventory data for this book
        try {
          const inventories = await this.fetchInventoriesByBookId(book.bookId);
          // Populate branch quantities
          inventories.forEach(inv => {
            this.branchQuantities[inv.branchId] = inv.soLuongHienCo;
          });
          
          // Calculate total from branch quantities
          this.updateTotalQuantity();
        } catch (error) {
          console.error('Error fetching inventories:', error);
          this.$toast.error('Không thể tải thông tin số lượng sách theo chi nhánh');
        }
      } else {
        // Add mode
        this.bookDialog.isEdit = false;
        this.bookDialog.book = this.getEmptyBookObject();
        
        // Initialize branch quantities to 0
        this.branches.forEach(branch => {
          this.branchQuantities[branch.branchId] = 0;
        });
      }
      
      this.bookDialog.show = true;
      
      // Set a short timeout to ensure the DOM is updated before setting the editor content
      this.$nextTick(() => {
        // Initialize the rich text editor with formatted content
        if (this.$refs.editor) {
          const formattedContent = this.formatContentForDisplay(this.bookDialog.book.moTa);
          this.$refs.editor.innerHTML = formattedContent;
        }
      });
    },
    
    // Create an empty book object for the form
    getEmptyBookObject() {
      return {
        tuaSach: '',
        moTa: '',
        namXuatBan: new Date().getFullYear(),
        hinhAnhSach: '',
        soLuong: 0,
        authorIds: [],
        categoryIds: [],
        inventories: []
      };
    },
    
    // Update total quantity when branch quantities change
    updateTotalQuantity() {
      // Tính tổng số lượng từ tất cả các chi nhánh
      let total = 0;
      
      for (const branchId in this.branchQuantities) {
        const quantity = parseInt(this.branchQuantities[branchId]) || 0;
        total += quantity;
      }
      
      // Cập nhật tổng số lượng vào model
      this.bookDialog.book.soLuong = total;
    },
    
    // Save (create or update) book
    async saveBook() {
      // Validate soLuong based on branches
      this.updateTotalQuantity();
      
      // Form validation
      if (!this.bookDialog.book.tuaSach) {
        this.$toast.error('Tên sách là trường bắt buộc.');
        return;
      }
      
      this.bookDialog.loading = true;
      this.error = null; // Clear previous errors
      
      try {
        // Check for proper authentication
        if (!this.checkAuthForManagement()) return;
        
        // Prepare inventories data
        const inventories = this.prepareInventoriesData();
        this.bookDialog.book.inventories = inventories;
        
        console.log('Saving book with inventory data:', this.bookDialog.book);
        
        let response;
        if (this.bookDialog.isEdit) {
          // Update existing book
          response = await apiService.books.update(this.bookDialog.book.bookId, this.bookDialog.book);
          this.$toast.success('Cập nhật sách thành công!');
        } else {
          // Create new book
          response = await apiService.books.create(this.bookDialog.book);
          this.$toast.success('Thêm sách mới thành công!');
        }
        
        console.log('Save response:', response);
        
        // Close dialog and refresh books
        this.bookDialog.show = false;
        this.fetchBooks();
        return true; // Indicate success
      } catch (error) {
        console.error('Error saving book:', error);
        
        // Enhanced error information
        let errorMessage = 'Không thể lưu thông tin sách. ';
        if (error.response) {
          errorMessage += error.response.data?.message || 
                       `Lỗi status: ${error.response.status}`;
          console.error('Server response:', error.response.data);
        } else if (error.request) {
          errorMessage += 'Không nhận được phản hồi từ máy chủ.';
        } else {
          errorMessage += error.message || 'Lỗi không xác định.';
        }
        
        this.error = errorMessage;
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
        // Check for proper authentication
        if (!this.checkAuthForManagement()) return;
        
        console.log('Deleting book ID:', this.deleteDialog.book.bookId);
        
        await apiService.books.delete(this.deleteDialog.book.bookId);
        this.$toast.success('Xóa sách thành công!');
        
        // Close dialog and refresh books
        this.deleteDialog.show = false;
        this.fetchBooks();
        return true; // Indicate success
      } catch (error) {
        console.error('Error deleting book:', error);
        
        // Enhanced error information
        let errorMessage = 'Không thể xóa sách. ';
        if (error.response) {
          errorMessage += error.response.data?.message || 
                       `Lỗi status: ${error.response.status}`;
        } else if (error.request) {
          errorMessage += 'Không nhận được phản hồi từ máy chủ.';
        } else {
          errorMessage += error.message || 'Lỗi không xác định.';
        }
        
        this.error = errorMessage;
        this.$toast.error(this.error);
        return false; // Indicate failure
      } finally {
        this.deleteDialog.loading = false;
      }
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
        // Check for proper authentication
        if (!this.checkAuthForManagement()) return;
        
        // Create form data for file upload
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        
        // Upload the file to the server
        const response = await apiService.files.upload(formData);
        
        // Update the image URL field with the returned URL
        if (response.data && response.data.fileUrl) {
          this.bookDialog.book.hinhAnhSach = response.data.fileUrl;
          
          // Show success message
          this.$toast.success('Tải ảnh lên thành công');
          console.log('Upload successful, new image URL:', this.bookDialog.book.hinhAnhSach);
        }
      } catch (error) {
        console.error('Error uploading image:', error);
        this.$toast.error('Lỗi khi tải ảnh lên: ' + (error.response?.data?.error || error.message));
      } finally {
        this.uploadingImage = false;
      }
    },
    
    // Get proper image URL for display
    getImageUrl(url) {
      if (!url) return '/placeholder-book.jpg';
      
      // Nếu đã là URL Cloudinary, trả về nguyên đường dẫn
      if (url.match(/^https?:\/\//)) {
        return url;
      }
      
      // Fallback cho placeholder
      return '/placeholder-book.jpg';
    },
    
    // Handle image loading errors
    handleImageError(event, item) {
      console.warn(`Failed to load image for book: ${item?.tuaSach || 'Unknown'}`);
      
      // Set a default placeholder image if event and target exist
      if (event && event.target) {
        event.target.src = '/placeholder-book.png';
      }
    },
    
    // Check authentication and permissions for management operations
    checkAuthForManagement() {
      // Check if user is logged in
      const token = localStorage.getItem('token');
      const user = JSON.parse(localStorage.getItem('user'));
      
      if (!token || !user) {
        this.$toast.error('Bạn cần đăng nhập để thực hiện chức năng này');
        return false;
      }
      
      // Check role
      const userRole = user.role?.toLowerCase();
      if (userRole !== 'admin' && userRole !== 'manager') {
        this.$toast.error('Bạn cần có quyền quản trị hoặc quản lý để thực hiện chức năng này');
        return false;
      }
      
      return true;
    },
    
    // Prepare inventory data before saving
    prepareInventoriesData() {
      const inventories = [];
      
      // Create inventory objects for each branch
      Object.keys(this.branchQuantities).forEach(branchId => {
        const quantity = parseInt(this.branchQuantities[branchId]) || 0;
        inventories.push({
          branchId: parseInt(branchId),
          bookId: this.bookDialog.book.bookId || null,
          tongSoBan: quantity,
          soLuongHienCo: quantity
        });
      });
      
      return inventories;
    }
  },
  
  async created() {
    try {
      // Fetch initial data
      await Promise.all([
        this.fetchCategories(),
        this.fetchAuthors(),
        this.fetchBranches()
      ]);
      
      // Fetch books
      this.fetchBooks();
    } catch (error) {
      console.error('Error initializing page:', error);
      this.error = 'Không thể tải dữ liệu ban đầu. Vui lòng thử lại sau.';
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

.text-editor-container {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-top: 5px;
  overflow: hidden;
}

.editor-tools {
  background-color: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
}

.content-editor {
  min-height: 150px;
  background-color: white;
  outline: none;
  line-height: 1.5;
}

.content-editor:focus {
  box-shadow: inset 0 0 0 2px rgba(0, 0, 0, 0.05);
}
</style>