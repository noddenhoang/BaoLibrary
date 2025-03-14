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
                :src="book.hinhAnhSach || 'https://placehold.co/300x450/e2e8f0/1e293b?text=No+Image'"
                :alt="book.tuaSach"
                height="350"
                class="mx-auto rounded book-cover"
                cover
              ></v-img>
              
              <div class="d-flex justify-center mt-4">
                <v-btn 
                  color="primary" 
                  prepend-icon="mdi-book-open-page-variant" 
                  class="mr-2"
                  :to="{ name: 'BorrowedBooks' }"
                >
                  Mượn sách
                </v-btn>
                <v-btn 
                  color="secondary" 
                  icon 
                  @click="toggleFavorite" 
                  :variant="isFavorite ? 'flat' : 'outlined'"
                >
                  <v-icon :color="isFavorite ? 'error' : undefined">
                    {{ isFavorite ? 'mdi-heart' : 'mdi-heart-outline' }}
                  </v-icon>
                </v-btn>
              </div>
            </v-col>
            
            <!-- Book Info -->
            <v-col cols="12" sm="8" md="9" class="pa-4">
              <h1 class="text-h4 mb-2">{{ book.tuaSach }}</h1>
              
              <div class="mb-4">
                <div v-if="book.authors && book.authors.length > 0" class="mb-2">
                  <span class="text-subtitle-1 font-weight-medium">Tác giả:</span>
                  <span class="ml-2">
                    <template v-for="(author, index) in book.authors" :key="author.authorId">
                      <span>{{ author.authorName }}</span>
                      <span v-if="index < book.authors.length - 1">, </span>
                    </template>
                  </span>
                </div>
                
                <div v-if="book.namXuatBan" class="mb-2">
                  <span class="text-subtitle-1 font-weight-medium">Năm xuất bản:</span>
                  <span class="ml-2">{{ book.namXuatBan }}</span>
                </div>
                
                <div v-if="book.categories && book.categories.length > 0" class="mb-2">
                  <span class="text-subtitle-1 font-weight-medium">Danh mục:</span>
                  <div class="d-flex flex-wrap mt-1">
                    <v-chip
                      v-for="category in book.categories"
                      :key="category.categoryId"
                      color="secondary"
                      variant="flat"
                      class="mr-2 mb-2"
                    >
                      {{ category.categoryName }}
                    </v-chip>
                  </div>
                </div>
              </div>
              
              <v-divider class="mb-4"></v-divider>
              
              <!-- Book Description -->
              <div v-if="book.moTa" class="mb-6">
                <h2 class="text-h6 mb-2">Mô tả</h2>
                <div class="text-body-1">
                  {{ book.moTa }}
                </div>
              </div>
              
              <!-- Book Status -->
              <div class="mb-4">
                <h2 class="text-h6 mb-2">Trạng thái</h2>
                <v-chip color="success" variant="flat" class="mr-2">
                  <v-icon start>mdi-check-circle</v-icon>
                  Còn sách
                </v-chip>
              </div>
            </v-col>
          </v-row>
        </v-card>
        
        <!-- Related Books Section (Mock) -->
        <div class="mt-8">
          <h2 class="text-h5 mb-4">Sách liên quan</h2>
          <p class="text-body-1 text-grey">
            Tính năng này sẽ được triển khai trong phiên bản tiếp theo.
          </p>
        </div>
      </template>
      
      <!-- Book Not Found -->
      <div v-else class="text-center py-10">
        <v-icon size="x-large" color="grey">mdi-book-off</v-icon>
        <p class="text-h6 text-grey mt-4">Không tìm thấy thông tin sách</p>
        <v-btn
          class="mt-4"
          :to="{ name: 'Catalog' }"
          variant="outlined"
          color="primary"
        >
          Quay lại danh sách sách
        </v-btn>
      </div>
    </v-container>
  </div>
</template>

<script>
import apiService from '@/services/api.service';

export default {
  name: 'BookDetailsPage',
  data() {
    return {
      book: null,
      loading: false,
      error: null,
      isFavorite: false
    };
  },
  computed: {
    bookId() {
      return this.$route.params.id;
    }
  },
  methods: {
    async fetchBookDetails() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await apiService.books.getById(this.bookId);
        this.book = response.data;
      } catch (error) {
        console.error('Error fetching book details:', error);
        this.error = 'Không thể tải thông tin sách. Vui lòng thử lại sau.';
      } finally {
        this.loading = false;
      }
    },
    toggleFavorite() {
      this.isFavorite = !this.isFavorite;
      // TODO: Implement favorite functionality in later increment
    }
  },
  mounted() {
    this.fetchBookDetails();
  },
  watch: {
    // Refetch data if book ID changes (unlikely but good practice)
    bookId() {
      this.fetchBookDetails();
    }
  }
};
</script>

<style scoped>
.book-cover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.book-cover:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}
</style> 