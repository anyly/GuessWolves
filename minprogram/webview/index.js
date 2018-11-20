Page({
  onLoad: function (option) {
    var that = this;
    
    var urlparams = '';
    if (option) {
      if (option.user) {
        urlparams += '#login?user=' + option.user + '&img=' + option.img;
      }
    }
    that.setData({
      'urlparams': urlparams
    });
      
    
  },
  startRecord() {
    var that = this;
    if (this.recordStatus != 'start') {
      this.recorderManager = wx.getRecorderManager();
      var options = {
        duration: 6000,
        sampleRate: 44100,
        numberOfChannels: 1,
        encodeBitRate: 220500,
        format: 'wav',
      }
      wx.authorize({
        scope: 'scope.record',
        success() {
          // 识别授权
          console.log("录音授权成功");
          that.recorderManager.start(options);
          that.recordStatus = 'start';

          that.recorderManager.onError(function (res) {
            console.log('recorder error', res);
          });
          that.recorderManager.onStop(function (res) {
            console.log('recorder stop', res)
            that.tempFilePath = res.tempFilePath;
          });
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
