package com.michel.todoapp.di

import android.content.Context
import dagger.Module
import com.google.gson.Gson
import com.michel.core.data.di.NetworkModule
import com.michel.network.backend.TodoItemsApi
import com.michel.network.service.TodoItemsServiceImpl
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [NetworkModule::class]
//)
//object NetworkTestModule {
//
//    @Provides
//    @Singleton
//    fun providesTodoItemsService(
//        @ApplicationContext context: Context,
//        todoItemsApi: TodoItemsApi,
//    ): TodoItemsServiceImpl {
//        return TodoItemsServiceImpl(
//            context = context,
//            api = todoItemsApi
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun providesTodoItemsApi(): TodoItemsApi = Retrofit.Builder()
//        .baseUrl("http://localhost/")
//        .addConverterFactory(GsonConverterFactory.create(Gson()))
//        .build()
//        .create(TodoItemsApi::class.java)
//
//}