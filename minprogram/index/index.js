Page({
  data: {
    //判断小程序的API，回调，参数，组件等是否在当前版本可用。
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  onLoad: function () {
    // 查看是否授权
    wx.getSetting({
      success(res) {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称
          wx.getUserInfo({
            success: function (res) {
              var userInfo = res.userInfo;
              wx.redirectTo({
                url: '/webview/index?user=' + userInfo.nickName + '&img=' + userInfo.avatarUrl,
              });
            }
          })
        }
      }
    })
  },
  bindGetUserInfo : function (e) {
    var userInfo = e.detail.userInfo
    wx.redirectTo({
      url: '/webview/index?user=' + userInfo.nickName + '&img=' + userInfo.avatarUrl,
    });
  }
})
