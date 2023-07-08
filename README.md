# Ktor + Koin + MongoDB backend template

Run docker compose

To build the app run `./gradlew build`

To run the app run `ENV=local ./gradlew run` This will use the `local.yaml` file which contains defaults for local
development.

# Usage

Imagine, you want to create a simple endpoint for you resource because you like cats.
Most of the time, you want to fetch facts from your database.


Sometimes, you want to access external API for the fact, _for inspiration of course ;)_ 

_Note: The OpenAPI paths are auto generated based on registered paths_
![Swagger UI](./img/swagger-ui.png?raw=true "Optional Title")

Start with defining the `Model` data class:

```kotlin
data class CatFact(
    @BsonId override val id: ObjectId? = null, //@BsonId is needed
    val fact: String,
) : Model

// To force implement id
interface Model {
    val id: ObjectId?
}
```

Next create repository

```kotlin
@Singleton // Koin annotation to be able to be auto-wired
class CatFactRepository(
    mongo: Mongo // autowire Mongo (holding connection) into repository 
) : MongoCrudRepository<CatFact>(
    // extend CRUD repo with pre-made functions + provide our Model 
    mongo = mongo,
    databaseName = "ktor-sample", // name of Mongo database
) {
    // Get collection from ktor-sample database. Every operation will work on this collection
    override fun MongoDatabase.selectRepositoryCollection() = getCollection<CatFact>("cat-facts")

    // you add you own specific queries here
    suspend fun mySpecialFunction() = withCollection {
        // we are already in scope of MongoCollection<CatFact> here, so it's easy to work with
    }
}
```

After that, move to the next layer, service. In service, you can combine multiple repositories, 
or even other dependencies

In this case we will combine repository access with client that fetches random fact from `https://catfact.ninja`

```kotlin

@Singleton
class CatFactService(
    private val catFactClient: CatFactClient,
    private val catFactRepository: CatFactRepository, // our previously created repository
) : ModelService<CatFact>(catFactRepository) { // we get basic functions from abstract repository

    //... and then we can define more
    suspend fun getFactFromApi(): CatFact = catFactClient.getCatFact()

    //... or create specific functions for our use case and using catFactRepository directly 
    suspend fun deleteWhereCatFactMatching(fact: String) = catFactRepository.deleteWhere {
        Filters.eq(CatFact::fact.name, fact)
    }
}
```

And as a last step, create controller:

```kotlin

@Singleton(binds = [Controller::class]) // has to bind Controller::class in order to auto-create routes
class CatFactController(
    private val catFactService: CatFactService, // we will be using previously created cat sercive
    private val useBearerAuth: Boolean // if true, Bearer token auth is turned on automatically. 
    // If false no auth is provided. You can add your auth in additionalRoutesForRegistration()
) : RestController<
        CatFact, // MODEL -> The Model that we are using 
        SaveCatFactRequestBodyListItem, // REQUEST_DTO -> Class that will be treated as incoming JSON
        CatFactResponseDto // RESPONSE_DTO -> Our controller will respond with this class
        >
    (
    basePath = "api/v1", // the base path that all of our routes will have
    // if false, you would need to add routes directly to additionalRoutesForRegistration()
    // use false when you want to publish only read operations for example
    autoRegisterRoutes = true, 
    service = catFactService,
) {
        
        
    override fun Route.additionalRoutesForRegistration() {
        // only routes in here will get registered
        getFactFromApi() // if you don't add the route here specifically, it will NOT be registered!
    }

    override fun getNameOfModelForRestPath() = "cat-fact" //the common path after base path

    // we have to define types specifically as we are working with generics
    // haven't found better way yet :(
    override fun requestDtoTypeInfo() = typeInfo<SaveCatFactRequestBodyListItem>()
    override fun listRequestTypeInfo() = typeInfo<ListWrapperDto<SaveCatFactRequestBodyListItem>>()

    override fun responseDtoTypeInfo() = typeInfo<CatFactResponseDto>()
    override fun listResponseDtoTypeInfo() = typeInfo<ListWrapperDto<CatFactResponseDto>>()

    // provide mapping functions between REQUEST_DTO -> MODEL-> RESPONSE_DTO
    // Note: If you don't want to use different classes, just define all as CatFact
    override fun CatFact.toResponseDto() = toDto()
    override fun SaveCatFactRequestBodyListItem.requestToModel() = toModel()

    // ... and create more routes! Always extend Route. 
    private fun Route.getFactFromApi() = get("/${getNameOfModelForRestPath()}/api", {
        // In this block, specify the OpenAPI specs
        response { HttpStatusCode.OK to { body(responseDtoTypeInfo().type) } }
    }) {
        //respond with cat fact from API (not db this time)
        call.respond(catFactService.getFactFromApi().toResponseDto())
    }
    
}
```

And that's it! Run application and you can enjoy your endpoints!

Also don't forget that it's possible to add any number of endpoints to this controller as with the `getFactFromApi()`
which is not interacting with MongoDB at all dor example.

# Why this stack?

**Ktor** was chosen as it's written in Kotlin and supports coroutines natively

**Koin** as DI _(Dependency Injection)_ was chosen as it's comfortable to use for development
especially with the addition of `koin-annotations`. Just mark class as `@Singleton` and it's ready to be used.

**MongoDB** provides great Kotlin integration utilizing coroutines and `Flow`. Seamless integration with `data` objects.

# What's inside?

Example how to create basic REST API very quickly, without the need to care about database at all

# What's next?

~~PostgresSQL will be added into the stack.~~ Replaced with Mongo

Examples of how to use coroutines / channels / flows.

Testability of API / Services. Working with mocks.

# What if I want to help / suggest changes?

Hey that's super welcome!

Either open PR or reach me on my email `krason.tomas@gmail.com` regarding any ides.

# Package structure

**application** -> contains code for running Koin, Server modules etc.

**client** -> contains clients that extract or load data from/to different servers. **Client** functions SHOULD return *
*model**.

**controller** -> contains code specifying what endpoints will be available on this server. **Controller** SHOULD NOT
return **model**.

**model** -> internal representation of objects. **Model** SHOULD NOT be `@Serializable`

**service** -> contains business logic

**repository** -> access to DB layers