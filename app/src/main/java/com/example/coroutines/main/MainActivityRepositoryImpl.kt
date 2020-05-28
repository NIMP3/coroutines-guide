package com.example.coroutines.main

import com.example.coroutines.extensions.logCoroutine
import com.example.coroutines.main.data.Dog
import com.example.coroutines.main.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MainActivityRepositoryImpl @Inject constructor(private val api: MainActivityApi) :
    MainActivityRepository {

//    override suspend fun getListOfDogs(): Result<List<Dog>> = withContext(Dispatchers.IO) {
//        val list = mutableListOf<Dog>()
//
//        val dogBreedListDeferred = async { api.getBreedsListAsync().execute() }
//        val dogBreedListResponse = dogBreedListDeferred.await()
//
//        if (dogBreedListResponse.isSuccessful) {
//            val dogList = dogBreedListResponse.body()?.message?.keys?.toList()
//            dogList?.forEach {
//                val dogImageDeferred = async { it.let { api.getImageByUrlAsync(it).execute() } }
//                val dogImage = dogImageDeferred.await()
//                if (dogImage.isSuccessful) {
//                    dogImage?.let { imageResponse ->
//                        list.add(Dog(it, imageResponse?.body()?.message))
//                    }
//                }
//            }
//        }
//        Result(list, null)
//    }

    override suspend fun getTopTwoDogsAsync(): Result<List<Dog>> = withContext(Dispatchers.IO) {
        //We need to move to background thread, "Dispatchers.IO" in this case as Network requests must always operate on
        // background thread.
        val list = mutableListOf<Dog>()
        logCoroutine("getTopTwoDogsAsync", coroutineContext)

        //The async{} builder immediately spawns the Coroutine inside the blocks.
        val dogBreedListDeferred = async { api.getBreedsListAsync().execute() }
        //The .await() pauses the function until the deferred val returns a result.
        val dogBreedListResponse = dogBreedListDeferred.await()

        //Selecting two dog breeds by Random
        val dogBreedOneName = dogBreedListResponse.body()?.message?.keys?.toList()?.random()
        val dogBreedTwoName = dogBreedListResponse.body()?.message?.keys?.toList()?.random()

        //Spawning two Coroutines by using async{} again.
        val dogBreedOneImageDeferred = async {
            logCoroutine("dogBreedOneImageDeferred", coroutineContext)
            dogBreedOneName?.let { api.getImageByUrlAsync(it).execute() }
        }
        val dogBreedTwoImageDeferred = async {
            logCoroutine("dogBreedTwoImageDeferred", coroutineContext)
            dogBreedTwoName?.let { api.getImageByUrlAsync(it).execute() }
        }


        //Await for both the started coroutines above by using await on the deferred val .
        val dogBreedOne = dogBreedOneImageDeferred.await()
        val dogBreedTwo = dogBreedTwoImageDeferred.await()

        if (dogBreedTwo?.isSuccessful!!) list.add(Dog(dogBreedTwoName, dogBreedTwo.body()?.message))
        if (dogBreedOne?.isSuccessful!!) list.add(Dog(dogBreedOneName, dogBreedOne.body()?.message))
        Result(list, null)
    }


    override suspend fun getListOfDogs(): Result<List<Dog>> {
        //No need to change context to Dispatchers.IO as Retrofit handles that automatically.
        val list = mutableListOf<Dog>()
        //Remove async{} and .await()
        val dogBreedList = api.getBreedsList().message.keys.toList()
        //This function is paused until the above api returns results.
        dogBreedList.forEach {
            val dogImage = api.getImageByUrl(it).message
            list.add(Dog(it, dogImage))
        }
        return Result(list, null)
    }
}