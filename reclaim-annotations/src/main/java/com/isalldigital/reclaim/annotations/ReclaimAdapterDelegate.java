package com.isalldigital.reclaim.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;


@Target(ElementType.TYPE)
@Retention(SOURCE)
public @interface ReclaimAdapterDelegate {

    /**
     * the data model that will trigger the usage of annotaded adapter delegate.
     * Data models must be one to one (but delagates can be one to many).
     *
     * Type must be of type Class and implement DisplayableCell interface.
     */
    Class<?> value();
}
