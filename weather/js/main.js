window.addEventListener('load', function() {
    Ctrl.init();
});

var Ctrl = function() {
    // private methods
    var result =  {
        init: function() {
            $("#selectLocale").bindTpl('tpl/locale.html', loadLocations() );
            this.cityChange();
        },
        cityChange: function () {
            var handler = function(data) {
                $('#mainView').bindTpl('tpl/current.html', data);
            };
            getCity($("#citySelect")[0].value, handler);
        },
        find: function() {
            var handler = function(data) {
                if (data.list) {
                    $('#mainView').bindTpl('tpl/searchResult.html', data.list[0]);
                } else {
                    $('#mainView').error('No weather station in that proximity.')
                }
            }
            getSearchResults($("#query")[0].value, handler)
        },
        addLocale: function (info) {
           var f = $("#citySelect").
        }
    };
    function getSearchResults(query, handler) {
        var url = "http://m.openweathermap.org/data/2.1/find/name?callback=?";
        $.getJSON( url, {q:query, type:"like", sort:"population"}, function(data) {
            console.log(JSON.stringify(data,null, 4));
            handler(data);
        });
    };
    function getCity(code, handler) {
        var url = "http://api.openweathermap.org/data/2.5/weather?callback=?";
        $.getJSON( url, {units:"imperial", id:code}, function(data) {
            console.log(JSON.stringify(data,null, 4));
            handler(data);
        });
    };
    function loadLocations() {
        var res = {list:[{name:'New York, NY', code:5128581},
            {name:'New Milford, Ct', code:4839435}]
        };
        var locs = localStorage.getItem('weather.locations');
        if  (locs) {
            res = JSON.parse(locs);
        }
        return res;
    };
    function storeLocations(locs) {
        localStorage.setItem('weather.locations', JSON.stringify(locs));
    };

    return result;
}();