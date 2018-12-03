function connectSocket(opt) {
  var socketTask = wx.connectSocket(opt);
  //
  bindHttp(socketTask);

  bindEmit(socketTask);

  bindAdmit(socketTask);

  bindOnMessage(socketTask);

  return socketTask;
}

const listeners = {};

function bindHttp(socketTask) {
  socketTask.http = function (opt) {
    var action = opt.action;
    var data = opt.data;
    var success = opt.success;

    opt.success = null;
    if (!action) {
      throw ReferenceError('action is '+action);
    }
    var action = 'http_' + action;
    listeners[action] = success;

    var request = {
      action: action,
      data: data
    };
    opt.data = JSON.stringify(request);
    socketTask.send(opt);
  }
}

function bindEmit(socketTask) {
  socketTask.emit = function (opt) {
    var action = opt.action;
    var data = opt.data;
    var success = opt.success;

    if (!action) {
      throw ReferenceError('action is ' + action);
    }

    var request = {
      action: action,
      data: data
    };
    opt.data = JSON.stringify(request);
    socketTask.send(opt);
  }
}

function bindAdmit(socketTask) {
  socketTask.admit = function (action, callback) {
    if (listeners[action]) {
      listeners[action] = callback;
    }
  }
}

function bindOnMessage(socketTask) {
  socketTask.onMessage(function (opt) {
    var data = opt.data;
    if (typeof (data) == 'string') {
      if (data.startsWith('{') || data.startsWith('[')) {
        const json = JSON.parse(data);
        opt.data = json.data;

        var action = json.action;
        if (action) {
          if (listeners[action]) {
            listeners[action](opt);
          }
        }
      }
    }
  });
}

module.exports = {
  connectSocket: connectSocket
};