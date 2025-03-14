<template>
  <div class="login-page py-12">
    <div class="max-w-md mx-auto bg-white dark:bg-secondary-900 rounded-lg shadow-md p-8">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-900 dark:text-white">Đăng nhập</h1>
      
      <v-alert
        v-if="error"
        type="error"
        variant="tonal"
        class="mb-4"
        closable
        @click:close="clearError"
      >
        {{ error }}
      </v-alert>
      
      <v-form @submit.prevent="submitLogin" ref="form" v-model="isFormValid">
        <v-text-field
          v-model="credentials.taiKhoan"
          label="Tài khoản"
          :rules="[rules.required]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-account"
        ></v-text-field>
        
        <v-text-field
          v-model="credentials.matKhau"
          label="Mật khẩu"
          :rules="[rules.required]"
          variant="outlined"
          class="mb-6"
          prepend-inner-icon="mdi-lock"
          :type="showPassword ? 'text' : 'password'"
          :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showPassword = !showPassword"
        ></v-text-field>
        
        <div class="flex justify-between items-center mb-6">
          <v-checkbox
            v-model="rememberMe"
            label="Ghi nhớ đăng nhập"
            hide-details
          ></v-checkbox>
          
          <router-link 
            to="/forgot-password" 
            class="text-primary-600 hover:text-primary-800 text-sm"
          >
            Quên mật khẩu?
          </router-link>
        </div>
        
        <v-btn
          type="submit"
          color="primary"
          block
          size="large"
          :loading="loading"
          :disabled="!isFormValid || loading"
        >
          Đăng nhập
        </v-btn>
        
        <div class="text-center mt-6">
          <p class="text-gray-600 dark:text-gray-400">
            Chưa có tài khoản? 
            <router-link 
              to="/register" 
              class="text-primary-600 hover:text-primary-800 font-medium"
            >
              Đăng ký ngay
            </router-link>
          </p>
        </div>
      </v-form>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'LoginView',
  
  data() {
    return {
      credentials: {
        taiKhoan: '',
        matKhau: ''
      },
      rememberMe: false,
      showPassword: false,
      isFormValid: false,
      rules: {
        required: value => !!value || 'Trường này là bắt buộc'
      }
    };
  },
  
  computed: {
    ...mapGetters('auth', ['loading', 'error'])
  },
  
  methods: {
    ...mapActions('auth', ['login', 'clearError']),
    
    async submitLogin() {
      if (!this.$refs.form.validate()) return;
      
      const success = await this.login(this.credentials);
      
      if (success) {
        // Check if there's a redirect URL in the query params
        const redirectPath = this.$route.query.redirect || '/';
        this.$router.push(redirectPath);
      }
    }
  },
  
  // Clear form when component is mounted
  mounted() {
    this.clearError();
    this.credentials = {
      taiKhoan: '',
      matKhau: ''
    };
  }
};
</script> 