package com.nilsson.sentiment.irc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class TwitchIrc {
    public Flux<String> streamFromChannel(String channel, String username, String oauthToken) {
        Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();
        Thread thread = new Thread(() -> {
            // TODO: Extract config
            String ircServer = "irc.chat.twitch.tv";
            int ircPort = 6697;

            log.debug("Connecting to Twitch chat server on port={} and server={}", ircPort, ircServer);
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(ircServer, ircPort)) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                log.debug("Connecting to channel with user={}, channel={}, oathToken={}",
                        username,
                        channel,
                        oauthToken);
                writer.write("PASS " + "oauth:" + oauthToken + "\r\n");
                writer.write("NICK " + username + "\r\n");
                writer.flush();
                writer.write("JOIN #" + channel + "\r\n");
                writer.flush();

                String line;
                long startTime = System.currentTimeMillis();
                while ((line = reader.readLine()) != null) {
                    log.debug("Read line: {}", line);

                    // Respond to PING (keep-alive check) with PONG
                    // (Twitch will periodically send you something like "PING :tmi.twitch.tv")
                    if (line.startsWith("PING")) {
                        log.debug("Received PING message. Will respond. Message={}", line);
                        String pingReply = line.replace("PING", "PONG");
                        log.debug("PONG response. Message={}", pingReply);
                        writer.write(pingReply + "\r\n");
                        writer.flush();
                        continue;
                    }
                    sink.tryEmitNext(secondsSinceStart(startTime) + line);

                }
                log.debug("Done reading irc channel");
                sink.tryEmitComplete();

            } catch (Exception e) {
                log.error("Exception occurred while streaming IRC", e);
                sink.tryEmitError(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return sink.asFlux();
    }

    private String secondsSinceStart(long startTime) {
        long millisSinceStart = System.currentTimeMillis() - startTime;
        return Long.toString(millisSinceStart / 1000);
    }
}

