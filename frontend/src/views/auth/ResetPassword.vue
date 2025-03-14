<template>
  <div class="reset-password-page py-12">
    <div class="max-w-md mx-auto bg-white dark:bg-secondary-900 rounded-lg shadow-md p-8">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-900 dark:text-white">Đặt lại mật khẩu</h1>
      
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
      
      <v-alert
        v-if="resetSuccess"
        type="success"
        variant="tonal"
        class="mb-4"
      >
        Mật khẩu đã được đặt lại thành công. Bạn có thể đăng nhập bằng mật khẩu mới.
      </v-alert>
      
      <v-form 
        v-if="!resetSuccess" 
        @submit.prevent="submitResetPassword" 
        ref="form" 
        v-model="isFormValid"
      >
        <v-text-field
          v-model="resetData.email"
          label="Email"
          :rules="[rules.required, rules.email]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-email"
          type="email"
          readonly
        ></v-text-field>
        
        <v-text-field
          v-model="resetData.token"
          label="Mã xác nhận"
          :rules="[rules.required]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-key"
          readonly
        ></v-text-field>
        
        <v-text-field
          v-model="resetData.newPassword"
          label="Mật khẩu mới"
          :rules="[rules.required, rules.password]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-lock"
          :type="showPassword ? 'text' : 'password'"
          :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showPassword = !showPassword"
        ></v-text-field>
        
        <v-text-field
          v-model="confirmPassword"
          label="Xác nhận mật khẩu mới"
          :rules="[rules.required, passwordMatch]"
          variant="outlined"
          class="mb-6"
          prepend-inner-icon="mdi-lock-check"
          :type="showConfirmPassword ? 'text' : 'password'"
          :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showConfirmPassword = !showConfirmPassword"
        ></v-text-field>
        
        <v-btn
          type="submit"
          color="primary"
          block
          size="large"
          :loading="loading"
          :disabled="!isFormValid || loading"
        >
          Đặt lại mật khẩu
        </v-btn>
      </v-form>
      
      <div class="text-center mt-6">
        <p class="text-gray-600 dark:text-gray-400">
          <router-link 
            to="/login" 
            class="text-primary-600 hover:text-primary-800 font-medium"
          >
            {{ resetSuccess ? 'Đăng nhập ngay' : 'Quay lại đăng nhập' }}
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'ResetPasswordView',
  
  data() {
    return {
      resetData: {
        email: '',
        token: '',
        newPassword: ''
      },
      confirmPassword: '',
      showPassword: false,
      showConfirmPassword: false,
      isFormValid: false,
      rules: {
        required: value => !!value || 'Trường này là bắt buộc',
        email: value => {
          const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          return pattern.test(value) || 'Email không hợp lệ';
        },
        password: value => {
          const hasMinLength = value.length >= 6;
          return hasMinLength || 'Mật khẩu phải có ít nhất 6 ký tự';
        }
      }
    };
  },
  
  computed: {
    ...mapGetters('auth', ['loading', 'error', 'resetSuccess']),
    
    passwordMatch() {
      return this.resetData.newPassword === this.confirmPassword || 'Mật khẩu không khớp';
    }
  },
  
  methods: {
    ...mapActions('auth', ['resetPassword', 'clearError']),
    
    async submitResetPassword() {
      if (!this.$refs.form.validate()) return;
      
      await this.resetPassword(this.resetData);
    }
  },
  
  // Get token and email from URL query params
  created() {
    this.clearError();
    
    const token = this.$route.query.token;
    const email = this.$route.query.email;
    
    if (!token || !email) {
      this.error = 'Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.';
      return;
    }
    
    this.resetData.token = token;
    this.resetData.email = email;
  }
};
</script> 