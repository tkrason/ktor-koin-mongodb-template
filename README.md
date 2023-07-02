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

Examples of how to use coroutines / channels / flows .

Testability of API / Services. Working with mocks.


# What if I want to help / suggest changes?

Hey that's super welcome! 

Either open PR or reach me on my email `krason.tomas@gmail.com` regarding any ides.