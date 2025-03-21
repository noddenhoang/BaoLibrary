<template>
  <div>
    <v-container fluid>
      <template v-if="books.length > 0">
        <v-list>
          <v-list-item
            v-for="book in books"
            :key="book.bookId"
            :to="{ name: 'BookDetails', params: { id: book.bookId } }"
            class="mb-2 rounded book-list-item"
          >
            <template v-slot:prepend>
              <v-avatar size="80" rounded>
                <v-img
                  :src="book.hinhAnhSach || '/placeholder-book.png'"
                  :alt="book.tuaSach"
                  cover
                  @error="handleImageError(book)"
                ></v-img>
              </v-avatar>
            </template>
            
            <v-list-item-title class="text-h6 mb-1">
              {{ book.tuaSach }}
            </v-list-item-title>
            
            <v-list-item-subtitle>
              <div class="d-flex align-center mb-1">
                <span class="text-body-2">
                  {{ book.authors && book.authors.length > 0 ? getAuthorsList(book.authors) : 'Không rõ tác giả' }}
                </span>
                <v-divider class="mx-2" vertical></v-divider>
                <span class="text-caption">{{ book.namXuatBan || 'Không rõ năm xuất bản' }}</span>
              </div>
              
              <div class="d-flex flex-wrap mb-2" v-if="book.categories && book.categories.length > 0">
                <v-chip
                  v-for="category in book.categories.slice(0, 3)"
                  :key="category.categoryId"
                  size="x-small"
                  class="mr-1 mb-1"
                  color="secondary"
                  variant="flat"
                >
                  {{ category.categoryName }}
                </v-chip>
                <v-chip v-if="book.categories.length > 3" size="x-small" variant="flat">
                  +{{ book.categories.length - 3 }}
                </v-chip>
              </div>
              
              <p class="text-body-2 text-truncate-2-lines text-grey" v-if="book.moTa">
                {{ book.moTa }}
              </p>
            </v-list-item-subtitle>
            
            <template v-slot:append>
              <div class="d-flex">
                <v-btn 
                  icon 
                  variant="text" 
                  @click.stop.prevent="toggleFavorite(book)" 
                  :color="book.isFavorite ? 'error' : undefined"
                >
                  <v-icon>{{ book.isFavorite ? 'mdi-heart' : 'mdi-heart-outline' }}</v-icon>
                </v-btn>
              </div>
            </template>
          </v-list-item>
        </v-list>
      </template>
      
      <div v-else class="text-center py-10">
        <v-icon size="x-large" color="grey">mdi-book-off</v-icon>
        <p class="text-h6 text-grey mt-4">Không tìm thấy sách nào</p>
      </div>
    </v-container>
  </div>
</template>

<script>
export default {
  name: 'BookList',
  props: {
    books: {
      type: Array,
      required: true
    }
  },
  methods: {
    getAuthorsList(authors) {
      if (authors.length === 0) return 'Không rõ tác giả';
      if (authors.length === 1) return authors[0].tenTacGia;
      if (authors.length === 2) {
        return `${authors[0].tenTacGia} & ${authors[1].tenTacGia}`;
      }
      return `${authors[0].tenTacGia} & ${authors.length - 1} tác giả khác`;
    },
    toggleFavorite(book) {
      book.isFavorite = !book.isFavorite;
      // TODO: Implement favorite functionality in later increment
    },
    // Handle image loading errors
    handleImageError(book) {
      console.warn(`Failed to load image for book: ${book.tuaSach}`);
      event.target.src = '/placeholder-book.jpg';
    }
  }
};
</script>

<style scoped>
.book-list-item {
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.book-list-item:hover {
  background-color: rgba(var(--v-theme-secondary), 0.05);
  transform: translateY(-2px);
}

.text-truncate-2-lines {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>