<template>
  <div>
    <v-container fluid>
      <v-row>
        <v-col
          v-for="book in books"
          :key="book.bookId"
          cols="12"
          sm="6"
          md="4"
          lg="3"
          xl="2"
          class="book-grid-item"
        >
          <book-card :book="book" />
        </v-col>
      </v-row>
      
      <div v-if="books.length === 0" class="text-center py-10">
        <v-icon size="x-large" color="grey">mdi-book-off</v-icon>
        <p class="text-h6 text-grey mt-4">Không tìm thấy sách nào</p>
      </div>
    </v-container>
  </div>
</template>

<script>
import BookCard from '@/components/book/BookCard.vue';

export default {
  name: 'BookGrid',
  components: {
    BookCard
  },
  props: {
    books: {
      type: Array,
      required: true
    }
  }
};
</script>

<style scoped>
.book-grid-item {
  margin-bottom: 1.5rem;
}
</style> 