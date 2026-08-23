package com.waterdelivery.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val appModule = listOf(
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
    platformModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
