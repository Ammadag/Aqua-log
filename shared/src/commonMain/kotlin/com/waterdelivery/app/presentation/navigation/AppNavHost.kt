package com.waterdelivery.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waterdelivery.app.presentation.add_customer.AddCustomerScreen
import com.waterdelivery.app.presentation.add_customer.AddCustomerViewModel
import com.waterdelivery.app.presentation.add_delivery.AddDeliveryScreen
import com.waterdelivery.app.presentation.add_delivery.AddDeliveryViewModel
import com.waterdelivery.app.presentation.contact_picker.ContactPickerScreen
import com.waterdelivery.app.presentation.contact_picker.ContactPickerViewModel
import com.waterdelivery.app.presentation.customer_detail.CustomerDetailScreen
import com.waterdelivery.app.presentation.customer_detail.CustomerDetailViewModel
import com.waterdelivery.app.presentation.customers.CustomersScreen
import com.waterdelivery.app.presentation.customers.CustomersViewModel
import com.waterdelivery.app.presentation.dashboard.DashboardScreen
import com.waterdelivery.app.presentation.dashboard.DashboardViewModel
import com.waterdelivery.app.presentation.invoice_preview.InvoicePreviewScreen
import com.waterdelivery.app.presentation.invoice_preview.InvoicePreviewViewModel
import com.waterdelivery.app.presentation.invoices.InvoicesScreen
import com.waterdelivery.app.presentation.invoices.InvoicesViewModel
import com.waterdelivery.app.presentation.settings.SettingsScreen
import com.waterdelivery.app.presentation.settings.SettingsViewModel
import com.waterdelivery.app.presentation.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topLevelRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Customers.route,
        Screen.Invoices.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute in topLevelRoutes) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = koinViewModel()
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToAddDelivery = { navController.navigate(Screen.AddDelivery.createRoute()) },
                    onNavigateToCustomers = { navController.navigate(Screen.Customers.route) },
                    onNavigateToInvoices = { navController.navigate(Screen.Invoices.route) },
                    onCustomerClick = { id -> navController.navigate(Screen.CustomerDetail.createRoute(id)) }
                )
            }

            composable(Screen.Customers.route) {
                val viewModel: CustomersViewModel = koinViewModel()
                CustomersScreen(
                    viewModel = viewModel,
                    onNavigateToAddCustomer = { navController.navigate(Screen.AddCustomer.route) },
                    onCustomerClick = { id -> navController.navigate(Screen.CustomerDetail.createRoute(id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
                val viewModel: CustomerDetailViewModel = koinViewModel(parameters = { parametersOf(customerId) })
                CustomerDetailScreen(
                    viewModel = viewModel,
                    onNavigateToAddDelivery = { id -> navController.navigate(Screen.AddDelivery.createRoute(id)) },
                    onNavigateToGenerateInvoice = { id -> navController.navigate(Screen.InvoicePreview.createRoute(id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.AddCustomer.route) {
                val viewModel: AddCustomerViewModel = koinViewModel()
                
                // Get result from contact picker if available
                val contactName = navController.currentBackStackEntry
                    ?.savedStateHandle?.get<String>("contactName")
                val contactPhone = navController.currentBackStackEntry
                    ?.savedStateHandle?.get<String>("contactPhone")
                
                if (contactName != null && contactPhone != null) {
                    viewModel.prefillFromContact(contactName, contactPhone)
                    navController.currentBackStackEntry?.savedStateHandle?.remove<String>("contactName")
                    navController.currentBackStackEntry?.savedStateHandle?.remove<String>("contactPhone")
                }

                AddCustomerScreen(
                    viewModel = viewModel,
                    onNavigateToContactPicker = { navController.navigate(Screen.ContactPicker.route) },
                    onSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.ContactPicker.route) {
                val viewModel: ContactPickerViewModel = koinViewModel()
                ContactPickerScreen(
                    viewModel = viewModel,
                    onContactSelected = { contact ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("contactName", contact.name)
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("contactPhone", contact.phoneNumber)
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AddDelivery.route,
                arguments = listOf(navArgument("customerId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId")
                val viewModel: AddDeliveryViewModel = koinViewModel(parameters = { parametersOf(customerId) })
                AddDeliveryScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onDeliverySaved = { navController.popBackStack() }
                )
            }

            composable(Screen.Invoices.route) {
                val viewModel: InvoicesViewModel = koinViewModel()
                InvoicesScreen(
                    viewModel = viewModel,
                    onInvoiceClick = { id -> navController.navigate(Screen.InvoicePreview.createRoute(id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.InvoicePreview.route,
                arguments = listOf(navArgument("invoiceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
                val viewModel: InvoicePreviewViewModel = koinViewModel(parameters = { parametersOf(invoiceId) })
                InvoicePreviewScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = koinViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
