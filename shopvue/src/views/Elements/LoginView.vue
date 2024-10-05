<template>
  <div id="app" class="app-container">
    <h1 class="title">购物系统登录</h1>
    <div class="form-container">
      <el-form :model="form" label-width="80px" class="demo-form">
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <!-- 按钮容器 -->
        <div class="button-container">
          <el-button type="success" @click="handleLogin">登录</el-button>
        </div>

        <el-row>
          <el-col :span="12">
            <div class="to-display-link">
              <router-link to="/display">只想逛逛？返回主界面！</router-link>
            </div>
          </el-col>
          <!-- 注册提示 -->
          <el-col :span="12">
            <div class="register-link">
              <router-link to="/register">没有账号？注册一个！</router-link>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      form: {
        email: "",
        password: "",
        message: "",
      },
    };
  },
  methods: {
    notice() {
      this.$alert(this.message, "注册失败", {
        confirmButtonText: "确定",
      });
    },
    check() {
      this.flag = true;
      if (this.form.email === "") {
        this.message = "请输入用户名";
        this.notice();
        this.flag = false;
      } else if (this.form.password === "") {
        this.message = "请输入密码";
        this.notice();
        this.flag = false;
      }
    },
    async handleLogin() {
      this.check();

      try {
        const response = await axios.post(
          "http://localhost:8080/login",
          this.form
        );
        // console.log(response.data.data.type);
        // 根据用户类型进行重定向
        if (response.data.code == 0) {
          console.log(response.data.msg);
          this.password = "";
          this.email = "";
          this.message = response.data.msg;
          this.notice();
        }
        else {
          if (response.data.data.type === 0) {
            console.log("登录成功", response.data);
            // 存储JWT到本地
            localStorage.setItem("jwt", response.data.data.token);
            console.log("存储的JWT:", localStorage.getItem("jwt")); // 输出存储的JWT
            this.$router.push("/display"); // 用户类型为0，跳转到展示商品页面
          } else if (response.data.data.type === 1) {
            console.log("登录成功", response.data);
            // 存储JWT到本地
            localStorage.setItem("jwt", response.data.data.token);
            this.$router.push("/shop");
          } // 用户类型为1，跳转到商店页面
        }
      } catch (error) {
        this.message = error;
        this.notice();
        console.error("登录失败", error);
      }
    },
  },
};
</script>

<style scoped>
.app-container {
  background-color: white;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: black;
}

.title {
  font-size: 2em;
  margin-bottom: 20px;
}

.form-container {
  width: 400px;
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.el-form-item label {
  color: black;
}

.el-input input {
  background-color: #444;
  color: blue;
}

.button-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.to-display-link {
  display: flex;
  margin-top: 10px;
  justify-content: flex-start;
}
.register-link {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
