package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
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
public class Part4MergeTest {

    ReactiveRepository<User> userRepository1 = new ReactiveUserRepository(500);
    ReactiveRepository<User> userRepository2 = new ReactiveUserRepository(MARIE, MIKE);

    //TODO 여기서 interLeave의 의미는?? -> Flux.merge()와 Flux.concatWith() 비교
    @Test
    public void mergeWithInterleave() throws Exception {

        final Flux<User> flux1 = userRepository1.findAll();
        final Flux<User> flux2 = userRepository2.findAll();

        final Flux<User> merged = Flux.merge(flux1, flux2);

        // assertValues 할때 순서가 다르면 에러남. 어디서 순서를 보자하는 건가?
        TestSubscriber.subscribe(merged).await()
                .assertValues(MARIE, MIKE, SKYLER, JESSE, WALTER, SAUL)
                .assertComplete();
    }

    @Test
    public void mergeWithoutInterleave() throws Exception {

        final Flux<User> flux1 = userRepository1.findAll();
        final Flux<User> flux2 = userRepository2.findAll();

        final Flux<User> merged = flux1.concatWith(flux2);

        TestSubscriber.subscribe(merged).await()
                .assertValues(SKYLER, JESSE, WALTER, SAUL, MARIE, MIKE)
                .assertComplete();
    }

    @Test
    public void multipleMonoToFlux() throws Exception {
        final Mono<User> skyler = userRepository1.findFirst();
        final Mono<User> marie = userRepository2.findFirst();

        final Flux<User> flux = Flux.concat(skyler, marie);

        TestSubscriber.subscribe(flux).await()
                .assertValues(SKYLER, MARIE).assertComplete();
    }
}
