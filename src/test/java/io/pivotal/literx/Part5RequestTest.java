package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Flux;

import static io.pivotal.literx.domain.User.*;

/**
 * @author chanwook
 */
public class Part5RequestTest {

    ReactiveRepository<User> userRepository = new ReactiveUserRepository();

    @Test
    public void requestNoValue() throws Exception {

        final Flux<User> flux = userRepository.findAll();

        //flux empty랑 동일한건가??
        TestSubscriber.subscribe(flux, 0).await()
                .assertNoValues();
    }


    @Test
    public void requestValueOneByOne() throws Exception {
        final Flux<User> flux = userRepository.findAll();
        assertAll(flux);
    }

    @Test
    public void experimentWithLog() throws Exception {
        final Flux<User> flux = userRepository.findAll().log();
        assertAll(flux);
    }

    @Test
    public void experimentWithDoOn() throws Exception {
        final Flux<User> flux = userRepository.findAll()
                .doOnSubscribe(s -> System.out.println("Starting :: " + s))
                .doOnNext(u -> System.out.println(u.getFirstname() + " - " + u.getLastname()))
                .doOnComplete(() -> System.out.println("End :: "));

        assertAll(flux);
    }

    private void assertAll(Flux<User> flux) {
        final TestSubscriber<User> subscriber = TestSubscriber.subscribe(flux, 0);

        subscriber.assertValueCount(0);
        assertOneWithNotTerminated(subscriber, SKYLER);
        assertOneWithNotTerminated(subscriber, JESSE);
        assertOneWithNotTerminated(subscriber, WALTER);
        assertOneWithCompleted(subscriber, SAUL);
    }

    private void assertOneWithCompleted(TestSubscriber<User> subscriber, User user) {
        subscriber.request(1);
        subscriber.awaitAndAssertNextValues(user).assertComplete();
    }

    private void assertOneWithNotTerminated(TestSubscriber<User> subscriber, User user) {
        subscriber.request(1);
        subscriber.awaitAndAssertNextValues(user).assertNotTerminated();
    }
}
