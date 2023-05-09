package org.toy.java.rsocket.client;

import io.netty.buffer.ByteBufAllocator;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.ipc.Client;
import io.rsocket.ipc.encoders.DefaultMetadataEncoder;
import io.rsocket.ipc.marshallers.Primitives;
import io.rsocket.ipc.marshallers.Strings;
import io.rsocket.transport.netty.client.TcpClientTransport;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * RSocket IPC Client
 *
 * @author aaronchenwei
 */
@Slf4j
public class ClientApplication {

  public static void main(String[] args) {
    RSocket rsocket = RSocketConnector
      .connectWith(
        TcpClientTransport
          .create("localhost", 7000)
      )
      .block();

    Objects.requireNonNull(rsocket);

    Client<CharSequence, String> helloService =
      Client.service("HelloService")
        .rsocket(rsocket)
        .customMetadataEncoder(new DefaultMetadataEncoder(ByteBufAllocator.DEFAULT))
        .noMeterRegistry()
        .noTracer()
        .marshall(Strings.marshaller())
        .unmarshall(Strings.unmarshaller());

    String r1 = helloService
      .requestResponse("hello")
      .apply("Alice")
      .block();
    log.info("r1 = {}", r1);

    String r2 = helloService
      .requestResponse("goodbye")
      .apply("Bob")
      .block();
    log.info("r2 = {}", r2);

    String first = helloService
      .requestStream("helloStream")
      .apply("Carol")
      .blockFirst();
    log.info("{}", first);

    helloService
      .fireAndForget("ff")
      .apply("boom")
      .block();

    String r3 = helloService
      .requestChannel("helloChannel")
      .apply(Mono.just("Eve"))
      .blockLast();
    log.info("{}", r3);

    Integer count = helloService
      .requestResponse("count", Primitives.intUnmarshaller())
      .apply("hello")
      .block();
    log.info("count = {}", count);

    long l = System.currentTimeMillis();
    String toString = helloService
      .requestStream("toString", Primitives.longMarshaller())
      .apply(l)
      .blockLast();
    log.info("toString = {}", toString);

    Integer increment =
      helloService
        .requestResponse("increment", Primitives.intMarshaller(), Primitives.intUnmarshaller())
        .apply(1)
        .block();
    log.info("increment = {}", increment);

    rsocket.dispose();
  }
}
