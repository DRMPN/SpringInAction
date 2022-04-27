package rsocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class GreetingController {

    /* request - response */
    @MessageMapping("greeting/{name}")
    public Mono<String> handleGreeting(@DestinationVariable("name") String name, Mono<String> greetingMono) {
        return greetingMono
                .doOnNext(greeting -> log.info("Recieved a greeting from {} : {}", name, greeting))
                .map(greeting -> "Hello to you, too, " + name);
    }

}
