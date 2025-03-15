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
        :src="book.hinhAnhSach || '/placeholder-book.png'"
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
      <div v-if="book.authors && book.authors.length > 0" class="text-truncate mb-1">
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
  name: 'BookCard',
  props: {
    book: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      hovering: false,
      isFavorite: false
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
    toggleFavorite() {
      this.isFavorite = !this.isFavorite;
      // TODO: Implement favorite functionality in later increment
    },
    
    // Handle image loading errors
    handleImageError(event) {
      console.warn(`Failed to load image for book: ${this.book.tuaSach}`);
      event.target.src = '/placeholder-book.png';
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