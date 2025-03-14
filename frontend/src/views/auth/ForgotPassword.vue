<template>
  <div class="forgot-password-page py-12">
    <div class="max-w-md mx-auto bg-white dark:bg-secondary-900 rounded-lg shadow-md p-8">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-900 dark:text-white">Quên mật khẩu</h1>
      
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
        v-if="resetRequestSent"
        type="success"
        variant="tonal"
        class="mb-4"
      >
        Yêu cầu đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn.
      </v-alert>
      
      <p v-if="!resetRequestSent" class="text-gray-600 dark:text-gray-400 mb-6">
        Nhập địa chỉ email của bạn và chúng tôi sẽ gửi cho bạn một liên kết để đặt lại mật khẩu.
      </p>
      
      <v-form 
        v-if="!resetRequestSent" 
        @submit.prevent="submitForgotPassword" 
        ref="form" 
        v-model="isFormValid"
      >
        <v-text-field
          v-model="email"
          label="Email"
          :rules="[rules.required, rules.email]"
          variant="outlined"
          class="mb-6"
          prepend-inner-icon="mdi-email"
          type="email"
        ></v-text-field>
        
        <v-btn
          type="submit"
          color="primary"
          block
          size="large"
          :loading="loading"
          :disabled="!isFormValid || loading"
        >
          Gửi yêu cầu
        </v-btn>
      </v-form>
      
      <div class="text-center mt-6">
        <p class="text-gray-600 dark:text-gray-400">
          <router-link 
            to="/login" 
            class="text-primary-600 hover:text-primary-800 font-medium"
          >
            Quay lại đăng nhập
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'ForgotPasswordView',
  
  data() {
    return {
      email: '',
      isFormValid: false,
      rules: {
        required: value => !!value || 'Trường này là bắt buộc',
        email: value => {
          const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          return pattern.test(value) || 'Email không hợp lệ';
        }
      }
    };
  },
  
  computed: {
    ...mapGetters('auth', ['loading', 'error', 'resetRequestSent'])
  },
  
  methods: {
    ...mapActions('auth', ['requestPasswordReset', 'clearError']),
    
    async submitForgotPassword() {
      if (!this.$refs.form.validate()) return;
      
      await this.requestPasswordReset({ email: this.email });
    }
  },
  
  // Clear form when component is mounted
  mounted() {
    this.clearError();
    this.email = '';
  }
};
</script> 