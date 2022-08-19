package org.toy.java.rsocket.server;

import io.rsocket.core.RSocketServer;
import io.rsocket.ipc.IPCRSocket;
import io.rsocket.ipc.RequestHandlingRSocket;
import io.rsocket.ipc.Server;
import io.rsocket.ipc.decoders.CompositeMetadataDecoder;
import io.rsocket.ipc.marshallers.Primitives;
import io.rsocket.ipc.marshallers.Strings;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RSocket IPC Server
 *
 * @author aaronchenwei
 *
 */
@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        RequestHandlingRSocket requestHandler = new RequestHandlingRSocket(new CompositeMetadataDecoder());

        AtomicBoolean ff = new AtomicBoolean();

        IPCRSocket service =
            Server.service("HelloService")
                .noMeterRegistry()
                .noTracer()
                .marshall(Strings.marshaller())
                .unmarshall(Strings.unmarshaller())
                .requestResponse(
                    "hello",
                    (s, byteBuf) -> Mono.just("Hello -> " + s)
                )
                .requestResponse(
                    "goodbye",
                    (s, byteBuf) -> Mono.just("Goodbye -> " + s)
                )
                .requestResponse(
                    "count",
                    Primitives.intMarshaller(),
                    (charSequence, byteBuf) -> Mono.just(charSequence.length()))
                .requestResponse(
                    "increment",
                    Primitives.intUnmarshaller(),
                    Primitives.intMarshaller(),
                    (integer, byteBuf) -> Mono.just(integer + 1))
                .requestStream(
                    "helloStream",
                    (s, byteBuf) -> Flux.range(1, 10).map(i -> i + " - Hello -> " + s))
                .requestStream(
                    "toString",
                    Primitives.longUnmarshaller(),
                    (aLong, byteBuf) -> Flux.just(String.valueOf(aLong)))
                .fireAndForget(
                    "ff",
                    (s, byteBuf) -> {
                        ff.set(true);
                        return Mono.empty();
                    })
                .requestChannel(
                    "helloChannel",
                    (s, publisher, byteBuf) -> Flux.just("Hello -> " + s))
                .toIPCRSocket();

        requestHandler.withEndpoint(service);

        log.info("Creating TcpServer...");
        TcpServer tcpServer = TcpServer
            .create()
            .host("localhost")
            .port(7000);

        // warmup rsocket server
        tcpServer.warmup().block();

        log.info("Creating RSocketServer...");
        CloseableChannel closeableChannel = RSocketServer
            .create()
            .acceptor((setup, sendingSocket) -> Mono.just(requestHandler))
            .bind(TcpServerTransport.create(tcpServer))
            .block();

        log.info("Finish initialization...");
        assert closeableChannel != null;
        closeableChannel.onClose().block();
        closeableChannel.dispose();
    }
}
