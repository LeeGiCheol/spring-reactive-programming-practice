package me.gicheol.webfluxdemo.publisher;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PubSub_Operator {

    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10L).collect(Collectors.toList()));
//        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//        Publisher<List> mapPub = mapPub(pub, s -> Collections.singletonList(s));
//        Publisher<Integer> sumPub = sumPub(pub);
//        Publisher<Integer> reducePub = reducePub(pub, 0, (a, b) -> a + b);
//        Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a + "-" + b);
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b + ", "));


        reducePub.subscribe(logSub());
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init,
                                                BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    R result = init;

                    @Override
                    public void onNext(T i) {
                        result = bf.apply(result, i);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

//    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                pub.subscribe(new DelegateSub(sub) {
//                    int sum = 0;
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        sum += integer;
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        super.onNext(sum);
//                        super.onComplete();
//                    }
//                });
//            }
//        };
//    }

    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    @Override
                    public void onNext(T integer) {
                        sub.onNext(f.apply(integer));
                    }

                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("onSubscribe()");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T integer) {
                log.debug("onNext:{}", integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("onError:{}", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("onComplate()");
            }
        };
    }

    private static Publisher<Integer> iterPub(final List<Integer> iterable) {
        return new Publisher<Integer>() {
            Iterable<Integer> iter = iterable;

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(sub::onNext);
                            sub.onComplete();

                        } catch (Throwable t) {
                            sub.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }

}
