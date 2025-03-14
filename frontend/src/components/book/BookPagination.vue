<template>
  <div class="d-flex justify-center my-4">
    <v-pagination
      v-model="currentPage"
      :length="pageCount"
      :total-visible="7"
      @update:model-value="changePage"
      rounded
    ></v-pagination>
    
    <div class="d-flex align-center ml-4">
      <v-select
        v-model="pageSize"
        :items="pageSizeOptions"
        label="Hiển thị"
        density="compact"
        style="width: 115px"
        variant="outlined"
        hide-details
        @update:model-value="changePageSize"
      ></v-select>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BookPagination',
  props: {
    totalItems: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    },
    currentPageNo: {
      type: Number,
      required: true
    },
    currentPageSize: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      currentPage: this.currentPageNo + 1, // Convert from 0-based to 1-based for display
      pageSize: this.currentPageSize,
      pageSizeOptions: [5, 10, 20, 50]
    };
  },
  computed: {
    pageCount() {
      return this.totalPages;
    }
  },
  watch: {
    currentPageNo(newVal) {
      this.currentPage = newVal + 1;
    },
    currentPageSize(newVal) {
      this.pageSize = newVal;
    }
  },
  methods: {
    changePage(page) {
      this.$emit('page-changed', page - 1); // Convert from 1-based to 0-based for API
    },
    changePageSize(size) {
      this.$emit('size-changed', size);
    }
  }
};
</script> 