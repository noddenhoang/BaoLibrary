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
          size="64"
          color="primary"
        ></v-progress-circular>
      </div>
      
      <!-- Books Grid/List View -->
      <div v-else>
        <!-- Show books in grid view -->
        <div v-if="viewMode === 'grid'" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <book-card
            v-for="book in pagedBooks.content"
            :key="book.bookId"
            :book="book"
          />
        </div>
        
        <!-- Show books in list view -->
        <book-list
          v-else
          :books="pagedBooks.content"
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
      </div>
    </v-container>
  </div>
</template>

<script>
import BookCard from '@/components/book/BookCard.vue';
import BookList from '@/components/book/BookList.vue';
import BookFilters from '@/components/book/BookFilters.vue';
import BookPagination from '@/components/book/BookPagination.vue';
import ViewToggle from '@/components/common/ViewToggle.vue';
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'CatalogView',
  components: {
    BookCard,
    BookList,
    BookFilters,
    BookPagination,
    ViewToggle
  },
  data() {
    return {
      viewMode: localStorage.getItem('viewMode') || 'grid',
      loading: true,
      pagination: {
        pageNo: 0,
        pageSize: 12,
        sortBy: 'tuaSach',
        sortDir: 'asc'
      },
      filters: {}
    };
  },
  computed: {
    ...mapGetters('books', ['pagedBooks']),
    ...mapGetters('categories', ['categories']),
    ...mapGetters('authors', ['authors'])
  },
  methods: {
    ...mapActions('books', ['fetchBooks', 'searchBooks', 'fetchBooksByCategory', 'fetchBooksByAuthor']),
    ...mapActions('categories', ['fetchCategories']),
    ...mapActions('authors', ['fetchAuthors']),
    
    handleFilterChange(filters) {
      this.filters = { ...filters };
      this.pagination.pageNo = 0;
      this.applyFilters();
    },
    
    handleViewChange(view) {
      this.viewMode = view;
      localStorage.setItem('viewMode', view);
    },
    
    handlePageChange(page) {
      this.pagination.pageNo = page;
      this.applyFilters();
    },
    
    handlePageSizeChange(size) {
      this.pagination.pageSize = size;
      this.pagination.pageNo = 0;
      this.applyFilters();
    },
    
    applyFilters() {
      this.loading = true;
      
      const { keyword, categoryId, authorId, sortBy, sortDir } = this.filters;
      
      if (sortBy) this.pagination.sortBy = sortBy;
      if (sortDir) this.pagination.sortDir = sortDir;
      
      if (categoryId) {
        this.fetchBooksByCategory({ 
          categoryId, 
          ...this.pagination 
        }).finally(() => this.loading = false);
      } else if (authorId) {
        this.fetchBooksByAuthor({ 
          authorId, 
          ...this.pagination 
        }).finally(() => this.loading = false);
      } else if (keyword) {
        this.searchBooks({ 
          keyword, 
          ...this.pagination 
        }).finally(() => this.loading = false);
      } else {
        this.fetchBooks(this.pagination)
          .finally(() => this.loading = false);
      }
    }
  },
  async created() {
    try {
      await Promise.all([
        this.fetchCategories(),
        this.fetchAuthors()
      ]);
      
      this.applyFilters();
    } catch (error) {
      console.error('Error initializing catalog:', error);
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.catalog-page {
  min-height: 70vh;
}
</style>