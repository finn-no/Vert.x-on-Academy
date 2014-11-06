var vertx = require('vertx');
var eb = require('vertx/event_bus');
var console = require('vertx/console');

eb.registerHandler('filter', function(message) {
    var filter = vertx.getMap('filter');
    filter.put('value', message);
    console.log('Received filter value: ' + message);
});

eb.publish('filter', '*');
