package com.waterdelivery.app.di

import com.waterdelivery.app.data.repository.BusinessProfileRepositoryImpl
import com.waterdelivery.app.data.repository.CustomerRepositoryImpl
import com.waterdelivery.app.data.repository.DeliveryRepositoryImpl
import com.waterdelivery.app.data.repository.InvoiceRepositoryImpl
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.repository.DeliveryRepository
import com.waterdelivery.app.domain.repository.InvoiceRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl(get()) }
    single<DeliveryRepository> { DeliveryRepositoryImpl(get()) }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get()) }
    single<BusinessProfileRepository> { BusinessProfileRepositoryImpl(get()) }
}
