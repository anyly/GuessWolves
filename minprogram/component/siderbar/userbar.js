// component/siderbar/userbar.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    isShow: {
      type: Boolean,
      value: false
    },
    websocket: {
      type: Object,
      value: null
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    user: wx.getStorageSync('user'),
    img: wx.getStorageSync('img')
  },

  /**
   * 组件的方法列表
   */
  methods: {
    showBar() {
      this.setData({
        isShow: true
      });
    },
    hideBar() {
      this.setData({
        isShow: false
      });
    }
  }
})
