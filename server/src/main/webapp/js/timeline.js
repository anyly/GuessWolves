function Timeline() {
    var self = this;
    var plots = [];
    var needWait = false;
    self.plots = plots;

    self.stop = function() {
        var e = new Error();
        e.name = 'timeline_stop';
        throw new e;
    };
    self.next = function() {
        var plot = plots[0];
        if (!plot) {
            return self;
        }
        //console.debug('name: '+plot.name);
        plot.apply(self);
        // 是否有等待点
        if (needWait) {
            needWait = false;
        } else {
            plots.shift();
            self.next();
        }
    };
    self.then = function (plot) {
        if (typeof(plot) != 'function') {
            throw new ReferenceError('Timeline.then has not function');
        }

        plots.push(plot);

        if (plots.length == 1) {
            self.next();
        }
        return self;
    };
    self.meanwhile = function (fun1, fun2) {
        var count = 0;
        var meanwhiles = arguments;
        var flag = false;

        var timepoint = function (callback) {
            flag = true;
            needWait = true;
            var fun = function () {
                if (callback) {
                    callback.apply(this, arguments);
                }

                if (++count==meanwhiles.length) {
                    plots.shift();
                    self.next();
                }
            };
            return fun;
        };

        var plot = function meanwhile() {
            for (var i=0; i<meanwhiles.length; i++) {
                meanwhiles[i].apply({
                    stop: self.stop,
                    timepoint: timepoint,
                    next: self.next
                });

                if (!flag) {
                    if (++count==meanwhiles.length) {
                        plots.shift();
                        self.next();
                        break;
                    }
                } else {
                    flag = false;
                }

            }
        };
        plots.push(plot);

        if (plots.length == 1) {
            self.next();
        }
        return self;
    };
    self.timepoint = function (callback) {
        needWait = true;
        var fun = function () {
            try {
                if (callback) {
                    callback.apply(self, arguments);
                }
            } catch (e) {
                if (e.name == 'timeline_stop') {

                } else {
                    throw e;
                }
            }
            plots.shift();
            self.next();
        };
        return fun;
    };
}
