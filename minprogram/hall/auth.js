Page({
  data: {
    //判断小程序的API，回调，参数，组件等是否在当前版本可用。
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  onLoad: function () {
    var that = this;
    // 查看是否授权
    wx.getSetting({
      success(res) {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称
          wx.getUserInfo({
            success: function (res) {
              var userInfo = res.userInfo;
              that.authlogin(userInfo);
            }
          })
        }
      }
    })
  },
  bindGetUserInfo: function (e) {
    var userInfo = e.detail.userInfo
    this.authlogin(userInfo);
  },
  authlogin(userInfo) {
    wx.setStorageSync('user', userInfo.nickName);
    wx.setStorageSync('img', userInfo.avatarUrl);
    wx.redirectTo({
      url: '/hall/index'
    });
  }
})
