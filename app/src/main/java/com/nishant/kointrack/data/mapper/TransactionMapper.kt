package com.nishant.kointrack.data.mapper

import com.nishant.kointrack.data.local.TransactionEntity
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        title = title,
        amount = amount,
        currency = currency,
        convertedAmountEUR = convertedAmountEUR,
        category = runCatching { TransactionCategory.valueOf(category) }.getOrDefault(TransactionCategory.OTHER),
        type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
        timestamp = timestamp,
        note = note
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        amount = amount,
        currency = currency,
        convertedAmountEUR = convertedAmountEUR,
        category = category.name,
        type = type.name,
        timestamp = timestamp,
        note = note
    )
}
