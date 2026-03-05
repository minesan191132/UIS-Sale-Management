<script setup>
</script>

<template>
    <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade-slide" mode="out-in">
        <component :is="Component" :key="$route.path" />
      </transition>
    </router-view>
    </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
}

/* Transition Effects */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
