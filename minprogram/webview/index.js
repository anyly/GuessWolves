Page({
  onLoad: function (option) {
    var that = this;
    // recorderManager = wx.getRecorderManager();
    // const options = {
    //   duration: 6000,
    //   sampleRate: 44100,
    //   numberOfChannels: 1,
    //   encodeBitRate: 22050,
    //   format: 'wav',
    // }
    
    wx.authorize({
      scope: 'scope.record',
      success() {
        // 识别授权
        console.log("录音授权成功");
        //第一次成功授权后 状态切换为2
        var urlparams = '';
        if (option) {
          if (option.user) {
            urlparams += '#login?user=' + option.user + '&img=' + option.img;
          }
        }
        that.setData({
          'urlparams': urlparams
        });
        // 用户已经同意小程序使用录音功能，后续调用 wx.startRecord 接口不会弹窗询问
        // wx.startRecord();
        //recorderManager.start(options);//使用新版录音接口，可以获取录音文件
      },
      fail() {
        wx.showModal({
          title: '提示',
          content: '您未授权录音，功能将无法使用',
          showCancel: true,
          confirmText: "授权",
          confirmColor: "#52a2d8",
          success: function (res) {
          },
          fail: function () {
            console.log("openfail");
          }
        });
      }
    });
    
  },
  webMessage : function (e) {
    console.log(e.detail);
  },
  webLoad : function (e) {
    console.log(e.detail);
  },
  webError: function (e) {
    console.log(e.detail);
  }
})
