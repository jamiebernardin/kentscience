;(function($){
    $.extend($.fn, {
        foo: function(){
            return this.html('bar')
        }
    });

    $.extend($.fn, {
        bindStr: function(str, model){
            return this.html(resolve(str, model));
        }
    });

    $.extend($.fn, {
        bindTpl: function(template, model){
        var that = this;
            $.ajax({
                type: "GET",
                async: false,
                url:  template,
                success: function(data, status, xhr) {
                    that.html(resolve(data, model));
                },
                error: function(xhr, errorType, error) {
                    console.log(error);
                }
            });
            return this;
        }
    });

    $.extend($.fn, {
        error: function(message){
            return this.html('<div class="alert-box alert">'+message+'</div>');
        }
    });

    var resolve = function(text, model) {
        var fbrak, bbrak,   // indexOf '{', '}'
            enclosure, symbol, words, num,  // '{model.foo}' 'model.foo'
            num, i, lines;
        lines = text.split("\n");
        lines.forEach(function(line) {
            if (line.indexOf('repeat') > -1) {
                text = text.replace(line, expandArrays(model, line.replace('repeat','')));
            }
        });
        console.log(text);
        num = text.split('}').length - 1
        for (i = 0; i < num; i++) {
            fbrak = text.indexOf('{');
            bbrak = text.indexOf('}');
            enclosure = text.substring(fbrak, bbrak+1);
            symbol = text.substring(fbrak+1, bbrak);
            words = symbol.split('.');
            text = text.replace(enclosure, extractProperty(model, symbol));
        }
        return text;
    }

    var expandArrays = function(model, line) {
        var arrayWord = line.substring(line.indexOf('{')+1, line.indexOf('[')),
            array = extractProperty(model, arrayWord),
            i = 0,
            newLines = [];
        for (i; i< array.length; i++) {
           newLines.push(line.split('[]').join('.'+i));
        }
        return newLines.join('\n');
    }

    var extractProperty = function (model, symbol) {
        var j, val = model,
            words = symbol.split('.');
        for (j = 1; j< words.length; j++ ) {
            val = val[words[j]];
        }
        return val;
    }

})(Zepto)

