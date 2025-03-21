<template>
  <div class="book-details-page py-6">
    <v-container>
      <!-- Back Button -->
      <div class="mb-4">
        <v-btn
          variant="text"
          prepend-icon="mdi-arrow-left"
          :to="{ name: 'Catalog' }"
          class="text-capitalize"
        >
          Quay lại danh sách
        </v-btn>
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
      
      <!-- Book Details -->
      <template v-else-if="book">
        <v-card class="mb-6">
          <v-row no-gutters>
            <!-- Book Cover -->
            <v-col cols="12" sm="4" md="3" class="pa-4">
              <v-img
                :src="book.hinhAnhSach || '/placeholder-book.jpg'"
                :alt="book.tuaSach"
                height="400"
                class="rounded"
                cover
                @error="handleImageError"
              ></v-img>
            </v-col>
            
            <!-- Book Info -->
            <v-col cols="12" sm="8" md="9" class="pa-4">
              <h1 class="text-h4 font-weight-bold mb-2">{{ book.tuaSach }}</h1>
              
              <!-- Authors -->
              <p class="text-subtitle-1 mb-2" v-if="book.authors && book.authors.length">
                <span class="font-weight-medium">Tác giả:</span>
                <span v-for="(author, index) in book.authors" :key="author.authorId">
                  {{ author.tenTacGia }}{{ index < book.authors.length - 1 ? ', ' : '' }}
                </span>
              </p>
              
              <!-- Categories -->
              <div class="mb-4" v-if="book.categories && book.categories.length">
                <v-chip
                  v-for="category in book.categories"
                  :key="category.categoryId"
                  class="mr-2 mb-2"
                  color="primary"
                  variant="flat"
                >
                  {{ category.categoryName }}
                </v-chip>
              </div>
              
              <!-- Year -->
              <p class="text-body-1 mb-4" v-if="book.namXuatBan">
                <span class="font-weight-medium">Năm xuất bản:</span> {{ book.namXuatBan }}
              </p>
              
              <!-- Description -->
              <div class="mb-6" v-if="book.moTa">
                <h3 class="text-h6 font-weight-medium mb-2">Mô tả</h3>
                <p class="text-body-1">{{ book.moTa }}</p>
              </div>
              
              <!-- Actions -->
              <div class="d-flex flex-wrap gap-2">
                <v-btn
                  color="primary"
                  prepend-icon="mdi-book-open-page-variant"
                  :disabled="!isLoggedIn"
                  @click="borrowBook"
                >
                  Mượn sách
                </v-btn>
                
                <v-btn
                  variant="outlined"
                  color="primary"
                  prepend-icon="mdi-heart"
                  :disabled="!isLoggedIn"
                  @click="toggleFavorite"
                >
                  Yêu thích
                </v-btn>
              </div>
              
              <!-- Login Prompt -->
              <v-alert
                v-if="!isLoggedIn"
                type="info"
                class="mt-6"
              >
                Bạn cần <router-link to="/login" class="font-weight-bold">đăng nhập</router-link> để mượn sách.
              </v-alert>
            </v-col>
          </v-row>
        </v-card>
      </template>
      
      <!-- Book not found -->
      <div v-else class="text-center my-12">
        <v-icon size="96" color="grey">mdi-book-off</v-icon>
        <h2 class="text-h5 mt-4">Không tìm thấy sách</h2>
        <p class="text-body-1 mt-2">Sách bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
        <v-btn
          class="mt-4"
          color="primary"
          :to="{ name: 'Catalog' }"
        >
          Quay lại danh sách sách
        </v-btn>
      </div>
    </v-container>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';

export default {
  name: 'BookDetailsView',
  data() {
    return {
      loading: true,
      error: null,
      book: null
    };
  },
  computed: {
    ...mapGetters('auth', ['isAuthenticated']),
    
    isLoggedIn() {
      return this.isAuthenticated;
    },
    
    bookId() {
      return parseInt(this.$route.params.id);
    }
  },
  methods: {
    ...mapActions('books', ['fetchBookById']),
    
    async loadBook() {
      this.loading = true;
      this.error = null;
      
      try {
        // Đảm bảo bookId là một số nguyên
        const bookId = parseInt(this.bookId);
        if (isNaN(bookId)) {
          throw new Error('ID sách không hợp lệ');
        }
        
        this.book = await this.fetchBookById(bookId);
      } catch (error) {
        console.error('Error fetching book:', error);
        this.error = 'Không thể tải thông tin sách. Vui lòng thử lại sau.';
      } finally {
        this.loading = false;
      }
    },
    
    handleImageError(event) {
      console.warn(`Failed to load image for book: ${this.book?.tuaSach}`);
      if (event && event.target) {
        event.target.src = '/placeholder-book.jpg';
      }
    },
    
    borrowBook() {
      if (!this.isLoggedIn) {
        this.$router.push({ 
          path: '/login',
          query: { redirect: this.$route.fullPath }
        });
        return;
      }
      
      // To be implemented
      alert('Chức năng mượn sách sẽ được phát triển trong phiên bản tiếp theo!');
    },
    
    toggleFavorite() {
      if (!this.isLoggedIn) {
        this.$router.push({ 
          path: '/login',
          query: { redirect: this.$route.fullPath }
        });
        return;
      }
      
      // To be implemented
      alert('Chức năng yêu thích sẽ được phát triển trong phiên bản tiếp theo!');
    }
  },
  created() {
    this.loadBook();
  }
};
</script>