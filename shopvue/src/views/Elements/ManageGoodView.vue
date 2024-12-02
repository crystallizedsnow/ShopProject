<template>
  <div class="shop-manage-container">
    <!-- 顶部栏 -->
    <el-row class="header" type="flex" justify="space-between" align="middle">
      <span class="shop-name">商店名：{{ shopName }}</span>
      <el-dropdown class="manage-dropdown" @command="handleCommand">
        <span class="el-dropdown-link">
          菜单 <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu class="manage-dropdown-menu" slot="dropdown">
          <el-dropdown-item command="data"> 管理商店数据</el-dropdown-item>
          <el-dropdown-item command="order">管理订单</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-row>

    <!-- 搜索功能 -->
    <el-row type="flex" justify="center" align="middle" class="search-bar">
      <el-col :span="5">
        <el-input v-model="searchName" placeholder="搜索商品名称"></el-input>
      </el-col>
      <!-- 留空一段距离 -->
      <el-col :span="1">
        <el-button type="primary" @click="searchGoods">搜索</el-button>
      </el-col>
      <el-col :span="4">
        <el-select v-model="searchType" placeholder="选择类型">
          <el-option class="choose-menu" label="电子产品" value="1"></el-option>
          <el-option class="choose-menu" label="食品" value="10"></el-option>
        </el-select>
      </el-col>
        <el-col :span="3">
        <el-button type="primary" @click="clearAllFilters">清空所有条件</el-button>
      </el-col>
    </el-row>
  
    <!-- 商品展示表格和增加商品按钮 -->
    <el-row type="flex" justify="center" class="table-and-button">
      <el-col :span="20">
        <el-table :data="goods" :cell-style="{ textAlign: 'center' }" :header-cell-style="{textAlign: 'center'}" style="width: 100%">
          <el-table-column prop="goodId" label="商品编号"></el-table-column>
          <el-table-column prop="name" label="名称"></el-table-column>
          <el-table-column prop="price" label="价格"></el-table-column>
          <el-table-column prop="num" label="数量"></el-table-column>
          <el-table-column prop="typeName" label="类型"></el-table-column>
          <el-table-column prop="image" label="图片">
            <template slot-scope="scope">
              <img
                :src="scope.row.image"
                alt="商品图片缺失"
                width="50"
                height="50"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button
                @click="deleteGood(scope.row.goodId)"
                type="danger"
                size="small"
                >删除</el-button
              >
              <el-button
                @click="openUpdateDialog(scope.row)"
                type="primary"
                size="small"
                >更新</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <!-- 插入商品按钮 -->
    <el-row type="flex" justify="center" class="table-and-button">
      <el-col :span="20" class="text-center">
        <el-button type="success" @click="openInsertDialog">增加商品</el-button>
      </el-col>
    </el-row>

    <!-- 插入/更新商品弹窗 -->
    <el-dialog
      :visible.sync="dialogVisible"
      @close="handleClose"
      title="商品信息"
    >
      <el-form :model="form">
        <el-form-item label="名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.price"></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="form.num"></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option
              v-for="item in types"
              :key="item.type"
              :label="item.typeName"
              :value="item.type"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleChange"
            :on-remove="handleRemove"
            :on-preview="handlePreview"
            :on-exceed="handleExceed"
            :limit="1"
            ref="upload"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitGood">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      shopId: "",
      shopName: "", // 商户名称
      userName: "", //用户名称
      searchName: "", // 搜索框商品名称
      searchType: null, // 搜索框商品类型
      goods: [], // 商品列表
      message: "",
      form: {
        name: "",
        price: "",
        num: "",
        type: null,
        image: "",
        shopId: "",
        goodId: "",
        typeName: "",
      },
      types: [],
      dialogVisible: false, // 控制弹窗显示
      fileList: [],
    };
  },
  mounted() {
    this.getAllTypes();
    this.getUserName();
  },
  methods: {
    async getUserName() {
      const token = localStorage.getItem("jwt"); // 从本地存储中获取JWT

      if (!token) {
        this.userName = null; // 如果没有JWT，显示“请登录”
        return;
      }

      await axios
        .get("http://8.155.18.88/api/getUsernameAndId", {
          headers: {
            token: token,
          },
        })
        .then((response) => {
          this.userName = response.data.data.username;
          this.userId = response.data.data.userId;
          this.getShopName();
        })
        .catch((error) => {
          console.error("未能获取用户信息", error);
          this.userName = null; // 如果获取用户名失败，显示“请登录”
        });
    },
    async getShopName() {
      const token = localStorage.getItem("jwt");
      if (!token) {
        this.shopName = null; // 如果没有JWT，显示“请登录”
        return;
      }
      try {
        const response = await axios.get(
          "http://8.155.18.88/api/manageGood/getShop",
          {
            headers: {
              token: token, // 将 token 作为请求头传递
            },
            params: {
              userName: this.userName,
            },
          }
        );
        this.shopName = response.data.data.name || null; // 确保正确赋值
        this.shopId = response.data.data.shopId;
        this.getGoods();
      } catch (error) {
        console.error("未能获取用户信息", error);
        this.shopName = null; // 如果获取失败，显示“请登录”
        this.shopId = null;
      }
    },

    async getGoods() {
      this.loading = true;
      this.clearGoods();
      try {
        const response = await axios.get(
          "http://8.155.18.88/api/manageGood/goods",
          {
            params: {
              shopName: this.shopName,
              goodName: this.searchName,
              type: this.searchType,
            },
          }
        );
        if (response.data && response.data.data && response.data.data.rows) {
          this.goods = [...this.goods, ...response.data.data.rows];

          // 为每个商品匹配 typeName
          this.goods.forEach((good) => {
            const matchedType = this.types.find(
              (typeItem) => typeItem.type === good.type
            );
            if (matchedType) {
              good.typeName = matchedType.typeName; // 匹配到 typeName
            } else {
              good.typeName = "未知类型"; // 如果没有匹配到
            }
          });
        } else {
          console.error("未找到记录:", response.data);
        }
      } catch (error) {
        console.error("获取商品失败", error);
      } finally {
        this.loading = false;
      }
    },

    async getAllTypes() {
      this.loading = true;
      try {
        const response = await axios.get(
          "http://8.155.18.88/api/getTypeName/all"
        );
        if (response.data && response.data.data) {
          this.types = response.data.data;
        } else {
          console.error("获取 types 失败");
        }
      } catch (error) {
        console.error("获取 types 请求失败", error);
      } finally {
        this.loading = false;
      }
    },

    clearGoods() {
      this.goods = [];
    },
    searchGoods() {
      this.getGoods(); // 调用接口获取搜索结果
    },
    openInsertDialog() {
      this.dialogVisible = true;
      this.form = {}; // 清空表单
      this.form.id = 0;
    },
    openUpdateDialog(good) {
      this.dialogVisible = true;
      this.form = { ...good }; // 填入商品信息进行更新
      this.form.id = 1;
      this.form.goodId = good.goodId;
    },
    handleChange(file) {
      if (file.status !== "ready") {
        this.fileList[0] = null;
        return;
      }
      this.fileList[0] = file;
      return false; // 阻止自动上传
    },
    handleRemove() {
      // 移除图片时清空相关数据
      this.fileList[0] = null;
    },
    handleExceed(files, fileList) {
      this.$message.warning("只能上传一张图片");
    },
    handlePreview(file) {
      // 点击预览图片时，展示大图
      window.open(this.fileList[0]);
    },
    handleClose() {
      this.fileList[0] = null;
      this.dialogVisible = false;
      this.$refs.upload.clearFiles();
    },
    async submitGood() {
      const token = localStorage.getItem("jwt");
      this.form.shopId = this.shopId;
      // 如果选择了图片，则先上传图片
      if (this.fileList[0]) {
        const formData = new FormData();
        formData.append("image", this.fileList[0].raw);

        try {
          const uploadResponse = await axios.post(
            "http://8.155.18.88/api/manageGood/uploadImage",
            formData,
            {
              headers: {
                token: token,
                "Content-Type": "multipart/form-data",
              },
            }
          );

          if (uploadResponse.data.code) {
            this.form.image = uploadResponse.data.data; // 将上传后的图片URL赋值给表单
            this.$message.success("图片上传成功");
          } else {
            this.$message.error("图片上传失败");
            return;
          }
        } catch (error) {
          this.$message.error("图片上传失败");
          return;
        }
      }
      // 检查商品信息是否填写完整
      if (
        !this.form.name ||
        !this.form.price ||
        !this.form.num ||
        !this.form.type ||
        !this.form.shopId ||
        !this.form.image
      ) {
        this.$message.error("请填写完整的商品信息");
        return;
      }
      // 接着上传商品信息
      let response;
      const config = {
        headers: {
          token: token,
        },
      };

      if (this.form.id) {
        response = await axios.post(
          "http://8.155.18.88/api/manageGood/updateGood",
          this.form,
          config
        );
      } else {
        response = await axios.post(
          "http://8.155.18.88/api/manageGood/insertGood",
          this.form,
          config
        );
      }

      if (response.data.code) {
        this.$message.success("操作成功");
        this.dialogVisible = false;
        this.getGoods(); // 刷新商品列表
      } else {
        this.$message.error(response.data.msg);
      }
    },
    async deleteGood(goodId) {
      const token = localStorage.getItem("jwt");
      const config = {
        headers: {
          token: token,
        },
        params: {
          goodId: goodId, // 通过 params 传递 goodId
        },
      };

      try {
        const response = await axios.delete(
          "http://8.155.18.88/api/manageGood/deleteGood",
          config
        );
        if (response.data.code) {
          this.getGoods(); // 删除后刷新列表
        } else {
          this.$message.error(response.data.message);
        }
      } catch (error) {
        console.error("Error deleting the good:", error);
        this.$message.error("删除失败");
      }
    },
    handleUploadSuccess(response) {
      this.form.image = response.url; // 获取上传成功后的图片URL
    },
    handleCommand(command) {
      if (command === "data") {
        this.goToManageData();
      } else if (command === "order") {
        this.goToManageOrder();
      } else if (command == "logout") {
        this.logout();
      }
    },
    goToManageData() {
      this.$router.push("/manageData");
    },
    goToManageOrder() {
      this.$router.push("/manageOrder");
    },
    logout() {
      localStorage.removeItem("jwt"); // 删除本地存储中的JWT
      this.userName = null;
      this.$router.push("/login"); // 路由到登录页面
    },
      clearAllFilters() {
        this.searchName= "", // 搜索框商品名称
      this.searchType=null, // 搜索框商品类型
      this.goods=[], // 商品列表
      this.getGoods();
    },
  },
};
</script>

<style scoped>
.shop-manage-container {
  padding: 20px;
}

.header {
  padding: 10px 20px;
  background-color: #f5f5f5;
  font-size: 18px; 
}

.el-dropdown-link {
  font-size: 18px; 
  font-weight: bold;
}
.manage-dropdown-menu{
  font-size: 18px; 
  font-weight: bold;
}
.search-bar {
  margin: 20px 0;
}

.spacer {
  width: 20px; /* 用于搜索框和选择框之间的空隙 */
}

.table-and-button {
  margin-top: 20px;
}

.el-upload {
  width: 100px;
  height: 100px;
}

.el-col {
  text-align: center;
}

.el-table {
  text-align: center; /* 表格字体居中 */
  padding: 10px;
  font-size: 18px; /* 字体大小调整 */
}
.el-table th, .el-table td {
  text-align: center; /* 表格字体居中 */
  padding: 10px;
  font-size: 18px; /* 字体大小调整 */
}
.el-table .el-table-column {
  white-space: nowrap;
  word-break: keep-all;
}
.el-button{
  font-size: 15px; /* 字体大小调整 */
}
.choose-menu{
  font-size:15px;
}
</style>
