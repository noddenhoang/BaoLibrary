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
              
              <!-- Description - Thêm phần hiển thị mô tả sách -->
              <div v-if="book.moTa" class="my-4">
                <h3 class="text-subtitle-1 font-weight-medium mb-2">Mô tả sách</h3>
                <div class="formatted-description pa-3 bg-grey-lighten-4 rounded" v-html="formatDescription(book.moTa)"></div>
              </div>
              
              <!-- Availability -->
              <div class="my-4 pa-4 rounded-lg" :class="availabilityClass">
                <div class="d-flex align-center mb-2">
                  <v-icon :color="isAvailable ? 'success' : 'error'" class="mr-2">
                    {{ isAvailable ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                  </v-icon>
                  <span class="text-subtitle-1 font-weight-bold">
                    {{ isAvailable ? 'Còn sách' : 'Hết sách' }}
                  </span>
                </div>
                <p v-if="isAvailable" class="mb-0">
                  Sách này hiện có sẵn tại thư viện. Bạn có thể mượn ngay.
                </p>
                <p v-else class="mb-0">
                  Xin lỗi, sách này hiện không có sẵn. Vui lòng thử lại sau.
                </p>
              </div>
              
              <!-- Borrowing Form -->
              <div v-if="isLoggedIn && isAvailable" class="mt-4">
                <h3 class="text-h6 font-weight-medium mb-3">Mượn sách</h3>
                
                <v-form ref="borrowForm" v-model="borrowForm.valid">
                  <v-row>
                    <v-col cols="12" md="6">
                      <v-select
                        v-model="borrowForm.branchId"
                        :items="branches"
                        item-title="branchName"
                        item-value="branchId"
                        label="Chi nhánh"
                        :rules="[v => !!v || 'Vui lòng chọn chi nhánh']"
                        variant="outlined"
                        hide-details="auto"
                        class="mb-4"
                      ></v-select>
                    </v-col>
                    
                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="borrowForm.rentalDays"
                        type="number"
                        min="1"
                        max="30"
                        label="Số ngày mượn"
                        hint="Phí thuê: 10,000 VND/ngày"
                        persistent-hint
                        :rules="[
                          v => !!v || 'Vui lòng nhập số ngày mượn',
                          v => v > 0 || 'Số ngày phải lớn hơn 0',
                          v => v <= 30 || 'Thời gian mượn tối đa là 30 ngày'
                        ]"
                        variant="outlined"
                        hide-details="auto"
                        @update:model-value="calculateRentalFee"
                      ></v-text-field>
                    </v-col>
                    
                    <v-col cols="12">
                      <v-alert type="info" variant="tonal" class="mb-4">
                        <div class="d-flex justify-space-between align-center">
                          <div>
                            <strong>Phí thuê:</strong> {{ formattedRentalFee }}
                          </div>
                          <div>
                            <strong>Ngày dự kiến trả:</strong> {{ formattedExpectedReturnDate }}
                          </div>
                        </div>
                      </v-alert>
                    </v-col>
                  </v-row>
                  
                  <v-btn
                    color="primary"
                    size="large"
                    block
                    @click="borrowBook"
                    :loading="borrowing"
                    :disabled="!borrowForm.valid || borrowing"
                  >
                    Mượn sách
                  </v-btn>
                </v-form>
              </div>
              
              <!-- Login prompt if not logged in -->
              <v-alert v-else-if="!isLoggedIn" type="info" variant="tonal" class="mt-4">
                Vui lòng <router-link to="/login">đăng nhập</router-link> để mượn sách.
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
import apiService from '@/services/api.service';

export default {
  name: 'BookDetailsView',
  data() {
    return {
      loading: true,
      error: null,
      book: null,
      borrowForm: {
        valid: true,
        branchId: null,
        rentalDays: 1
      },
      borrowing: false,
      rentalFee: 0
    };
  },
  computed: {
    ...mapGetters('auth', ['isAuthenticated']),
    
    isLoggedIn() {
      return this.isAuthenticated;
    },
    
    bookId() {
      return parseInt(this.$route.params.id);
    },
    
    isAvailable() {
      return this.book && this.book.soLuong > 0;
    },
    
    availabilityClass() {
      return {
        'bg-grey-lighten-3': !this.isAvailable,
        'bg-grey-lighten-2': this.isAvailable
      };
    },
    
    branches() {
      return this.book && this.book.inventories ? this.book.inventories.map(i => ({
        branchId: i.branchId,
        branchName: i.tenChiNhanh
      })) : [];
    },
    
    formattedRentalFee() {
      if (!this.rentalFee) return '0 VND';
      return `${this.rentalFee.toLocaleString()} VND`;
    },
    
    formattedExpectedReturnDate() {
      if (!this.borrowForm.rentalDays) return 'N/A';
      const today = new Date();
      const returnDate = new Date(today);
      returnDate.setDate(today.getDate() + this.borrowForm.rentalDays);
      return returnDate.toLocaleDateString();
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
      
      if (!this.$refs.borrowForm.validate()) {
        return;
      }
      
      this.borrowing = true;
      
      const borrowingData = {
        userId: this.$store.getters['auth/user'].userId,
        branchId: this.borrowForm.branchId,
        borrowingDetails: [
          {
            bookId: this.bookId,
            rentalDays: parseInt(this.borrowForm.rentalDays)
          }
        ]
      };
      
      // Gọi API để mượn sách
      this.$store.dispatch('borrowings/createBorrowing', borrowingData)
        .then(response => {
          // Hiển thị thông báo thành công
          this.$store.dispatch('setNotification', {
            type: 'success',
            message: `Mượn sách thành công! Phí thuê: ${response.totalRentalFee.toLocaleString()} VND`
          }, { root: true });
          
          // Chuyển đến trang quản lý mượn sách
          this.$router.push('/user/borrowings');
        })
        .catch(error => {
          // Hiển thị thông báo lỗi
          let errorMessage = 'Không thể mượn sách. Vui lòng thử lại sau.';
          
          if (error.response && error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message;
          }
          
          this.$store.dispatch('setNotification', {
            type: 'error',
            message: errorMessage
          }, { root: true });
        })
        .finally(() => {
          this.borrowing = false;
        });
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
    },
    
    calculateRentalFee() {
      if (!this.borrowForm.rentalDays || this.borrowForm.rentalDays <= 0) {
        this.rentalFee = 0;
        return;
      }
      
      // Gọi API để tính phí thuê thay vì hardcode
      apiService.borrowings.calculateRentalFee(this.borrowForm.rentalDays)
        .then(response => {
          this.rentalFee = response.data;
        })
        .catch(error => {
          console.error('Error calculating rental fee:', error);
          // Fallback nếu API lỗi (10,000 VND/ngày)
          this.rentalFee = 10000 * this.borrowForm.rentalDays;
        });
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