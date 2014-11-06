var vertx = require('vertx');
var eb = require('vertx/event_bus');
var console = require('vertx/console');

eb.registerHandler('cleaned_message', function(message) {
    var value = vertx.getMap('filter').get('value');

    if (value === null) {
        value = '*';
    }

    if (value == '*' || message.indexOf(value) != -1) {
        console.log('Filtered message: ' + message);
        eb.publish('filtered_message', message);
    }
});
