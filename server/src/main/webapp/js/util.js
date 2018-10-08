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
    var i = location.pathname.lastIndexOf('/');
    window.location.basepath = location.host+'/'+location.pathname.substring(0, i);

})();

/**
 * 断线提醒
 */
(function () {
    window.connectFail = function (callback) {
        window.connectSuccess();
        var jq = $(
            '<blackout>' +
            '<content><svg t="1538984890396" class="icon" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5714" data-spm-anchor-id="a313x.7781069.0.i9" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200"><defs><style type="text/css"></style></defs><path d="M615 465.9l-110.3-67.7-26.1 42.6 110.3 67.7c11.3 6.9 19.2 17.9 22.4 31 3.1 13.1 1 26.5-5.9 37.8l-182 296.6c-6.9 11.3-17.9 19.2-31 22.4-13.1 3.1-26.5 1-37.8-5.9l-182.4-112c-11.3-6.9-19.2-17.9-22.4-31-3.1-13.1-1-26.5 5.9-37.8l116.5-189.9-42.6-26.1-116.5 189.9C84.3 730.4 99.2 792.3 146 821l182.5 112c46.9 28.8 108.8 13.9 137.5-32.9l182-296.6c28.7-47 13.9-108.9-33-137.6z m261.2 31.4l-121.3 42.5-14.5-47.9 119.3-41.8c12.5-4.4 22.6-13.5 28.4-25.6 5.8-12.1 6.6-25.7 2.2-38.2l-70.8-201.9c-4.4-12.5-13.5-22.6-25.6-28.4-12.1-5.8-25.7-6.6-38.2-2.2L427.3 268.9c-12.5 4.4-22.6 13.5-28.4 25.6-5.8 12.1-6.6 25.7-2.2 38.2l24.2 69-47.2 16.5-24.2-69c-18.2-51.9 9.4-109.3 61.3-127.5l328.4-115.1c51.9-18.2 109.3 9.4 127.5 61.3l70.8 201.9c18.1 52-9.4 109.3-61.3 127.5zM284.1 256l-47.2 16.4-40-115.4 47.2-16.4 40 115.4z m-43.3 100.9l-5.3 49.7-121.3-13 5.3-49.7 121.3 13z" fill="#ffffff" p-id="5715"></path></svg> 网络中断，点击重连</content>' +
            '<svg t="1538982812185" class="button" style="" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1929" xmlns:xlink="http://www.w3.org/1999/xlink" width="200" height="200"><defs><style type="text/css"></style></defs><path d="M688 766.272a301.376 301.376 0 0 1-204.8 54.656c-3.392-0.32-6.72-0.896-10.176-1.344-6.528-0.896-13.056-1.728-19.392-3.008a220.672 220.672 0 0 1-11.648-2.688 261.376 261.376 0 0 1-18.496-4.8c-2.88-0.896-5.76-1.92-8.704-2.944a298.176 298.176 0 0 1-25.28-9.728 292.48 292.48 0 0 1-23.36-11.456l-1.024-0.512a311.744 311.744 0 0 1-89.152-74.432c-1.216-1.472-2.368-3.136-3.584-4.672a311.168 311.168 0 0 1-67.264-193.472h71.168a5.12 5.12 0 0 0 4.544-2.752 4.992 4.992 0 0 0-0.256-5.248L161.088 322.24a5.12 5.12 0 0 0-8.64 0L32.896 503.872c-1.024 1.472-1.216 3.52-0.256 5.248s2.688 2.752 4.544 2.752h71.168c0 86.016 26.496 165.696 71.424 231.552 0.576 0.96 0.96 1.92 1.536 2.816 4.672 6.72 9.856 12.992 14.848 19.392 1.856 2.368 3.648 4.864 5.568 7.232 7.36 9.088 15.168 17.472 23.232 25.856l2.24 2.304a397.888 397.888 0 0 0 122.752 84.8l7.424 3.328c8.512 3.584 17.344 6.72 26.112 9.728 4.16 1.408 8.256 2.88 12.48 4.16 7.744 2.304 15.616 4.224 23.552 6.144 5.312 1.28 10.496 2.624 15.936 3.648 2.176 0.512 4.288 1.152 6.528 1.472 7.488 1.344 14.976 2.112 22.528 3.008l8.064 1.152a397.12 397.12 0 0 0 271.104-71.936 49.408 49.408 0 0 0 11.904-68.224 48.064 48.064 0 0 0-67.584-12.032m227.776-254.4a409.856 409.856 0 0 0-71.104-231.04c-0.704-1.152-1.152-2.304-1.792-3.264a428.928 428.928 0 0 0-17.664-23.04l-2.112-2.752a399.68 399.68 0 0 0-150.656-114.624 161.28 161.28 0 0 1-4.8-2.24 502.976 502.976 0 0 0-28.352-10.56c-3.648-1.152-7.04-2.432-10.624-3.456a377.856 377.856 0 0 0-25.344-6.656c-4.672-1.088-9.408-2.304-14.208-3.264-2.368-0.448-4.544-1.152-6.912-1.6-6.4-1.152-12.8-1.6-19.2-2.432-4.352-0.576-8.768-1.28-13.184-1.728a413.568 413.568 0 0 0-32-1.6c-1.92 0-3.84-0.32-5.824-0.32a397.312 397.312 0 0 0-231.488 73.92 49.408 49.408 0 0 0-11.968 68.288 48.128 48.128 0 0 0 67.584 11.968 302.464 302.464 0 0 1 205.12-54.656l8.128 1.152c7.36 0.832 14.592 1.92 21.696 3.328 3.136 0.576 6.272 1.408 9.344 2.112 6.976 1.6 14.016 3.328 20.8 5.376l6.4 2.24c7.808 2.56 15.424 5.312 22.976 8.512l2.368 1.088c45.12 19.648 84.736 49.92 115.648 87.808l0.576 0.768a311.04 311.04 0 0 1 69.696 196.736h-71.104a5.12 5.12 0 0 0-4.288 8l119.616 181.568a5.12 5.12 0 0 0 8.576 0l119.488-181.568a5.12 5.12 0 1 0-4.288-8h-71.104v-0.064z m0 0" p-id="1930" fill="#ffffff"></path></svg> style="width:80px;height:80px;" />' +
            '</blackout>');
        jq.appendTo(document.body);
        jq.find('.button').click(function () {
            if (callback) callback();
            return false;
        });
    };
    window.connectSuccess = function () {
        $('blackout').remove();
    };
})();
