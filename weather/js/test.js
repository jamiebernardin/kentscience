$('#mainView').foo();

$('#mainView').bindStr('{{a}}+{{b}}', {a:3,b:4});

var foo = {a: 'whatever',
           b: [1,2,3,4,5],
           c: [{name:'a',value:0},{name:'b',value:2}],
           d: {name: 'bar', value: 'baz'}
};


$('#mainView').bindTpl('tpl/test2.html', foo);

var url = "http://api.openweathermap.org/data/2.5/forecast?city=19839&callback=?";

$.getJSON( url, function( data ) {
    console.log(JSON.stringify(data.list[0],null, 4));
    delete data.list;
    console.log(JSON.stringify(data,null, 4));
 //   $('#mainView').html(bindTpl('tpl/forcast.html', data));
});

