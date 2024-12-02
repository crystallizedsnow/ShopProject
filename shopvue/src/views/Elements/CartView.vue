<template>
  <div>
    <!-- 用户个人信息 -->
    <el-row class="header" type="flex" justify="space-between">
      <el-dropdown v-if="userName" @command="handleCommand">
        <span class="el-dropdown-link">
          用户：{{ userName }} ，您好<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="display">首页</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <span v-else @click="goToLogin">请登录</span>
    </el-row>

    <!-- 卡片容器，包含购物车内容 -->
    <el-card class="cart-card" shadow="hover">
      <h2>购物车</h2>

      <!-- 商品展示表格 -->
      <el-table :data="cartGoods" :cell-style="{ textAlign: 'center' }" :header-cell-style="{textAlign: 'center'}"  class="cart-table">
        <!-- 多选框列 -->
        <el-table-column width="55">
          <template slot-scope="scope">
            <el-checkbox
              @change="handleSelectionChange(scope.row)"
              :checked="isChecked(scope.row.goodId)"
              :key="scope.row.goodId"
            />
          </template>
        </el-table-column>

        <el-table-column
          prop="goodId"
          label="商品编号"
          width="180"
        ></el-table-column>
        <el-table-column prop="name" label="名称" width="180"></el-table-column>
        <el-table-column prop="price" label="价格" width="100"></el-table-column>
        <el-table-column label="数量" width="150">
          <template slot-scope="scope">
            <el-input-number
              v-model.number="scope.row.cartnum"
              :min="1"
              :max="scope.row.num"
              @change="updateGoodQuantity(scope.row)"
              class="input-number"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column prop="image" label="图片" width="200">
          <template slot-scope="scope">
            <img
              :src="scope.row.image"
              alt="商品图片缺失"
              width="150"
              height="150"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="shopName"
          label="商店名"
          width="120"
        ></el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button
              @click="deleteGood(scope.row.goodId)"
              type="danger"
              size="small"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 合计价格和结算按钮 -->
      <div class="total-section">
        <span>合计：{{ totalPrize }} 元</span>
        <el-button type="primary" @click="handleOrder">结算</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      cartGoods: [], // 购物车商品列表
      totalPrize: 0, // 总价
      userId: "", // 用户ID
      userName: "", // 用户名
      selectedGoods: [], // 选中的商品列表
    };
  },
  mounted() {
    this.initializePage(); // 确保页面初始化时，先获取用户名，再加载购物车
  },
  watch: {
    // 监听购物车商品变化，动态计算总价
    selectedGoods: {
      handler: "calculateTotalPrize",
      deep: true,
    },
  },
  methods: {
    async initializePage() {
      await this.getUserName(); // 等待用户名获取完成
      this.loadCartGoods(); // 获取购物车商品
    },
    async getUserName() {
      const token = localStorage.getItem("jwt");
      if (!token) {
        this.userName = null;
        this.$message.error("未能获取用户信息，请重新登录");
        this.$router.push("/login");
        return;
      }
      try {
        const response = await axios.get(
          "http://8.155.18.88/api/getUsernameAndId",
          {
            headers: { token: token },
          }
        );
        if (response.data.code) {
          this.userName = response.data.data.username;
          this.userId = response.data.data.userId; // 获取到 userId
        } else {
          console.error("未能获取用户信息，请重新登录", error);
          this.$message.error("未能获取用户信息，请重新登录");
        }
      } catch (error) {
        console.error("未能获取用户信息，请登录", error);
        this.$message.error("未能获取用户信息，请登录");
        this.userName = null;
      }
    },
    // 判断商品是否已被选中
    isChecked(goodId) {
      return this.selectedGoods.some((item) => item.goodId === goodId);
    },
    // 加载购物车商品
    loadCartGoods() {
      const token = localStorage.getItem("jwt");
      axios
        .get("http://8.155.18.88/api/cart/getGoods", {
          headers: { token: token },
          params: { userId: this.userId }, // 确保 userId 已经赋值
        })
        .then((response) => {
          if (response.data && response.data.data && response.data.msg) {
            this.cartGoods = response.data.data;
          } else {
            console.error("未找到购物车商品记录:", response.data);
            this.$message.error("未找到购物车商品记录")
          }
        })
        .catch((error) => {
          console.error("加载购物车失败:", error);
          this.$message.error("加载购物车失败:");
        });
    },
    // 删除商品
    async deleteGood(goodId) {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.delete(
          "http://8.155.18.88/api/cart/delete",
          {
            headers: { token: token },
            params: { goodId: goodId, userId: this.userId },
          }
        );
        if (response.data.code === 1) {
          this.loadCartGoods(); // 删除后刷新购物车
        } else {
          this.$message.error(response.data.message);
        }
      } catch (error) {
        console.error("删除商品失败:", error);
        this.$message.error("删除失败");
      }
    },

    // 更新商品数量
    updateGoodQuantity(good) {
      const token = localStorage.getItem("jwt");
      axios
        .post(
          "http://8.155.18.88/api/cart/updateNum",
          { goodId: good.goodId, num: good.cartnum, userId: this.userId },
          { headers: { token: token } }
        )
        .then((response) => {
          if (response.data.code !== 1) {
            this.$message.error("更新数量失败");
          }
        })
        .catch((error) => {
          console.error("更新数量失败:", error);
          this.$message.error("更新数量失败");
        });
    },

    // 处理商品选择变化
    handleSelectionChange(row) {
      const index = this.selectedGoods.findIndex(
        (item) => item.goodId === row.goodId
      );
      if (index !== -1) {
        // 如果选中则从 selectedGoods 移除
        this.selectedGoods.splice(index, 1);
      } else {
        // 否则添加完整的 row 对象到 selectedGoods
        this.selectedGoods.push(row);
      }
    },

    // 计算合计价格，只计算选中的商品
    calculateTotalPrize() {
      this.totalPrize = this.selectedGoods.reduce((total, item) => {
        return total + item.price * item.cartnum;
      }, 0);
    },

    // 结算跳转到订单页面，并传递选中的商品
    handleOrder() {
       if(this.selectedGoods.length==0){
        this.$message.error("请选择商品");
        return;
      }
      const selectedItems = this.selectedGoods.map((item) => ({
        goodId: item.goodId,
        cartnum: item.cartnum,
      }));
     
      this.$router.push({
        path: "/addOrder",
        query: {
          goods: JSON.stringify(selectedItems), // 将选中的商品作为参数传递
        },
      });
    },
      handleCommand(command) {
      if (command === 'display') {
        this.goToDisplay();
      } else if (command === 'logout') {
        this.goToLogin();
      }
    },
    goToLogin() {
      this.$router.push("/login");
    },

    goToDisplay() {
      this.$router.push("/display");
    },
  },
};
</script>

<style scoped>
.shopping-page {
  padding: 20px;
}

.header {
  padding: 10px 20px;
  background-color: #f5f5f5;
}

.header span {
  font-size:16px
}
/* 调整卡片容器的宽度和上方距离 */
.cart-card {
  width: 70%;
  margin: 40px auto 20px; /* 上方距离为40px，底部距离为20px */
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /* 增加卡片阴影 */
}

/* 表格字体大小和边框阴影 */
.cart-table {
  width: 80%;
  border-collapse: collapse; /* 去除表格间的边框 */
  margin: 0 auto;
  padding: 0 auto;
}
.cart-table .el-table th, .cart-table .el-table td {
  font-size: 16px;
  padding: 10px;
  text-align: left;
  border: 1px solid #ebeef5; /* 添加边框 */
}
.cart-table .el-table th {
  background-color: #f9fafc; /* 表头背景色 */
}

/* 调整输入框宽度 */
.input-number {
  width: 95%;
}

/* 合计和结算部分样式 */
.total-section {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  padding: 0 20px; /* 增加合计与结算部分与两侧的距离 */
}

/* 调整按钮和合计之间的间距 */
.total-section .el-button {
  margin-left: 20px; /* 结算按钮与合计部分保持合适的距离 */
}
</style>
