package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.adapter.RxJava1Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.Single;

import java.util.concurrent.CompletableFuture;

import static io.pivotal.literx.domain.User.*;

/**
 * @author chanwook
 */
//TODO 왜 전환이 필요한건가?? 정말 순수히 타입 변환하려고??
public class Part8ConversionTest {

    ReactiveRepository<User> repository = new ReactiveUserRepository();

    @Test
    public void observableConversion() throws Exception {
        final Flux<User> all = repository.findAll();

        // Flux to RxJava Observable
        final Observable<User> observable = RxJava1Adapter.publisherToObservable(all);

        // RxJava Observable to Flux
        TestSubscriber.subscribe(RxJava1Adapter.observableToFlux(observable)).await()
                .assertValues(SKYLER, JESSE, WALTER, SAUL)
                .assertComplete();
    }

    @Test
    public void singleConversion() throws Exception {
        final Mono<User> mono = repository.findFirst();
        final Single<User> single = RxJava1Adapter.publisherToSingle(mono);

        TestSubscriber.subscribe(RxJava1Adapter.singleToMono(single)).await()
                .assertValues(SKYLER).assertComplete();
    }

    @Test
    public void completableFutureConversion() throws Exception {
        final Mono<User> mono = repository.findFirst();

        final CompletableFuture<User> future = mono.toFuture();

        TestSubscriber.subscribe(Mono.fromFuture(future)).await()
                .assertValues(SKYLER);

    }
}
