<template>
  <div class="d-flex justify-end mb-4">
    <v-btn-toggle
      v-model="selectedView"
      color="primary"
      mandatory
      @update:model-value="toggleView"
    >
      <v-btn value="grid" icon="mdi-view-grid" title="Grid View"></v-btn>
      <v-btn value="list" icon="mdi-view-list" title="List View"></v-btn>
    </v-btn-toggle>
  </div>
</template>

<script>
export default {
  name: 'ViewToggle',
  props: {
    view: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      selectedView: this.view
    };
  },
  methods: {
    toggleView(view) {
      this.$emit('view-changed', view);
    }
  },
  watch: {
    view(newVal) {
      this.selectedView = newVal;
    }
  }
};
</script> 