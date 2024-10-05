import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)
const routes = [
  {
    path: '/',
    redirect: 'display'
  },
  {
    path:'/display',
    name:'display',
    component:() => import('../views/Elements/DisplayGoodView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Elements/LoginView.vue')
  },
  {
    path:'/register',
    name:'register',
    component:() => import('../views/Elements/RegisterView.vue')
  },
  {
    path:'/shop',
    name:'shop',
    component:() => import('../views/Elements/ManageGoodView.vue')
  },
  {
    path:'/cart',
    name:'cart',
    component:()=>import('../views/Elements/CartView.vue')
  },
  {
    path:'/addorder',
    name:'addorder',
    component:()=>import('../views/Elements/AddOrderView.vue')
  },
  {
    path:'/payment',
    name:'payment',
    component:()=>import('../views/Elements/PayView.vue')
  },
  {
    path:'/showorder',
    name:'showorder',
    component:()=>import('../views/Elements/DisplayOrderView.vue')
  },
  {
    path:'/manageorder',
    name:'manageorder',
    component:()=>import('../views/Elements/ManageOrderView.vue')
  },
  {
    path:'/paymentsuccess',
    name:'paymentsuccess',
    component:()=>import('../views/Elements/PaySuccessView.vue')
  },
]
const router = new VueRouter({
  mode: 'history',  // 使用 history 模式，去掉 URL 中的 `#`
  routes
})

export default router
