# Ktor + Koin backend template

To build the app run `./gradlew build`

To run the app run `ENV=local ./gradlew run` This will use the `local.yaml` file which contains defaults for local development.

# Why this stack?

**Ktor** was chosen as it's written in Kotlin and supports coroutines natively

**Koin** as DI _(Dependency Injection)_ was chosen as it's comfortable to use for development
especially with the addition of `koin-annotations`. Just mark class as `@Singleton` and it's ready to be used.

# What's inside?

Couple of examples how to create basic REST API.

# What's next?

PostgresSQL will be added into the stack.

Examples of how to use coroutines / channels / flows.

Testability of API / Services. Working with mocks.


# What if I want to help / suggest changes?

Hey that's super welcome! 

Either open PR or reach me on my email `krason.tomas@gmail.com` regarding any ides.

# Package structure

**application** -> contains code for running Koin, Server modules etc.

**client** -> contains clients that extract or load data from/to different servers. **Client** functions SHOULD return **model**.

**controller** -> contains code specifying what endpoints will be available on this server. **Controller** SHOULD NOT return **model**.

**model** -> internal representation of objects. **Model** SHOULD NOT be `@Serializable`

**service** -> contains business logic

**repository** -> access to DB layers