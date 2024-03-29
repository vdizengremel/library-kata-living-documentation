package com.example.demo.core.domain.member;

import com.example.living.documentation.annotation.CoreConcept;

/**
 * Error that can prevent a member to borrow a book.
 */
@CoreConcept
public enum BorrowingError {
    MEMBER_IS_BANNED,
    HAS_LATE_BORROWING,
    HAS_REACHED_MAX_AUTHORIZED_BORROWING
}
