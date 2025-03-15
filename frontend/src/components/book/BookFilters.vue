<template>
  <div class="book-filters">
    <v-expansion-panels>
      <v-expansion-panel>
        <v-expansion-panel-title>
          <v-icon start>mdi-filter</v-icon>
          Bộ lọc
          <template v-slot:actions="{ expanded }">
            <v-btn
              :icon="expanded ? 'mdi-chevron-up' : 'mdi-chevron-down'"
              variant="text"
              color="grey-darken-1"
              size="small"
            ></v-btn>
          </template>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <!-- Search Keyword -->
            <v-col cols="12">
              <v-text-field
                v-model="filters.keyword"
                label="Tìm kiếm sách"
                prepend-inner-icon="mdi-magnify"
                hide-details
                density="compact"
                variant="outlined"
                @update:model-value="debouncedSearch"
                clearable
              ></v-text-field>
            </v-col>
            
            <!-- Categories Filter -->
            <v-col cols="12" sm="6" md="4">
              <v-select
                v-model="filters.categoryId"
                :items="categories"
                item-title="categoryName"
                item-value="categoryId"
                label="Danh mục"
                hide-details
                density="compact"
                variant="outlined"
                @update:model-value="applyFilters"
                clearable
                prepend-inner-icon="mdi-shape"
              ></v-select>
            </v-col>
            
            <!-- Authors Filter -->
            <v-col cols="12" sm="6" md="4">
              <v-select
                v-model="filters.authorId"
                :items="authors"
                item-title="tenTacGia"
                item-value="authorId"
                label="Tác giả"
                hide-details
                density="compact"
                variant="outlined"
                @update:model-value="applyFilters"
                clearable
                prepend-inner-icon="mdi-account"
              ></v-select>
            </v-col>
            
            <!-- Publication Year Filter -->
            <v-col cols="12" sm="6" md="4">
              <v-select
                v-model="filters.year"
                :items="yearOptions"
                label="Năm xuất bản"
                hide-details
                density="compact"
                variant="outlined"
                @update:model-value="applyFilters"
                clearable
                prepend-inner-icon="mdi-calendar"
              ></v-select>
            </v-col>
            
            <!-- Sort Options -->
            <v-col cols="12" sm="6" md="4">
              <v-select
                v-model="filters.sortBy"
                :items="sortOptions"
                item-title="text"
                item-value="value"
                label="Sắp xếp theo"
                hide-details
                density="compact"
                variant="outlined"
                @update:model-value="applyFilters"
                prepend-inner-icon="mdi-sort"
              ></v-select>
            </v-col>
            
            <!-- Sort Direction -->
            <v-col cols="12" sm="6" md="4">
              <v-btn-toggle
                v-model="filters.sortDir"
                color="primary"
                density="compact"
                mandatory
                @update:model-value="applyFilters"
              >
                <v-btn value="asc" prepend-icon="mdi-sort-ascending">
                  Tăng dần
                </v-btn>
                <v-btn value="desc" prepend-icon="mdi-sort-descending">
                  Giảm dần
                </v-btn>
              </v-btn-toggle>
            </v-col>
            
            <!-- Actions -->
            <v-col cols="12" sm="6" md="4" class="d-flex justify-end align-center">
              <v-btn
                color="error"
                variant="text"
                @click="resetFilters"
                class="mr-2"
                prepend-icon="mdi-filter-remove"
              >
                Xóa bộ lọc
              </v-btn>
              <v-btn
                color="primary"
                @click="applyFilters"
                prepend-icon="mdi-filter-check"
              >
                Áp dụng
              </v-btn>
            </v-col>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>
</template>

<script>
export default {
  name: 'BookFilters',
  props: {
    categories: {
      type: Array,
      default: () => []
    },
    authors: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      filters: {
        keyword: '',
        categoryId: null,
        authorId: null,
        year: null,
        sortBy: 'tuaSach',
        sortDir: 'asc'
      },
      sortOptions: [
        { text: 'Tên sách', value: 'tuaSach' },
        { text: 'Năm xuất bản', value: 'namXuatBan' }
      ],
      yearOptions: this.generateYearOptions(),
      debounceTimeout: null
    };
  },
  methods: {
    generateYearOptions() {
      const currentYear = new Date().getFullYear();
      const years = [];
      
      for (let i = currentYear; i >= 1900; i -= 10) {
        years.push({ title: `${i - 9} - ${i}`, value: i - 5 });
      }
      
      return years;
    },
    applyFilters() {
      this.$emit('filter-changed', { ...this.filters });
    },
    resetFilters() {
      this.filters = {
        keyword: '',
        categoryId: null,
        authorId: null,
        year: null,
        sortBy: 'tuaSach',
        sortDir: 'asc'
      };
      this.applyFilters();
    },
    debouncedSearch() {
      clearTimeout(this.debounceTimeout);
      this.debounceTimeout = setTimeout(() => {
        this.applyFilters();
      }, 500);
    }
  },
  mounted() {
    // Apply initial filters
    this.applyFilters();
  }
};
</script>

<style scoped>
.book-filters {
  margin-bottom: 1rem;
}
</style>