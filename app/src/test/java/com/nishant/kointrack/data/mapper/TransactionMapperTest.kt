package com.nishant.kointrack.data.mapper

import com.nishant.kointrack.data.local.TransactionEntity
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionMapperTest {

    @Test
    fun `TransactionEntity toDomain maps all fields correctly`() {
        val entity = TransactionEntity(
            id = "tx123",
            title = "Groceries",
            amount = 45.5,
            currency = "EUR",
            convertedAmountEUR = 45.5,
            category = "FOOD",
            type = "EXPENSE",
            timestamp = 1700000000000L,
            note = "Supermarket"
        )

        val domain = entity.toDomain()

        assertEquals("tx123", domain.id)
        assertEquals("Groceries", domain.title)
        assertEquals(45.5, domain.amount, 0.001)
        assertEquals("EUR", domain.currency)
        assertEquals(45.5, domain.convertedAmountEUR, 0.001)
        assertEquals(TransactionCategory.FOOD, domain.category)
        assertEquals(TransactionType.EXPENSE, domain.type)
        assertEquals(1700000000000L, domain.timestamp)
        assertEquals("Supermarket", domain.note)
    }

    @Test
    fun `Transaction toEntity maps all fields correctly`() {
        val domain = Transaction(
            id = "tx456",
            title = "Salary",
            amount = 3000.0,
            currency = "EUR",
            convertedAmountEUR = 3000.0,
            category = TransactionCategory.INCOME,
            type = TransactionType.INCOME,
            timestamp = 1700000000000L,
            note = "Monthly"
        )

        val entity = domain.toEntity()

        assertEquals("tx456", entity.id)
        assertEquals("Salary", entity.title)
        assertEquals(3000.0, entity.amount, 0.001)
        assertEquals("INCOME", entity.category)
        assertEquals("INCOME", entity.type)
    }
}
