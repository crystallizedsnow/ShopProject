<template>
  <div id="app" class="app-container">
    <h1 class="title">用户注册</h1>
    <div class="form-container">
      <el-form :model="form" label-width="80px" class="demo-form">
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户类型">
          <el-radio-group v-model="form.type">
            <el-radio label="我是用户"></el-radio>
            <el-radio label="我是商家"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type === '我是商家'" label="商店名">
          <el-input
            v-model="form.shopName"
            placeholder="请输入商店名"
          ></el-input>
        </el-form-item>
        <div class="button-container">
          <el-button type="success" @click="handleRegister">注册</el-button>
        </div>
        <!-- 登录提示 -->
        <div class="login-link">
          <router-link to="/login">已有账号？去登陆！</router-link>
        </div>
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
        username: "",
        password: "",
        shopId: "",
        type: "0",
        existShop: "0",
        shopName: "", // 商店名
      },
      flag: true,
      message: "",
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
      var ePattern =
        /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
      if (
        this.form.email === "" ||
        this.form.username === "" ||
        this.form.password === ""
      ) {
        this.message = "信息没填写完整";
        this.notice();
        this.flag = false;
      } else if (!ePattern.test(this.form.email)) {
        this.$message.warning("邮箱格式不对");
        this.flag = false;
      } else if (this.form.type === "我是用户") {
        this.form.type = "0";
      } else if (this.form.type === "我是商家") {
        this.form.type = "1";
          if (!this.form.shopName) {
      this.message = "商店名不能为空";
      this.notice();
      this.flag = false;
      return;
    }
      } else {
        this.message = "请选择您是用户还是商家。";
        this.notice();
        this.flag = false;
      }
    },

    async handleRegister() {
  this.check();

  if (this.flag === true) {
    try {
      // 提取商店名
      const { shopName, ...userInfo } = this.form;

      // 如果类型为商家并且商店名不为空，传递商店名
      const shopParam = this.form.type === "1" && shopName ? `?shopName=${encodeURIComponent(shopName)}` : "";

      const response = await axios.post(
        `http://8.155.18.88/api/register${shopParam}`, // 拼接 shopName 参数
        userInfo, // 其余数据作为 JSON 请求体
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      this.$message.success("注册成功,请登录");
      this.$router.push("/login");  
    } catch (error) {
      console.error("注册失败", error);
      this.message = "注册失败，请重试";
      this.notice();
    }
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

.el-button {
  margin-right: 10px;
  text-align: center;
}
.b1 {
  text-align: center;
}
.button-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.login-link {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
