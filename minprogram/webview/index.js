Page({
  onLoad: function (option) {
    var that = this;
    
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
        ////使用新版录音接口，可以获取录音文件
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
  startRecord() {
    if (this.recordStatus != 'start') {
      this.recorderManager = wx.getRecorderManager();
      const options = {
        duration: 6000,
        sampleRate: 44100,
        numberOfChannels: 1,
        encodeBitRate: 220500,
        format: 'wav',
      }
      this.recorderManager.start(options);
      this.recordStatus = 'start';

      this.recorderManager.onError(function(res) {
        console.log('recorder error', res);
      });
      this.recorderManager.onStop(function(res) {
        console.log('recorder stop', res)
        this.tempFilePath = res.tempFilePath;
      });
    }
  },
  stopRecord() {
    if (this.recorderManager && this.recordStatus != 'stop') {
      this.recorderManager.stop();
      this.recordStatus = 'stop';
    }
  },
  playRecord() {

  },
  webMessage(e) {
    console.log(e.detail);
  },
  webLoad(e) {
    var url = e.detail.src;
    var sindex = url.indexOf('?nactive=');
    if (sindex != -1) {
      sindex += '?nactive='.length;
      var eindex = url.indexOf('#', sindex);
      var nactive = url.substring(sindex, eindex);
      if (nactive) {
        if (this[nactive]) {
          this[nactive]();
        }
      }
    }
    console.log(e.detail);
  },
  webError(e) {
    console.log(e.detail);
  }
})
