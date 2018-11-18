Page({
  onLoad: function (option) {
    var urlparams = '';
    if (option) {
      if (option.user) {
        urlparams += '#login?user='+option.user+'&img='+option.img;
      }
    }
    this.setData({
      'urlparams': urlparams
    });
  }
})
