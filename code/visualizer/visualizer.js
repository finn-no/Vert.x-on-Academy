var vertx = require('vertx')

var server = vertx.createHttpServer()

// Serve the static resources
server.requestHandler(function(req) {
    if (req.uri() == "/client.js") req.response.sendFile("visualizer/target/client.js");
    if (req.uri() == "/") req.response.sendFile("visualizer/resources/index.html");
    else req.response.sendFile("visualizer/resources" + req.uri())
})

// Create a SockJS bridge which lets everything through (be careful!)
vertx.createSockJSServer(server).bridge({prefix: "/eventbus"}, [{}], [{}]);

server.listen(8080);

vertx.setPeriodic(500, function(timerID) {
    vertx.eventBus.publish('filtered_message', 'test message');
});


