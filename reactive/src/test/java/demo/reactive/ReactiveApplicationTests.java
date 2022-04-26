package demo.reactive;

import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@SpringBootTest
class ReactiveApplicationTests {

	@Test
	public void createAFlux_fromArray() {
		String[] fruits = new String[] {
				"Apple", "Orange", "Grape", "Banana", "Strawberry" };

		Flux<String> fruitFlux = Flux.fromArray(fruits);

		StepVerifier.create(fruitFlux)
				.expectNext("Apple")
				.expectNext("Orange")
				.expectNext("Grape")
				.expectNext("Banana")
				.expectNext("Strawberry")
				.verifyComplete();
	}

	@Test
	public void createAFlux_formStream() {
		Stream<String> fruiStream = Stream.of("Apple", "Orange", "Grape", "Banana", "Strawberry");

		Flux<String> fruitFlux = Flux.fromStream(fruiStream);

		StepVerifier.create(fruitFlux)
				.expectNext("Apple")
				.expectNext("Orange")
				.expectNext("Grape")
				.expectNext("Banana")
				.expectNext("Strawberry")
				.verifyComplete();
	}

	@Test
	public void createAFlux_range() {
		Flux<Integer> intervalFlux = Flux.range(1, 5);

		StepVerifier.create(intervalFlux)
				.expectNext(1)
				.expectNext(2)
				.expectNext(3)
				.expectNext(4)
				.expectNext(5)
				.verifyComplete();
	}

	@Test
	public void createAFlux_interval() {
		Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1)).take(5);

		StepVerifier.create(intervalFlux)
				.expectNext(0L)
				.expectNext(1L)
				.expectNext(2L)
				.expectNext(3L)
				.expectNext(4L)
				.verifyComplete();
	}

	@Test
	public void mergeFluxes() {
		Flux<String> charactherFlux = Flux
				.just("Garfield", "Kojak", "Barbossa")
				.delayElements(Duration.ofMillis(500));
		Flux<String> foodFlux = Flux
				.just("Lasagna", "Lollipops", "Apples")
				.delaySubscription(Duration.ofMillis(250))
				.delayElements(Duration.ofMillis(500));

		Flux<String> mergeFlux = charactherFlux.mergeWith(foodFlux);

		StepVerifier.create(mergeFlux)
				.expectNext("Garfield")
				.expectNext("Lasagna")
				.expectNext("Kojak")
				.expectNext("Lollipops")
				.expectNext("Barbossa")
				.expectNext("Apples")
				.verifyComplete();
	}

	@Test
	public void zipFluxes() {
		Flux<String> charactherFlux = Flux.just("Garfield", "Kojak", "Barbossa");
		Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

		Flux<Tuple2<String, String>> zippedFlux = Flux.zip(charactherFlux, foodFlux);

		StepVerifier.create(zippedFlux)
				.expectNextMatches(p -> p.getT1().equals("Garfield") && p.getT2().equals("Lasagna"))
				.expectNextMatches(p -> p.getT1().equals("Kojak") && p.getT2().equals("Lollipops"))
				.expectNextMatches(p -> p.getT1().equals("Barbossa") && p.getT2().equals("Apples"))
				.verifyComplete();
	}

	@Test
	public void zipFluxesToObjects() {
		Flux<String> charactherFlux = Flux.just("Garfield", "Kojak", "Barbossa");
		Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

		Flux<String> zippedFlux = Flux.zip(charactherFlux, foodFlux, (c, f) -> c + " eats " + f);

		StepVerifier.create(zippedFlux)
					.expectNext("Garfield eats Lasagna")
					.expectNext("Kojak eats Lollipops")
					.expectNext("Barbossa eats Apples")
					.verifyComplete();
	}

	@Test
	public void skipAFew() {
		Flux<String> skipFlux = Flux
				.just("one", "skip", "skip", "three hundred and one", "three hundred and two")
				.skip(3);

		StepVerifier.create(skipFlux)
					.expectNext("three hundred and one")
					.expectNext("three hundred and two")
					.verifyComplete();

	}

}
