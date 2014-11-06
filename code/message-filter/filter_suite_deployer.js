var container = require('vertx/container');
var console = require('vertx/console');

function deployVerticle(name) {
    container.deployVerticle(name, function (err, deploy_id) {
        if (!err) {
            console.log('The ' + name + ' has been deployed with id = ' + deploy_id);
        } else {
            console.log('Deployment failed: ' + err.getMessage());
        }
    });
}

deployVerticle('message-filter/message_filter.js');
deployVerticle('message-filter/filter_retriever.js');
