<template>
  <div class="admin-layout d-flex">
    <Sidebar />
    <div class="main-content flex-grow-1 overflow-auto" style="height: 100vh;">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import Sidebar from '../components/admin/sidebar.vue'
</script>

<style scoped>
.admin-layout {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.main-content {
  background-color: #f8f9fa;
}
</style>
