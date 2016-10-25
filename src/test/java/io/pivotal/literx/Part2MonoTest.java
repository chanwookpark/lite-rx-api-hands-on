package io.pivotal.literx;

import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Mono;

/**
 * @author chanwook
 */
public class Part2MonoTest {

    @Test
    public void empty() throws Exception {
        final Mono<String> empty = Mono.empty();
        TestSubscriber.subscribe(empty).assertValueCount(0).assertComplete();
    }

    @Test
    public void value() throws Exception {
        final Mono<String> mono = Mono.just("foo");
        TestSubscriber.subscribe(mono).assertValueCount(1).assertValues("foo").assertComplete();
    }

    @Test
    public void error() throws Exception {
        final Mono<Object> error = Mono.error(new IllegalStateException());
        TestSubscriber.subscribe(error).assertError(IllegalStateException.class).assertNotComplete();
    }
}
