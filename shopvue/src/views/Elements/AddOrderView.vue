<template>
  <div class="order-confirmation">
    <!-- 用户个人信息 -->
    <el-row class="header" type="flex" justify="space-between">
      <el-dropdown v-if="userName" @command="handleCommand">
        <span class="el-dropdown-link">
          用户：{{ userName }} ，您好<i
            class="el-icon-arrow-down el-icon--right"
          ></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="display">首页</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <span v-else @click="goToLogin">请登录</span>
    </el-row>
    <!-- 卡片包裹内容 -->
    <el-card class="order-card">
      <el-form :model="form">
        <h2>添加订单</h2>
        <el-divider></el-divider>
        <!-- 收集用户地址 -->
        <h3>收货地址</h3>
        <el-form-item>
          <el-input
            v-model="form.address"
            placeholder="请输入收货地址"
          ></el-input>
        </el-form-item>

        <h3>确认订单信息</h3>

        <!-- 展示所选商品 -->
        <div
          v-for="(shop, index) in groupedGoods"
          :key="'shop--' + index"
          style="width: 100%"
        >
          <!-- 展示商店名 -->
          <div>
            <h3>商店名：{{ shop.shopName }}</h3>
          </div>

          <!-- 展示商品信息 -->
          <el-table style="font-size:18px;"  :cell-style="{ textAlign: 'center' }" :header-cell-style="{textAlign: 'center'}" :data="shop.items" class="order-table">
            <el-table-column
              prop="goodName"
              label="商品名"
              width="150px"
            ></el-table-column>
            <el-table-column label="商品图片" width="200px">
              <template slot-scope="scope">
                <img
                  :src="scope.row.image"
                  alt="商家未添加商品图片"
                  style="width: 150px; height: 150px"
                />
              </template>
            </el-table-column>
            <el-table-column
              prop="cartnum"
              label="数目"
              width="150px"
            ></el-table-column>
            <el-table-column
              prop="price"
              label="单价"
              width="150px"
            ></el-table-column>
            <el-table-column
              prop="totalPrice"
              label="总价"
              width="150px"
            ></el-table-column>
          </el-table>

          <!-- 每个商店的总价 -->
          <div :style="{ textAlign: 'right' }">
            <strong>{{ shop.shopName }} 总价: ￥{{ shop.shopTotal }}</strong>
          </div>
        </div>

        <!-- 总价和购买按钮 -->
        <div class="order-summary">
          <h3>订单总价: ￥{{ totalPrice }}</h3>
          <el-button type="primary" @click="handlePurchase">购买</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      form: {
        address: "", // 用户输入的地址
      },
      selectedGoods: [], // 从购物车传递过来的商品信息
      totalPrice: 0, // 计算的总价
      groupedGoods: [], // 按商店分组后的商品信息
    };
  },
  created() {
    const goods = JSON.parse(this.$route.query.goods || "[]");
    this.selectedGoods = goods;
    this.calculateTotalPrice();
  },
  mounted() {
    this.initialInfo();
  },
  methods: {
    async initialInfo() {
      this.getUserName();
      await this.loadGoodInfo();
      this.groupGoodsByShop(); // 分组商品
    },
    // 计算总价
    calculateTotalPrice() {
      this.totalPrice = this.selectedGoods.reduce(
        (sum, item) => sum + item.totalPrice,
        0
      );
    },
    // 分组商品
    groupGoodsByShop() {
      const shopMap = {};

      this.selectedGoods.forEach((item) => {
        if (!shopMap[item.shopName]) {
          shopMap[item.shopName] = {
            shopName: item.shopName,
            shopId: item.shopId,
            items: [],
            shopTotal: 0,
          };
        }
        shopMap[item.shopName].items.push(item);
        shopMap[item.shopName].shopTotal += item.totalPrice; // 计算每个商店的总价
      });

      this.groupedGoods = Object.values(shopMap);
    },
    // 处理购买操作，插入订单并跳转支付页面
    handlePurchase() {
      if (!this.form.address) {
        this.$message.error("请输入收货地址！");
        return;
      }

      // 准备订单数据
      const orderItems = [];
      const orderShops = [];

      this.groupedGoods.forEach((shop) => {
        // 添加商店信息
        orderShops.push({
          shopId: shop.shopId,
          totalMoney: shop.shopTotal,
        });

        // 为每个商店添加商品信息
        shop.items.forEach((item) => {
          orderItems.push({
            goodId: item.goodId,
            buyNum: item.cartnum,
          });
        });
      });

      // 组装订单数据
      const order = {
        userId: this.userId,
        address: this.form.address,
        totalPrice: this.totalPrice,
        state: 0, // 未付款
        orderItems: orderItems,
        orderShops: orderShops,
      };
      const token = localStorage.getItem("jwt");
      // 调用后端接口生成订单
      axios
        .post("http://localhost:8080/order/insert", order, {
          headers: {
            token: token,
          },
        })
        .then((response) => {
          if (response.data.code) {
            this.$message.success("订单生成成功！");
            var orderId = response.data.data;
            this.$router.push({
              path: "/payment",
              query: { totalPrice: this.totalPrice, orderId: orderId },
            });
          } else {
            this.$message.error(response.data.msg);
          }
        })
        .catch((error) => {
          this.$message.error("订单生成失败，请重试！");
          console.error(error);
        });
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
          "http://localhost:8080/getUsernameAndId",
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
    async loadGoodInfo() {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.get(
          "http://localhost:8080/getGoodsByIds",
          {
            headers: {
              token: token,
            },
            params: {
              goodIdsStr: this.selectedGoods
                .map((item) => item.goodId)
                .join(","),
            },
          }
        );

        if (response.data && response.data.data && response.data.code) {
          const goods = response.data.data; // 获取商品信息
          // 更新 selectedGoods，插入商品的详细信息
          this.selectedGoods = this.selectedGoods.map((item) => {
            const good = goods.find((g) => g.goodId === item.goodId);
            return {
              ...item,
              shopId: good.shopId,
              goodName: good.name,
              price: good.price,
              image: good.image,
              shopName: good.shopName,
              totalPrice: good.price * item.cartnum,
            };
          });
          // 计算总价
          this.calculateTotalPrice();
        }
      } catch (error) {
        console.error("加载商品信息时出错:", error);
      }
    },
    goToLogin() {
      this.$router.push("/login");
    },

    gotoDisplay() {
      this.$router.push("/display");
    },
  },
};
</script>
<style scoped>
shopping-page {
  padding: 20px;
}

.header {
  padding: 10px 20px;
  background-color: #f5f5f5;
}

.header span {
  font-size: 16px;
}
.order-card {
  width: 80%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  padding: 40px;
  margin:  40px auto;
}

.order-card .order-table {
  width: 800px; /* 表格宽度 */
  margin: 0 auto;
  padding: 0 auto;
}

.order-card .order-table img {
  width: 150px;
  height: 150px;
  object-fit: cover; /* 图片尺寸固定 */
}

.order-summary {
  display: flex;
  justify-content: space-between; /* 购买按钮和总价对称分布 */
  align-items: center;
  margin-top: 20px;
  padding: 0 ; /* 给内容与卡片边框添加适当的距离 */
}

.order-summary h3 {
  font-size: 18px;
}

.order-summary .el-button {
  font-size: 16px;
}
 
</style>