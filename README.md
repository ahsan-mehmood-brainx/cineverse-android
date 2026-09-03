# CineVerse

A Media / Movie Explorer app built with Clean Architecture + MVVM, traditional Android Views
(ViewBinding, no Compose), Hilt, Room, Retrofit/Moshi, Navigation Component, and WorkManager.

## Tech stack

- 100% Kotlin, Views + ViewBinding (no Jetpack Compose)
- Clean Architecture (`data` / `domain` / `ui`) + MVVM
- Navigation Component, single Activity (`MainActivity`) with Bottom Navigation + Navigation Drawer
- Dependency Injection: Hilt
- Networking: Retrofit + OkHttp + Moshi
- Local persistence: Room
- Async: Kotlin Coroutines + Flow / StateFlow
- Image loading: Coil
- Background jobs: WorkManager (`worker/MovieSyncWorker`, scheduled daily)
- Min SDK 26, Target SDK 35

## Project structure

```
com.example.cineverse/
├── CineVerseApp.kt          # @HiltAndroidApp Application, WorkManager Configuration.Provider
├── di/                      # Hilt modules (App, Network, Database, Repository)
├── data/                    # remote (Retrofit API/DTOs/interceptors), local (Room), repositories, mappers
├── domain/                  # pure Kotlin models, repository interfaces, use cases
├── ui/                      # one package per feature (home, categories, search, detail, favorites, profile)
├── util/                    # Resource<T>, Constants, extensions, Java interop example
└── worker/                  # WorkManager jobs
```

## TMDB API key setup

This project reads movies from [The Movie Database (TMDB) API](https://www.themoviedb.org/documentation/api).
The key must **never** be committed to source control.

1. Create a free TMDB account and generate an API key/read token at
   https://www.themoviedb.org/settings/api
2. Open `local.properties` at the repo root (already git-ignored) and add:

   ```properties
   TMDB_API_KEY=your_real_api_key_here
   ```

3. That's it — `app/build.gradle.kts` reads `TMDB_API_KEY` from `local.properties` at build
   time and exposes it as `BuildConfig.TMDB_API_KEY`, which `util/Constants.kt` wraps and
   `data/remote/interceptor/ApiKeyInterceptor.kt` attaches to every TMDB request.

If `local.properties` has no `TMDB_API_KEY` entry, the build still succeeds but the field is
an empty string — network calls to TMDB will fail authentication until a real key is added.

### Alternative: a dedicated secrets file

If you'd rather keep the key out of `local.properties`, create `apikey.properties` (also
git-ignored) and point the build script's `Properties` loader at it instead — the mechanism
is identical, only the file path changes.
