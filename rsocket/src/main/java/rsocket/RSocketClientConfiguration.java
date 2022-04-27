package rsocket;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class RSocketClientConfiguration {

    @Bean
    public ApplicationRunner sender(RSocketRequester.Builder requestBuilder) {
        return args -> {

            RSocketRequester tcp = requestBuilder.tcp("localhost", 7000);

            /* request - response */
            String who = "Grieg";
            tcp
                    .route("greeting/{name}", who)
                    .data("Hello RSocket!")
                    .retrieveMono(String.class)
                    .subscribe(response -> log.info("Got response: {}", response));

            /* request - stream */
            String stockSymbol = "TEST";
            tcp
                    .route("stock/{symbol}", stockSymbol)
                    .retrieveFlux(StockQuote.class)
                    .doOnNext(StockQuote -> log.info("Price of {} : {} at {}",
                            StockQuote.getSymbol(),
                            StockQuote.getPrice(),
                            StockQuote.getTimestamp()))
                    .subscribe();

            /* fire and forget */
            tcp
                    .route("alert")
                    .data(new Alert(Alert.Level.RED, "Frog", Instant.now()))
                    .send()
                    .subscribe();
            log.info("Alert sent!");

            /* channel */
            Flux<GratuityIn> gratuityInFlux = Flux.fromArray(new GratuityIn[] {
                    new GratuityIn(BigDecimal.valueOf(35.50), 18),
                    new GratuityIn(BigDecimal.valueOf(75.40), 50),
                    new GratuityIn(BigDecimal.valueOf(27.30), 4),
                    new GratuityIn(BigDecimal.valueOf(99.70), 16),
                    new GratuityIn(BigDecimal.valueOf(69.99), 25),
            }).delayElements(Duration.ofSeconds(1));
            tcp
                    .route("gratuity")
                    .data(gratuityInFlux)
                    .retrieveFlux(GratuityOut.class)
                    .subscribe(out -> log.info(out.getPercent() + "% gratuity on "
                            + out.getBillTotal() + " is "
                            + out.getGratuity()));
        };
    }

}
