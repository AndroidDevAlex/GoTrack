package com.example.map.domain

import javax.inject.Inject

class LogOutUseCase @Inject constructor(
   private val mapRepository: MapRepository
) {

   suspend fun deleteData() {
      mapRepository.logOut()
   }
}