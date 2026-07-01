package com.example.atomchallenge.feature.countries.data.repository

import com.example.atomchallenge.feature.countries.data.local.dao.CountryDao
import com.example.atomchallenge.feature.countries.data.mapper.toDomain
import com.example.atomchallenge.feature.countries.data.mapper.toEntity
import com.example.atomchallenge.feature.countries.data.remote.api.CountryApiService
import com.example.atomchallenge.feature.countries.domain.model.Country
import com.example.atomchallenge.feature.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val apiService: CountryApiService,
    private val countryDao: CountryDao
) : CountryRepository {

    // 👇 LEER — siempre, siempre, siempre desde Room
    // Nunca llama a apiService aquí. La UI se suscribe a esto
    // y se actualiza sola cada vez que la tabla cambie (ver PASO 17)
    override fun getCountries(): Flow<List<Country>> {
        return countryDao.getAllCountries()
            .map { entities -> entities.map { it.toDomain() } }
        // 👆 dos niveles de map:
        // el de afuera transforma el Flow (cada emisión)
        // el de adentro transforma cada CountryEntity → Country
    }

    // 👇 ESCRIBIR — la única función que toca la red
    override suspend fun refreshCountries() {
        // 1. Pide datos frescos a la API
        val remoteCountries = apiService.getCountries()

        // 2. Los convierte a Entity (no a Domain — van directo a Room)
        val entities = remoteCountries.map { it.toEntity() }

        // 3. Los guarda — REPLACE sobreescribe lo viejo (PASO 17)
        countryDao.insertAll(entities)

        // 4. No retorna nada. getCountries() (el Flow) se entera
        //    solo, porque Room notifica el cambio automáticamente
    }

    // 👇 Detalle de un país — primero intenta Room, si no está, pide a la red
    override suspend fun getCountryByName(name: String): Country? {
        val local = countryDao.getCountryByName(name)

        if (local != null) {
            return local.toDomain()
        }

        // 👇 Fallback — esto cubre el caso de que el usuario llegue
        // a un país que aún no se haya sincronizado en Room
        // (poco probable en este flujo, pero es la versión robusta)
        val remote = apiService.getCountryByName(name).firstOrNull()
        return remote?.let {
            val entity = it.toEntity()
            countryDao.insertAll(listOf(entity))
            entity.toDomain()
        }
    }
}