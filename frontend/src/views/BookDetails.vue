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
                :src="book.hinhAnhSach || 'https://via.placeholder.com/300x400/e0e0e0/666666?text=No+Image'"
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
              <p class="text-body-1 mb-2" v-if="book.namXuatBan">
                <span class="font-weight-medium">Năm xuất bản:</span> {{ book.namXuatBan }}
              </p>
              
              <!-- Quantity -->
              <p class="text-body-1 mb-4">
                <span class="font-weight-medium">Số lượng có sẵn:</span> 
                <v-chip
                  :color="book.soLuong > 0 ? 'success' : 'error'"
                  variant="outlined"
                  class="ml-2"
                >
                  {{ book.soLuong > 0 ? book.soLuong : 'Hết sách' }}
                </v-chip>
              </p>
              
              <!-- Description -->
              <div class="mb-6" v-if="book.moTa">
                <h3 class="text-h6 font-weight-medium mb-2">Mô tả</h3>
                <div class="text-body-1 formatted-description" v-html="formatDescription(book.moTa)"></div>
              </div>
              
              <!-- Availability by Branch -->
              <div v-if="book.inventories && book.inventories.length > 0" class="mb-6">
                <h3 class="text-h6 font-weight-medium mb-2">Tình trạng sách theo chi nhánh</h3>
                <v-list density="compact" border rounded>
                  <v-list-item
                    v-for="inventory in book.inventories"
                    :key="`${inventory.bookId}-${inventory.branchId}`"
                    :title="inventory.tenChiNhanh"
                    :subtitle="inventory.soLuongHienCo > 0 ? `Còn ${inventory.soLuongHienCo} cuốn` : 'Hết sách'"
                  >
                    <template v-slot:prepend>
                      <v-icon 
                        :color="inventory.soLuongHienCo > 0 ? 'success' : 'error'"
                        class="mr-2"
                      >
                        {{ inventory.soLuongHienCo > 0 ? 'mdi-check-circle' : 'mdi-close-circle' }}
                      </v-icon>
                    </template>
                    
                    <template v-slot:append>
                      <v-btn
                        v-if="inventory.soLuongHienCo > 0 && isLoggedIn"
                        size="small"
                        color="primary"
                        variant="text"
                        @click="borrowBookFromBranch(inventory.branchId)"
                      >
                        Mượn tại đây
                      </v-btn>
                    </template>
                  </v-list-item>
                </v-list>
              </div>
              
              <!-- Actions -->
              <div class="d-flex flex-wrap gap-2">
                <v-btn
                  color="primary"
                  prepend-icon="mdi-book-open-page-variant"
                  :disabled="!isLoggedIn || book.soLuong <= 0"
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
              
              <!-- Out of stock notice -->
              <v-alert
                v-else-if="book.soLuong <= 0"
                type="warning"
                class="mt-6"
              >
                Sách này hiện đã hết. Vui lòng quay lại sau.
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
      console.warn('Failed to load book cover image');
      if (event && event.target) {
        event.target.src = 'https://via.placeholder.com/300x400/e0e0e0/666666?text=No+Image';
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
    },
    
    borrowBookFromBranch(branchId) {
      // To be implemented
      alert('Chức năng mượn sách tại chi nhánh sẽ được phát triển trong phiên bản tiếp theo!');
    },
    
    // Format text from database format to HTML display
    formatDescription(text) {
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
    }
  },
  created() {
    this.loadBook();
  }
};
</script>

<style scoped>
.book-details-page {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.formatted-description {
  line-height: 1.6;
}

.formatted-description :deep(strong) {
  font-weight: 700;
}

.formatted-description :deep(em) {
  font-style: italic;
}
</style>