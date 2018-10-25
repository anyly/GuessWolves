/**
 * Created by idear on 2018/9/21.
 */
(function () {
    var httpPrefix = 'http_';

    var original = window['WebSocket'];

    function getNo() {
        if (window.location.hash != '' && window.location.hash != '#') {
            var start = window.location.hash.indexOf('#') + 1;
            var end = window.location.hash.indexOf('?', start);
            if (end >= 0) {
                var no = window.location.hash.substring(start, end);
                return parseInt(no);
            }
            var no = window.location.hash.substring(start);
            return parseInt(no);
        }
        return null;
    }

    window.WebSocketClient = function(url) {
        var self = this;
        var websocket = null;
        var httplistener = {};
        var messagelistener = {};
        this.readyState = 0;

        if (original) {
            websocket = new original(url);
        } else {
            throw new Error('当前浏览器 Not support service');
        }

        function isSupport() {
            return websocket;
        }

        // 关闭窗口前
        document.addEventListener('onbeforeunload', function () {
            websocket.close();
        });

        // 切换窗口
        document.addEventListener('visibilitychange', function(){
            if(document.visibilityState=='hidden') { //状态判断
                websocket.close();
            }else {
                window.location.reload(true);
            }
        });

        /**
         * 注册消息
         * {function({data: (String|Blob|ArrayBuffer)})}
         **/
        websocket.onmessage = function (event) {
            var message = event.data;
            var data = eval('('+message+')');//JSON.parse(message);
            try {
                var action = data.action;
                var object = data["data"];
                if (action.indexOf(httpPrefix) == 0) {
                    httplistener[action](object);
                } else {
                    messagelistener[action](object);
                }
            } catch (e) {
                console.error('admit error:"' + message + '"\n at ' + '\'' + e.script + '\'' + '第' +e.line + '行'+'第'+e.column+'列');
            }
        };


        /**
         * 发送与回应,像一起http请求
         * {function({data: (String|Blob|ArrayBuffer)})}
         **/
        this.http = function (action, param, callback) {
            if (!isSupport()) {
                return this;
            }
            var data = {
                action: httpPrefix+action,
                data : param
            };
            websocket.send(JSON.stringify(data));
            if (callback) {
                httplistener[httpPrefix+action] = callback;
            }
            return this;
        };

        /**
         * 发送数据
         * {function({data: (String|Blob|ArrayBuffer)})}
         **/
        this.emit = function (action, param) {
            if (!isSupport()) {
                return this;
            }
            var data = {
                action: action,
                data : param
            };
            websocket.send(JSON.stringify(data));
            return this;
        };

        /**
         * 注册监听器用于大量接收
         * {function({data: (String|Blob|ArrayBuffer)})}
         **/
        this.listen = function(listener) {
            if (!isSupport()) {
                return this;
            }
            if (listener) {
                messagelistener.assign(listener);
            }
            return this;
        };

        /**
         * 接收数据
         * {function({data: (String|Blob|ArrayBuffer)})}
         **/
        this.admit = function (action, callback) {
            if (!isSupport()) {
                return this;
            }
            if (callback) {
                messagelistener[action] = callback;
            }
            return this;
        };

        /**
         * 连接事件
         * {function(Event)}
         **/
        this.onConnect = function (callback) {
            if (!isSupport()) {
                return this;
            }
            this.readyState = 1;
            websocket.onopen = callback;
            return this;
        };

        /**
         * 主动断开
         * @param {number} [code]
         * @param {string} [reason]
         */
        this.disconnect = function (code, reason) {
            if (!isSupport()) {
                return this;
            }
            this.readyState = 2;
            websocket.close.apply(websocket, arguments);
            return this;
        };

        /**
         * 断线事件
         * {function({code: Number, reason: String, wasClean: Boolean})}
         **/
        this.onDisconnect = function (callback) {
            if (!isSupport()) {
                return this;
            }
            this.readyState = 3;
            websocket.onclose = function (code, reason , wasClean) {
                if (!code) {
                    return;
                }
                callback.apply(self, arguments);
                httplistener = {};
                messagelistener = {};
            };
            return this;
        };

        /**
         * {function(Event)}
         **/
        this.onError = function (callback) {
            if (!isSupport()) {
                return this;
            }
            websocket.onerror = function (event) {
                console.error(JSON.stringify(event));
                if (callback) {
                    callback(event);
                }
            }
            return this;
        };

        /**
         * 断线重连
         * @returns {*}
         */
        this.reconnect = function () {
            if (!isSupport()) {
                return this;
            }
            if (this.readyState >= 3) {
                if (original) {
                    websocket = new original(url);
                } else {
                    alert('当前浏览器 Not support service');
                    return null;
                }
            }
            return this;
        };
        return this;
    };

    /**
     * 登录,{房间号, 用户名, 头像}
     * @param no
     * @param user
     * @param img
     */
    WebSocketClient.prototype.login = function (user, img, callback) {
        this.http('login', {
            user: user,
            img: img
        }, callback);
        return this;
    };

    /**
     * 登出
     */
    WebSocketClient.prototype.logout = function (callback) {
        this.http('logout', null , callback);
        return this;
    };

    /**
     * 创建游戏, 返回房间号
     * @param callback -> int
     */
    WebSocketClient.prototype.newGame = function (data, callback) {
        this.http('newGame', data , callback);
        return this;
    };

    /**
     * 删除游戏
     * @param callback
     */
    WebSocketClient.prototype.removeGame = function (no, callback) {
        this.http('removeGame', no, callback);
        return this;
    };

    /**
     * 加入游戏
     */
    WebSocketClient.prototype.joinGame = function (no, callback) {
        this.http('joinGame', no, callback);
    };

    /**
     * 离开游戏
     */
    WebSocketClient.prototype.leaveGame = function (callback) {
        this.http('leaveGame', null, callback);
    };
})();
