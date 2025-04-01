<template>
  <v-card
    :to="{ name: 'BookDetails', params: { id: book.bookId } }"
    class="book-card h-full transition-all duration-300"
    :elevation="hovering ? 8 : 2"
    @mouseenter="hovering = true"
    @mouseleave="hovering = false"
  >
    <div class="book-card-image-container">
      <v-img
        :src="getImageUrl(book.hinhAnhSach)"
        :alt="book.tuaSach"
        class="book-card-image"
        height="200"
        cover
        @error="handleImageError"
      >
        <template v-slot:placeholder>
          <div class="d-flex align-center justify-center fill-height">
            <v-progress-circular indeterminate color="primary"></v-progress-circular>
          </div>
        </template>
      </v-img>
    </div>
    
    <v-card-title class="text-truncate">{{ book.tuaSach }}</v-card-title>
    
    <v-card-subtitle>
      <div class="text-truncate mb-1" v-if="book.authors && book.authors.length > 0">
        <span>{{ authorsList }}</span>
      </div>
      <div v-if="book.namXuatBan" class="text-caption">
        {{ book.namXuatBan }}
      </div>
    </v-card-subtitle>
    
    <v-card-text>
      <div v-if="book.categories && book.categories.length > 0" class="mb-2">
        <v-chip
          v-for="category in book.categories.slice(0, 2)"
          :key="category.categoryId"
          size="small"
          class="mr-1 mb-1"
          color="secondary"
          variant="flat"
        >
          {{ category.categoryName }}
        </v-chip>
        <v-chip v-if="book.categories.length > 2" size="small" variant="flat">
          +{{ book.categories.length - 2 }}
        </v-chip>
      </div>
      
      <div class="d-flex align-center mb-2">
        <v-icon 
          size="small" 
          :color="book.soLuong > 0 ? 'success' : 'error'" 
          class="mr-1"
        >
          {{ book.soLuong > 0 ? 'mdi-check-circle' : 'mdi-alert-circle' }}
        </v-icon>
        <span 
          class="text-caption" 
          :class="{ 'text-success': book.soLuong > 0, 'text-error': book.soLuong <= 0 }"
        >
          {{ book.soLuong > 0 ? `Còn ${book.soLuong} cuốn` : 'Hết sách' }}
        </span>
      </div>
      
      <p class="text-caption text-truncate" v-if="book.moTa">
        {{ cleanDescription(book.moTa) }}
      </p>
    </v-card-text>
    
    <v-card-actions>
      <v-btn
        variant="text"
        color="primary"
        :to="{ name: 'BookDetails', params: { id: book.bookId } }"
        class="text-capitalize"
      >
        Chi tiết
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn 
        icon 
        size="small" 
        @click.stop.prevent="toggleFavorite" 
        :color="isFavorite ? 'error' : undefined"
      >
        <v-icon>{{ isFavorite ? 'mdi-heart' : 'mdi-heart-outline' }}</v-icon>
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  props: {
    book: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      hovering: false,
      isFavorite: false,
      imageError: false
    };
  },
  computed: {
    authorsList() {
      if (!this.book.authors || this.book.authors.length === 0) return '';
      if (this.book.authors.length === 1) return this.book.authors[0].tenTacGia;
      if (this.book.authors.length === 2) {
        return `${this.book.authors[0].tenTacGia} & ${this.book.authors[1].tenTacGia}`;
      }
      return `${this.book.authors[0].tenTacGia} & ${this.book.authors.length - 1} tác giả khác`;
    }
  },
  methods: {
    getImageUrl(url) {
      if (!url || this.imageError) {
        console.log('Sử dụng ảnh placeholder do không có URL hoặc có lỗi');
        return 'https://via.placeholder.com/300x400/e0e0e0/666666?text=No+Image';
      }
      
      // Nếu đã là URL https (Cloudinary), sử dụng trực tiếp
      if (url.match(/^https?:\/\//)) {
        return url;
      }
      
      // Trường hợp: Google Drive
      if (url.includes('drive.google.com')) {
        // Extract the file ID if possible
        const idMatch = url.match(/[-\w]{25,}/);
        if (idMatch) {
          const fileId = idMatch[0];
          return `https://drive.google.com/uc?export=view&id=${fileId}`;
        }
      }
      
      // Fallback - trả về URL gốc
      return url;
    },

    toggleFavorite() {
      this.isFavorite = !this.isFavorite;
      // TODO: Implement favorite functionality in later increment
    },
    
    handleImageError(event) {
      this.imageError = true;
      console.warn(`Failed to load image for book: ${this.book.tuaSach}`);
      
      if (event && event.target) {
        event.target.src = "https://via.placeholder.com/300x400/e0e0e0/666666?text=No+Image";
      }
    },

    cleanDescription(description) {
      if (!description) return '';
      
      // Loại bỏ các ký tự định dạng và giữ lại văn bản thuần túy
      return description
        .replace(/\*\*/g, '') // Loại bỏ dấu in đậm
        .replace(/\*/g, '')   // Loại bỏ dấu in nghiêng
        .replace(/\\_/g, '')  // Loại bỏ dấu gạch dưới
        .replace(/\\n/g, ' '); // Thay thế xuống dòng bằng khoảng trắng
    }
  }
};
</script>

<style scoped>
.book-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1) !important;
}

.book-card-image-container {
  position: relative;
  overflow: hidden;
}

.book-card-image {
  transition: transform 0.5s ease;
}

.book-card:hover .book-card-image {
  transform: scale(1.05);
}
</style>