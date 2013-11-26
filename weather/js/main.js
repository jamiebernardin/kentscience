window.addEventListener('load', function() {
    Ctrl.init();
});

var Ctrl = function() {
    var result =  {
        init: function(locs) {
            if (locs == undefined) {
                locs = loadLocations();
            }
            $("#selectLocale").bindTpl('tpl/locale.html', locs );
            $("#citySelect").val(locs.current);
            this.cityChange();

        },
        cityChange: function () {
            getWeather($("#citySelect").val(), function(data) {
                $('#mainView').bindTpl('tpl/current.html', data);
            });
            var locs = loadLocations();
            locs.current = $("#citySelect").val();
            storeLocations(locs);
            getForecast($("#citySelect")[0].value, function(data) {
                 $('#forecastView').bindTpl('tpl/forecast.html', data);
           });
        },
        find: function() {
            if ($("#query")[0].value.length > 2) {
                searchCity($("#query")[0].value, function(data) {
                    console.log(JSON.stringify(data, null, 4));
                    if (data.list) {
                        $('#mainView').bindTpl('tpl/searchResult.html', data.list[0]);
                    } else {
                        $('#mainView').error('No weather station in that proximity.')
                    }
                })
            }
        },
        addLocale: function (code, name) {
            var locs = loadLocations();
            locs.list.push({name:name, code:code});
            locs.current = code;
            storeLocations(locs);
            $("#selectLocale").bindTpl('tpl/locale.html', locs );
            $("#citySelect").val(locs.current);
            $("#query").val("");
            this.cityChange();
        }
    };
    function searchCity(query, handler) {
        var url = "http://api.openweathermap.org/data/2.5/find?callback=?";
        $.getJSON( url, {q:query, type:"like", sort:"population", units:"imperial"}, function(data) {
         //   console.log(JSON.stringify(data,null, 4));
            handler(data);
        });
    };
    function getWeather(code, handler) {
        var url = "http://api.openweathermap.org/data/2.5/weather?callback=?";
        $.getJSON( url, {units:"imperial", id:code}, function(data) {
            for (var i = 0; i <data.weather.length; i++) {
                data.weather[i].icon = "http://openweathermap.org/img/w/" + data.weather[i].icon + ".png"
            }
            data.dt = new Date(data.dt*1000).toUTCString()
            if (data.rain == undefined) {
                data.rain = {'3h':0};
            } else {
                data.rain.rate = data.rain['3h']/3
            }
        //    console.log(JSON.stringify(data,null, 4));
            handler(data);
        });
    };
    function getForecast(code, handler) {

        var url = "http://api.openweathermap.org/data/2.5/forecast/daily?callback=?", date;
        $.getJSON( url, {units:"imperial", id:code}, function(data) {
            for (var i = 0; i <data.list.length; i++) {
                data.list[i].weather[0].icon = "http://openweathermap.org/img/w/" + data.list[i].weather[0].icon + ".png"
                date = new Date(data.list[i].dt*1000);
                data.list[i].day = days[date.getDay()];
                data.list[i].month = months[date.getMonth()];
                data.list[i].date = date.getDate();
            }
            console.log(JSON.stringify(data,null, 4));
            handler(data);
        });
    };
    function loadLocations() {
        var res = {current:4839435, list:[{name:'New York', code:5128581},
            {name:'New Milford', code:4839435}]
        };
        var locs = localStorage.getItem('weather.locations');
        if  (locs) {
            res = JSON.parse(locs);
        }  else {
           storeLocations(res);
        }
        return res;
    };
    function storeLocations(locs) {
        localStorage.setItem('weather.locations', JSON.stringify(locs));
    };
    var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return result;
}();