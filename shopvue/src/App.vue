<template>
  <div>
    <!-- <login-view></login-view> -->
    <!-- <register-view></register-view> -->
    <router-view></router-view>
    <!-- <display-good-view></display-good-view> -->
  </div>
</template>

<script>
import DisplayGoodView from './views/Elements/DisplayGoodView.vue';
import LoginView from './views/Elements/LoginView.vue';
import RegisterView from './views/Elements/RegisterView.vue';
export default {
  // components: { DisplayGoodView },
  // components: { LoginView,RegisterView },
  data() {
    return {
      message: "Hello Vue",
    };
  },
};
</script>

<style>
</style>
