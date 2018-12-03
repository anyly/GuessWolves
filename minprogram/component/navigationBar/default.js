// component/navigationBar/default.js
Component({
  attached() {
    var sys = wx.getSystemInfoSync();
    let totalTopHeight = 68
    if (sys.model.indexOf('iPhone X') !== -1) {
      totalTopHeight = 88
    } else if (sys.model.indexOf('iPhone') !== -1) {
      totalTopHeight = 64
    }

    this.setData({
      statusBarHeight: sys.statusBarHeight,
      titleBarHeight: totalTopHeight - sys.statusBarHeight
    });
  },
  /**
   * 组件的属性列表
   */
  properties: {
    backicon: {
      type: String,
      value: 'https://www.idearfly.com/static/image/backicon.svg',
    },
    title: {
      type: String,
      value: __wxConfig.global.window.navigationBarTitleText
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    canBack: getCurrentPages().length > 1
  },

  /**
   * 组件的方法列表
   */
  methods: {
    back() {
      wx.navigateBack({
        delta: 1
      });
      this.triggerEvent('back');
    },
    config() {
      this.triggerEvent('config');
    }
  }
})
