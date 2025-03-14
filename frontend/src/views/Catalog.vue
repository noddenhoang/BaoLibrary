<template>
  <div class="catalog-page py-6">
    <v-container>
      <h1 class="text-3xl font-bold mb-6 text-gray-900 dark:text-white">Thư viện sách</h1>
      
      <!-- Book Filters -->
      <book-filters
        :categories="categories"
        :authors="authors"
        @filter-changed="handleFilterChange"
      />
      
      <!-- Action Bar: View Toggle -->
      <div class="d-flex justify-space-between align-center mb-4">
        <div>
          <p class="text-subtitle-1">
            {{ loading ? 'Đang tải...' : `Hiển thị ${pagedBooks.content ? pagedBooks.content.length : 0} trên ${pagedBooks.totalElements || 0} kết quả` }}
          </p>
        </div>
        <view-toggle
          :view="viewMode"
          @view-changed="handleViewChange"
        />
      </div>
      
      <!-- Loading State -->
      <div v-if="loading" class="d-flex justify-center my-12">
        <v-progress-circular
          indeterminate
          color="primary"
          size="64"
        ></v-progress-circular>
      </div>
      
      <!-- Error State -->
      <v-alert
        v-else-if="error"
        type="error"
        variant="tonal"
        class="my-4"
      >
        {{ error }}
      </v-alert>
      
      <!-- Book Results -->
      <template v-else>
        <!-- Grid View -->
        <book-grid
          v-if="viewMode === 'grid'"
          :books="pagedBooks.content || []"
        />
        
        <!-- List View -->
        <book-list
          v-else
          :books="pagedBooks.content || []"
        />
        
        <!-- Pagination -->
        <book-pagination
          v-if="pagedBooks.totalElements > 0"
          :total-items="pagedBooks.totalElements"
          :total-pages="pagedBooks.totalPages"
          :current-page-no="pagination.pageNo"
          :current-page-size="pagination.pageSize"
          @page-changed="handlePageChange"
          @size-changed="handlePageSizeChange"
        />
      </template>
    </v-container>
  </div>
</template>

<script>
import apiService from '@/services/api.service';
import BookFilters from '@/components/book/BookFilters.vue';
import ViewToggle from '@/components/book/ViewToggle.vue';
import BookGrid from '@/components/book/BookGrid.vue';
import BookList from '@/components/book/BookList.vue';
import BookPagination from '@/components/book/BookPagination.vue';

export default {
  name: 'CatalogPage',
  components: {
    BookFilters,
    ViewToggle,
    BookGrid,
    BookList,
    BookPagination
  },
  data() {
    return {
      viewMode: localStorage.getItem('catalogViewMode') || 'grid',
      pagedBooks: {
        content: [],
        totalElements: 0,
        totalPages: 0,
        pageNo: 0,
        pageSize: 10,
        last: true
      },
      filters: {
        keyword: '',
        categoryId: null,
        authorId: null,
        year: null,
        sortBy: 'tuaSach',
        sortDir: 'asc'
      },
      pagination: {
        pageNo: 0,
        pageSize: 10
      },
      categories: [],
      authors: [],
      loading: false,
      error: null
    };
  },
  methods: {
    async fetchData() {
      this.loading = true;
      this.error = null;
      
      try {
        let endpoint = '/books';
        let params = {
          pageNo: this.pagination.pageNo,
          pageSize: this.pagination.pageSize,
          sortBy: this.filters.sortBy,
          sortDir: this.filters.sortDir
        };
        
        // Handle search query
        if (this.filters.keyword) {
          endpoint = '/books/search';
          params.keyword = this.filters.keyword;
        }
        
        // Handle category filter
        if (this.filters.categoryId) {
          endpoint = `/books/category/${this.filters.categoryId}`;
        }
        
        // Handle author filter
        if (this.filters.authorId) {
          endpoint = `/books/author/${this.filters.authorId}`;
        }
        
        // Fetch books based on filters and pagination
        const response = await apiService.books.getAll(params);
        this.pagedBooks = response.data;
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
    
    handleFilterChange(filters) {
      this.filters = { ...filters };
      this.pagination.pageNo = 0; // Reset to first page on filter change
      this.fetchData();
    },
    
    handleViewChange(view) {
      this.viewMode = view;
      localStorage.setItem('catalogViewMode', view);
    },
    
    handlePageChange(page) {
      this.pagination.pageNo = page;
      this.fetchData();
      // Scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    
    handlePageSizeChange(size) {
      this.pagination.pageSize = size;
      this.pagination.pageNo = 0; // Reset to first page when changing page size
      this.fetchData();
    }
  },
  mounted() {
    // Load initial data
    this.fetchData();
    this.fetchCategories();
    this.fetchAuthors();
  }
};
</script>

<style scoped>
.catalog-page {
  min-height: 70vh;
}
</style> 