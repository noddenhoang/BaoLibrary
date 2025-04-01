<template>
  <div class="register-page py-12">
    <div class="max-w-md mx-auto bg-white dark:bg-secondary-900 rounded-lg shadow-md p-8">
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-900 dark:text-white">Đăng ký tài khoản</h1>
      
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
      
      <v-form @submit.prevent="submitRegister" ref="form" v-model="isFormValid">
        <!-- Họ tên -->
        <v-text-field
          v-model="userData.hoTen"
          label="Họ và tên"
          :rules="[rules.required]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-account"
        ></v-text-field>
        
        <!-- Tài khoản -->
        <v-text-field
          v-model="userData.taiKhoan"
          label="Tài khoản"
          :rules="[rules.required, rules.minLength]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-account-key"
        ></v-text-field>
        
        <!-- Email -->
        <v-text-field
          v-model="userData.email"
          label="Email"
          :rules="[rules.required, rules.email]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-email"
          type="email"
        ></v-text-field>
        
        <!-- Số điện thoại -->
        <v-text-field
          v-model="userData.soDienThoai"
          label="Số điện thoại"
          :rules="[rules.required, rules.phone]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-phone"
        ></v-text-field>
        
        <!-- Địa chỉ -->
        <v-textarea
          v-model="userData.diaChi"
          label="Địa chỉ"
          :rules="[rules.required]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-map-marker"
          rows="2"
          auto-grow
        ></v-textarea>
        
        <!-- Mật khẩu -->
        <v-text-field
          v-model="userData.matKhau"
          label="Mật khẩu"
          :rules="[rules.required, rules.password]"
          variant="outlined"
          class="mb-4"
          prepend-inner-icon="mdi-lock"
          :type="showPassword ? 'text' : 'password'"
          :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showPassword = !showPassword"
        ></v-text-field>
        
        <!-- Xác nhận mật khẩu -->
        <v-text-field
          v-model="confirmPassword"
          label="Xác nhận mật khẩu"
          :rules="[rules.required, passwordMatch]"
          variant="outlined"
          class="mb-6"
          prepend-inner-icon="mdi-lock-check"
          :type="showConfirmPassword ? 'text' : 'password'"
          :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showConfirmPassword = !showConfirmPassword"
        ></v-text-field>
        
        <!-- Điều khoản -->
        <v-checkbox
          v-model="agreeToTerms"
          :rules="[rules.agree]"
          label="Tôi đồng ý với các điều khoản và điều kiện"
          class="mb-6"
        ></v-checkbox>
        
        <v-btn
          type="submit"
          color="primary"
          block
          size="large"
          :loading="loading"
          :disabled="!isFormValid || loading"
        >
          Đăng ký
        </v-btn>
        
        <div class="text-center mt-6">
          <p class="text-gray-600 dark:text-gray-400">
            Đã có tài khoản? 
            <router-link 
              to="/login" 
              class="text-primary-600 hover:text-primary-800 font-medium"
            >
              Đăng nhập
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
  name: 'RegisterView',
  
  data() {
    return {
      userData: {
        hoTen: '',
        taiKhoan: '',
        email: '',
        soDienThoai: '',
        diaChi: '',
        matKhau: '',
        role: 'member'
      },
      confirmPassword: '',
      agreeToTerms: false,
      showPassword: false,
      showConfirmPassword: false,
      isFormValid: false,
      rules: {
        required: value => !!value || 'Trường này là bắt buộc',
        minLength: value => value.length >= 4 || 'Tối thiểu 4 ký tự',
        email: value => {
          const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
          return pattern.test(value) || 'Email không hợp lệ';
        },
        phone: value => {
          const pattern = /^[0-9]{10,11}$/;
          return pattern.test(value) || 'Số điện thoại không hợp lệ';
        },
        password: value => {
          const hasMinLength = value.length >= 6;
          return hasMinLength || 'Mật khẩu phải có ít nhất 6 ký tự';
        },
        agree: value => value || 'Bạn phải đồng ý với điều khoản để tiếp tục'
      }
    };
  },
  
  computed: {
    ...mapGetters('auth', ['loading', 'error']),
    
    passwordMatch() {
      return this.userData.matKhau === this.confirmPassword || 'Mật khẩu không khớp';
    }
  },
  
  methods: {
    ...mapActions('auth', ['register']),
    
    clearError() {
      this.$store.commit('auth/SET_ERROR', null);
    },
    
    async submitRegister() {
      if (!this.$refs.form.validate()) return;
      
      const success = await this.register(this.userData);
      
      if (success) {
        this.$router.push('/');
      }
    }
  },
  
  // Clear form when component is mounted
  mounted() {
    this.clearError();
    this.userData = {
      hoTen: '',
      taiKhoan: '',
      email: '',
      soDienThoai: '',
      diaChi: '',
      matKhau: '',
      role: 'member'
    };
    this.confirmPassword = '';
    this.agreeToTerms = false;
  }
};
</script> 