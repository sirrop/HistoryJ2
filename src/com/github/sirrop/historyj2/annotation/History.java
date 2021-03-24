package com.github.sirrop.historyj2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class History {
    private History() {}

    /**
     * updateObjectメソッドにつけるアノテーションです。
     * 名前が間違っているとき、コンパイルエラーを発生させます。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Update {
    }
}
