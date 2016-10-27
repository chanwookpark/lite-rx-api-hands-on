package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;

import static io.pivotal.literx.domain.User.*;

/**
 * @author chanwook
 */
public class Part7ReactiveToBlockingTest {

    ReactiveRepository repository = new ReactiveUserRepository();

    @Test
    public void mono() throws Exception {
        final Mono<User> mono = repository.findFirst();

        User user = mono.block();

        assert SKYLER.equals(user);
    }

    @Test
    public void flux() throws Exception {
        final Flux flux = repository.findAll();

        final Iterator iterator = flux.toIterable().iterator();

        assert SKYLER.equals(iterator.next());
        assert JESSE.equals(iterator.next());
        assert WALTER.equals(iterator.next());
        assert SAUL.equals(iterator.next());
    }
}
