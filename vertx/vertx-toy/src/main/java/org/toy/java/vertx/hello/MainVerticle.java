package org.toy.java.vertx.hello;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  public static void main(final String[] args) {
    Launcher.executeCommand("run", MainVerticle.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer()
      .requestHandler(
        req -> req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!"))
      .listen(8888, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          log.info("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
