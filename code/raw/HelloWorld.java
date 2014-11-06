package raw;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

import java.util.Map;

public class HelloWorld extends Verticle {

    public void start() {

        vertx.createHttpServer().requestHandler((HttpServerRequest req) -> {

            req.response().end("<html><body><h1>Hello World from Java</h1></body></html>");

        }).listen(8080);
    }
}
