/**
 * Created by idear on 2018/9/22.
 */
/**
 * 获得表单的数据
 */

(function () {
    window.formData = function (formid) {
        var data = {};
        var jq_form = $('#'+formid);
        //
        function setData(name, value) {
            var old = data[name];
            if (old) {
                if ($.isArray(old)) {
                    old.push(value);
                } else {
                    data[name] = [old, value];
                }
            } else {
                data[name] = value;
            }
        }
        //select
        jq_form.find('select[name!=""]').each(function () {
            var jq_select = $(this);
            var select_name, select_value;
            select_name = jq_select.attr('name');
            jq_select.find('option:checked').each(function () {
                select_value = this.value;
            });
            if (select_value) {
                setData(select_name, select_value);
            }
        });
        //textarea
        jq_form.find('textarea[name!=""]').each(function () {
            var jq_textarea = $(this);
            setData(jq_textarea.attr('name'), jq_textarea.html());
        });
        //radio
        jq_form.find('input[type=radio][name!=""]:checked').each(function () {
            var jq_radio = $(this);
            setData(jq_radio.attr('name'),jq_radio.val());
        });
        //checkbox
        jq_form.find('input[type=checkbox][name!=""]:checked').each(function () {
            var jq_checkbox = $(this);
            var checkbox_name = jq_checkbox.attr('name');
            var valueArray = data[checkbox_name];
            if (valueArray) {
                if ($.isArray(valueArray)) {
                    valueArray.push(jq_checkbox.val());
                } else {
                    data[checkbox_name] = [valueArray, jq_checkbox.val()];
                }
            } else {
                data[checkbox_name] = [jq_checkbox.val()];
            }

        });
        //input
        jq_form.find('input[type!=radio][type!=checkbox][name!=""]').each(function () {
            var jq_input = $(this);
            setData(jq_input.attr('name'), jq_input.val());
        });

        return data;
    };

    window.widgetValue = function (widgetName) {
        var widgetValue;
        //
        function setData(value) {
            if (widgetValue) {
                if (widgetValue.constructor == 'Array') {
                    widgetValue.push(value);
                } else {
                    widgetValue = [widgetValue, value];
                }
            } else {
                widgetValue = value;
            }
        }
        $('[name='+widgetName+']').each(function () {
            var tagName = this.tagName;
            var jq = $(this);
            if (tagName == 'select') {
                jq.find('option:checked').each(function () {
                    setData(this.value);
                })
            } else if (tagName == 'textarea') {
                setData(jq.html());
            } else if (tagName == 'input') {
                if (jq.attr('type') == 'radio' && jq.pop('checked') == true) {
                    setData(jq.val());
                } else if (jq.attr('type') == 'checkbox' && jq.pop('checked') == true) {
                    if (widgetValue) {
                        if (widgetValue.constructor == 'Array') {
                            widgetValue.push(value);
                        } else {
                            widgetValue = [widgetValue, value];
                        }
                    } else {
                        widgetValue = value;
                    }
                    checkboxs.push(jq.val());
                } else {// input text
                    setData(jq.val());
                }
            }
        });
        if (widgetValue) {
            return widgetValue;
        }
        return undefined;
    };
}).call(window);

/**
 * 切换页面
 */
(function () {
    window.showHideAnimation = function (selector, hasClass, showAnimation, hideAnimation, time) {
        time = time | 0;
        $(selector).each(function (index) {
            var q_item = $(this);
            if (q_item.hasClass(hasClass)) {
                q_item[showAnimation](time);
            } else {
                q_item[hideAnimation](time);
            }
        });
    };

    window.showPageAnimation = function (hasClass, showAnimation, hideAnimation, time) {
        showHideAnimation('.page', hasClass, showAnimation, hideAnimation, time);
    };

    window.showPage = function (hasClass, time) {
        showPageAnimation(hasClass, 'show', 'hide', time);
    };

    window.fadePage = function (hasClass, time) {
        showPageAnimation(hasClass, 'fadeIn', 'fadeOut', time);
    };
})();

/**
 * mobile支持
 */
(function () {
    /**
     * 点击事件
     */
    $.fn.tap = function (callback) {
        if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
            // 手机端
            this.on("touchstart", callback);
        } else {
            // pc端
            this.click(callback);
        }
        return this;
    };
})();
