package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chanwook
 */
public class Part3TransformTest {

    ReactiveRepository<User> repository = new ReactiveUserRepository();

    @Test
    public void transformMono() throws Exception {
        final Mono<User> mono = repository.findFirst();

        // transform to upper case text
        final Mono<User> transformed =
                mono.map(u -> new User(u.getUsername().toUpperCase(), u.getFirstname().toUpperCase(), u.getLastname().toUpperCase()));

        TestSubscriber.subscribe(transformed).await()
                .assertValues(new User("SWHITE", "SKYLER", "WHITE")).assertComplete();
    }

    @Test
    public void transformFlux() throws Exception {
        final Flux<User> flux = repository.findAll();

        // transform to upper case text
        final Flux<User> transformed =
                flux.map(u -> new User(u.getUsername().toUpperCase(), u.getFirstname().toUpperCase(), u.getLastname().toUpperCase()));

        TestSubscriber.subscribe(transformed).await()
                .assertValues(
                        new User("SWHITE", "SKYLER", "WHITE"),
                        new User("JPINKMAN", "JESSE", "PINKMAN"),
                        new User("WWHITE", "WALTER", "WHITE"),
                        new User("SGOODMAN", "SAUL", "GOODMAN"))
                .assertComplete();
    }

    @Test
    public void asyncTransform() throws Exception {
        final Flux<User> flux = repository.findAll();
        final Flux<User> transformed = flux.flatMap(u -> Mono.just(new User(u.getUsername().toUpperCase(), u.getFirstname().toUpperCase(), u.getLastname().toUpperCase())));

        TestSubscriber.subscribe(transformed).await()
                .assertValues(
                        new User("SWHITE", "SKYLER", "WHITE"),
                        new User("JPINKMAN", "JESSE", "PINKMAN"),
                        new User("WWHITE", "WALTER", "WHITE"),
                        new User("SGOODMAN", "SAUL", "GOODMAN"))
                .assertComplete();
    }
}
