package com.zhuoli.onecard;


import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {
	private static volatile RxBus defaultInstance;

	private final Subject<Object, Object> bus;

	private RxBus() {
		bus = new SerializedSubject<>(PublishSubject.create());
	}

	public static RxBus getDefault() {
		RxBus rxBus = defaultInstance;
		if (defaultInstance == null) {
			synchronized (RxBus.class) {
				rxBus = defaultInstance;
				if (defaultInstance == null) {
					rxBus = new RxBus();
					defaultInstance = rxBus;
				}
			}
		}
		return rxBus;
	}

	public void post(Object o) {
		bus.onNext(o);
	}

	public <T> Subscription subscribe(Class<T> eventType, Action1<T> onNext) {
		return bus.ofType(eventType).subscribe(onNext);
	}
}