package com.michel.core.data.repository

import com.michel.core.data.models.TodoItem

interface WorkerRepository {

    // Синхронизирует данные на сервере и устройстве с возвращением результата
    suspend fun synchronize(): Result<Unit>

}