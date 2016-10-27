package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveUserRepository;
import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.pivotal.literx.Users.MARIE;
import static io.pivotal.literx.Users.MIKE;
import static io.pivotal.literx.domain.User.*;

/**
 * @author chanwook
 */
public class Part6OtherOperationTest {

    @Test
    public void zipFirstNameAndLastName() throws Exception {

        final Flux<String> userNameFlux = Flux.just(JESSE.getUsername(), SAUL.getUsername(), SKYLER.getUsername(), WALTER.getUsername());
        final Flux<String> firstNameFlux = Flux.just(JESSE.getFirstname(), SAUL.getFirstname(), SKYLER.getFirstname(), WALTER.getFirstname());
        final Flux<String> lastNameFlux = Flux.just(JESSE.getLastname(), SAUL.getLastname(), SKYLER.getLastname(), WALTER.getLastname());

        final Flux<User> zipFlux =
                Flux.zip(userNameFlux, firstNameFlux, lastNameFlux).map(tuple3 -> new User(tuple3.getT1(), tuple3.getT2(), tuple3.getT3()));

        TestSubscriber
                .subscribe(zipFlux)
                .assertValues(JESSE, SAUL, SKYLER, WALTER);
    }

    @Test
    public void fastestMono() throws Exception {
        // MARIE Win!
        Mono<User> user1 = new ReactiveUserRepository(MARIE).findFirst();
        Mono<User> user2 = new ReactiveUserRepository(250, MIKE).findFirst();

        Mono<User> first = Mono.first(user1, user2);

        TestSubscriber.subscribe(first).await().assertValues(MARIE).assertComplete();

        // MIKE Win!
        user1 = new ReactiveUserRepository(250, MARIE).findFirst();
        user2 = new ReactiveUserRepository(MIKE).findFirst();

        first = Mono.first(user1, user2);

        TestSubscriber.subscribe(first).await().assertValues(MIKE).assertComplete();
    }

    @Test
    public void fastestFlux() throws Exception {
        Flux<User> users1 = new ReactiveUserRepository(MARIE, MIKE).findAll();
        Flux<User> users2 = new ReactiveUserRepository(250).findAll();

        Flux<User> flux = Flux.firstEmitting(users1, users2);

        TestSubscriber.subscribe(flux).await().assertValues(MARIE, MIKE).assertComplete();

        users1 = new ReactiveUserRepository(250, MARIE, MIKE).findAll();
        users2 = new ReactiveUserRepository().findAll();

        flux = Flux.firstEmitting(users1, users2);

        TestSubscriber.subscribe(flux).await().assertValues(SKYLER, JESSE, WALTER, SAUL).assertComplete();
    }

    //TODO end는 왜 end인가?
    @Test
    public void end() throws Exception {
        final Mono<Void> mono = new ReactiveUserRepository().findAll().then();

        TestSubscriber.subscribe(mono).assertNotTerminated().await().assertNoValues().assertComplete();
    }

    @Test
    public void monoWithValueInsteadOfError() throws Exception {
        final Mono<Object> error = Mono.error(new IllegalStateException());

        Mono<Object> otherwise = error.otherwise(e -> Mono.just(SAUL));
        TestSubscriber.subscribe(otherwise).assertValues(SAUL).assertComplete();

        otherwise = error.otherwise(e -> Mono.just(SKYLER));
        TestSubscriber.subscribe(otherwise).assertValues(SKYLER).assertComplete();
    }

    @Test
    public void fluxWithValueInsteadOfError() throws Exception {
        final Flux<User> error = Flux.error(new IllegalStateException());

        Flux<User> flux = error.onErrorResumeWith(e -> Flux.just(SAUL, SKYLER));
        TestSubscriber.subscribe(flux).assertValues(SAUL, SKYLER).assertComplete();

        flux = error.onErrorResumeWith(e -> Flux.just(SKYLER, WALTER));
        TestSubscriber.subscribe(flux).assertValues(SKYLER, WALTER).assertComplete();
    }
}
