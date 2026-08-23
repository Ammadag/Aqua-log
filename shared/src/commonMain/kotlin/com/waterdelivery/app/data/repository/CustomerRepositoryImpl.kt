package com.waterdelivery.app.data.repository

import com.waterdelivery.app.data.local.dao.CustomerDao
import com.waterdelivery.app.data.local.mapper.toDomain
import com.waterdelivery.app.data.local.mapper.toEntity
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomerRepositoryImpl(
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun getAllCustomers(): Flow<List<Customer>> {
        return customerDao.getAllActiveCustomers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCustomerById(id: String): Customer? {
        return customerDao.getCustomerById(id)?.toDomain()
    }

    override fun searchCustomers(query: String): Flow<List<Customer>> {
        return customerDao.searchCustomers(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addCustomer(customer: Customer) {
        customerDao.insertCustomer(customer.toEntity())
    }

    override suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer.toEntity())
    }
}
