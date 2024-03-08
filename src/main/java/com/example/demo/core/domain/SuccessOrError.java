package com.example.demo.core.domain;

import io.vavr.control.Either;

import java.util.function.Function;

public class SuccessOrError<S, E> {
    private final Either<E, S> either;

    private SuccessOrError(Either<E, S> either) {
        this.either = either;
    }

    public static <S, E> SuccessOrError<S, E> success(S successValue) {
        return new SuccessOrError<>(Either.right(successValue));
    }

    public static <S, E> SuccessOrError<S, E> error(E error) {
        return new SuccessOrError<>(Either.left(error));
    }

    public <R> R ifSuccessOr(Function<S, R> toApplyIfSuccess, Function<E, R> toApplyIfError) {
        return either.fold(toApplyIfError, toApplyIfSuccess);
    }
}
