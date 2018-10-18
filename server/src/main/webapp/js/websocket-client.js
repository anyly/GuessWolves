/**
 * Created by idear on 2018/9/21.
 */
(function () {

    var httpPrefix = 'http_';

    var original = window['WebSocket']
    ////pc
    function pcWebSocket(ws) {
        var websocket = null;
        var httplistener = {};
        var messagelistener = {};
        this.readyState = 0;

        if (original) {
            websocket = new original(ws);
        } else {
            alert('当前浏览器 Not support service');
            return null;
        }

        function isSupport() {
            return websocket;
        }


        window.onbeforeunload = function () {
            websocket.close();
        };

        /**
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
                console.error('admit error:"' + message + '"\n' + e);
            }
        };


        /**
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
         * @param {number} [code]
         * @param {string} [reason]
         */
        this.disconnect = function (code, reason) {
            if (!isSupport()) {
                return this;
            }
            this.readyState = 2;
            if (code) {
                if (reason) {
                    websocket.close(code, reason);
                } else {
                    websocket.close(code);
                }
            } else {
                websocket.close();
            }
            return this;
        };

        /**
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
                callback(code, reason , wasClean);
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
            websocket.onerror = callback;
            return this;
        };

        this.reconnect = function () {
            if (!isSupport()) {
                return this;
            }
            if (this.readyState >= 3) {
                if (original) {
                    websocket = new original(ws);
                } else {
                    alert('当前浏览器 Not support service');
                    return null;
                }
            }
            return this;
        };

        return this;
    }
    ////mobile
    window.__WEBSOCKET_CALLBACK = {};
    function mobileWebSocket(ws) {

        var httplistener = {};
        var messagelistener = {};
        var id = WebSocketBridge.create();
        if (!id) {
            alert('当前浏览器 Not support service');
        }

        function isSupport() {
            return id;
        }

        window.onbeforeunload = function () {
            WebSocketBridge.disconnect(id);
        };

        window.__WEBSOCKET_CALLBACK[id] = {};
        window.__WEBSOCKET_CALLBACK[id].message = function (event) {
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
                console.error('admit error:"' + message + '"\n' + e);
            }
        };
        WebSocketBridge.onmessage(id, 'window.__WEBSOCKET_CALLBACK['+id+'].message');

        /**
         * {function({data: (String|Blob|ArrayBuffer)})}
         **/
        this.http = function (action, param, callback) {
            if (!isSupport()) {
                return this;
            }
            var newAction = httpPrefix+action;
            var data = {
                action: newAction,
                data : param
            };

            WebSocketBridge.send(id, JSON.stringify(data));
            if (callback) {
                httplistener[newAction] = callback;
            }
            return this;

        };

        /**
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
            WebSocketBridge.send(id, JSON.stringify(data));
            return this;
        };

        /**
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
         * {function(Event)}
         **/
        this.onConnect = function (callback) {
            if (!isSupport()) {
                return this;
            }
            window.__WEBSOCKET_CALLBACK[id].onopen = callback;
            WebSocketBridge.onopen(id, 'window.__WEBSOCKET_CALLBACK['+id+'].onopen');
            return this;
        };

        /**
         * @param {number} [code]
         * @param {string} [reason]
         */
        this.disconnect = function (code, reason) {
            if (!isSupport()) {
                return this;
            }
            WebSocketBridge.disconnect(id, code, reason);
            return this;
        };

        /**
         * {function({code: Number, reason: String, wasClean: Boolean})}
         **/
        this.onDisconnect = function (callback) {
            if (!isSupport()) {
                return this;
            }
            window.__WEBSOCKET_CALLBACK[id].onclose = function (code, reason , wasClean) {
                if (code == 600) {
                    return;
                }
                callback(code, reason , wasClean);
                httplistener = {};
                messagelistener = {};
            };
            WebSocketBridge.onclose(id, 'window.__WEBSOCKET_CALLBACK['+id+'].onclose');
            return this;
        };

        /**
         * {function(Event)}
         **/
        this.onError = function (callback) {
            if (!isSupport()) {
                return this;
            }
            window.__WEBSOCKET_CALLBACK[id].onerror = callback;
            WebSocketBridge.onerror(id, 'window.__WEBSOCKET_CALLBACK['+id+'].onerror');
            return this;
        };

        this.reconnect = function () {
            if (!isSupport()) {
                return this;
            }
            //WebSocketBridge.connect(id, ws);
            window.location.href=window.location.href;
            if (!id) {
                alert('当前浏览器 Not support service');
            }
            return this;
        };
        WebSocketBridge.connect(id, ws);
        return this;
    }

    window.WebSocket = function (ws) {
        if (window['WebSocketBridge']) {
            return mobileWebSocket(ws);
        } else {
            return pcWebSocket(ws);
        }
    };
})();
