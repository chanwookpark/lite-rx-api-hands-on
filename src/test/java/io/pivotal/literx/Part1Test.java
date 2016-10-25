package io.pivotal.literx;

import io.pivotal.literx.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Flux;

/**
 * @author chanwook
 */
public class Part1Test {

    @Test
    public void emptyFlux() throws Exception {
        final Flux<Object> emptyFlux = Flux.empty();
        TestSubscriber.subscribe(emptyFlux).assertValueCount(0).assertComplete();
    }

    @Test
    public void fromValues() throws Exception {
        final Flux<String> values = Flux.just("foo", "bar");
        TestSubscriber.subscribe(values).assertValueCount(2).assertComplete();
    }

    @Test
    public void fromArray() throws Exception {
        final Flux<String> values = Flux.fromArray(new String[]{"foo", "far", "soo"});
        TestSubscriber.subscribe(values).assertValueCount(3).assertComplete();
    }

    @Test
    public void error() throws Exception {
        final Flux<Object> error = Flux.error(new IllegalStateException());
        TestSubscriber.subscribe(error).assertError(IllegalStateException.class).assertNotComplete();
    }
}
