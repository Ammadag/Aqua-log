package com.waterdelivery.app.di

import com.waterdelivery.app.domain.usecase.AddCustomerUseCase
import com.waterdelivery.app.domain.usecase.GenerateInvoiceUseCase
import com.waterdelivery.app.domain.usecase.GetCustomerSummaryUseCase
import com.waterdelivery.app.domain.usecase.GetDeliveryHistoryUseCase
import com.waterdelivery.app.domain.usecase.GetDashboardStatsUseCase
import com.waterdelivery.app.domain.usecase.RecordDeliveryUseCase
import com.waterdelivery.app.domain.usecase.SearchCustomersUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { RecordDeliveryUseCase(get(), get()) }
    factory { GetCustomerSummaryUseCase(get(), get()) }
    factory { GetDeliveryHistoryUseCase(get()) }
    factory { GenerateInvoiceUseCase(get(), get(), get()) }
    factory { GetDashboardStatsUseCase(get(), get()) }
    factory { AddCustomerUseCase(get()) }
    factory { SearchCustomersUseCase(get()) }
}
