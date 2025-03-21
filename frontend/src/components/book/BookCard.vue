<template>
  <v-card
    :to="book.soLuong > 0 ? { name: 'BookDetails', params: { id: book.bookId } } : ''"
    class="book-card h-full transition-all duration-300"
    :class="{ 'sold-out': book.soLuong <= 0 }"
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
        <div 
          v-if="book.soLuong <= 0" 
          class="sold-out-overlay d-flex align-center justify-center"
        >
          <div class="text-h5 font-weight-bold white--text">HẾT SÁCH</div>
        </div>
      </v-img>
    </div>
    
    <v-card-title class="text-truncate">{{ book.tuaSach }}</v-card-title>
    
    <v-card-subtitle>
      <div class="d-flex align-center">
        <span class="text-truncate">{{ authorsList }}</span>
        <v-spacer></v-spacer>
        <v-chip 
          size="small" 
          :color="book.soLuong > 0 ? 'success' : 'error'" 
          class="ml-2"
        >
          {{ book.soLuong || 0 }}
        </v-chip>
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
      
      <p class="text-caption text-truncate" v-if="book.moTa">
        {{ book.moTa }}
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
        return '/placeholder-book.jpg';
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
        event.target.src = '/placeholder-book.jpg';
      }
    }
  }
};
</script>

<style scoped>
.book-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.book-card:hover:not(.sold-out) {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1) !important;
}

.sold-out {
  opacity: 0.7;
  cursor: not-allowed;
}

.sold-out-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 1;
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