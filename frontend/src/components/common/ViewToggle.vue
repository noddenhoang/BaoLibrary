<template>
  <div class="view-toggle">
    <v-btn-toggle
      v-model="selectedView"
      color="primary"
      density="comfortable"
      mandatory
    >
      <v-btn value="grid" prepend-icon="mdi-view-grid">
        Grid
      </v-btn>
      <v-btn value="list" prepend-icon="mdi-view-list">
        List
      </v-btn>
    </v-btn-toggle>
  </div>
</template>

<script>
export default {
  name: 'ViewToggle',
  props: {
    view: {
      type: String,
      default: 'grid',
      validator: (value) => ['grid', 'list'].includes(value)
    }
  },
  data() {
    return {
      selectedView: this.view
    };
  },
  watch: {
    view(newView) {
      this.selectedView = newView;
    },
    selectedView(newValue) {
      this.$emit('view-changed', newValue);
    }
  }
};
</script>

<style scoped>
.view-toggle {
  display: flex;
  justify-content: flex-end;
}
</style>
