package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.BlockingRepository;
import io.pivotal.literx.repository.BlockingUserRepository;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Iterator;

import static io.pivotal.literx.domain.User.*;

/**
 * @author chanwook
 */
public class Part9BlockingToReactiveTest {

    @Test
    public void slowPublisherFastSubscriber() throws Exception {

        final BlockingRepository<User> blockingRepository = new BlockingUserRepository();

        final Flux<User> flux = Flux.defer(() -> Flux.fromIterable(blockingRepository.findAll()))
                .subscribeOn(Schedulers.elastic());

        TestSubscriber.subscribe(flux).assertNotTerminated().await()
                .assertValues(SKYLER, JESSE, WALTER, SAUL)
                .assertComplete();
    }

    @Test
    public void fastPublisherSlowSubscriber() throws Exception {

        BlockingRepository<User> blockingRepository = new BlockingUserRepository(new User[]{});
        ReactiveRepository<User> reactiveRepository = new ReactiveUserRepository();

        final Flux<User> flux = reactiveRepository.findAll();
        //여기서 모니인 이유는? 리턴 타입이 User가 아니라 Void다. 완료 상태를 나타내는 듯...
        final Mono<Void> complete = flux.publishOn(Schedulers.parallel()).doOnNext(user -> blockingRepository.save(user)).then();

        TestSubscriber.subscribe(complete).assertNotTerminated().await().assertComplete();

        final Iterator<User> iterator = blockingRepository.findAll().iterator();
        assert SKYLER.equals(iterator.next());
        assert JESSE.equals(iterator.next());
        assert WALTER.equals(iterator.next());
        assert SAUL.equals(iterator.next());
    }

    @Test
    public void nullHandling() throws Exception {
        Mono<User> mono = Mono.justOrEmpty(SKYLER);
        TestSubscriber.subscribe(mono).assertValues(SKYLER).assertComplete();

        mono = Mono.justOrEmpty(null);
        TestSubscriber.subscribe(mono).assertNoValues().assertComplete();
    }
}
