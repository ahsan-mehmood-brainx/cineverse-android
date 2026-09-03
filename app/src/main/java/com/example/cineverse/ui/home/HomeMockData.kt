package com.example.cineverse.ui.home

import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie

/**
 * Placeholder movie feed for the Home screen. posterUrl stays null until the TMDB
 * repository lands, so the UI always falls back to the local poster placeholder;
 * swapping this object out for a repository call is the only change needed later.
 */
object HomeMockData {

    val trendingMovies = listOf(
        Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Batman raises the stakes battling the Joker's reign of chaos over Gotham."),
        Movie(2, "Inception", null, 8.8, "2010-07-16", "Sci-Fi", "A thief who steals corporate secrets through dream-sharing is given a chance at redemption."),
        Movie(3, "Interstellar", null, 8.7, "2014-11-07", "Sci-Fi", "A team of explorers travel through a wormhole in search of a new home for humanity."),
        Movie(4, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir must navigate a treacherous desert planet to secure his family's future."),
        Movie(5, "Oppenheimer", null, 8.6, "2023-07-21", "Drama", "The story of the physicist who helped build the first atomic bomb.")
    )

    val popularMovies = listOf(
        Movie(6, "Avatar", null, 7.9, "2009-12-18", "Sci-Fi", "A paraplegic marine dispatched to Pandora becomes torn between orders and protecting the world."),
        Movie(7, "The Matrix", null, 8.7, "1999-03-31", "Sci-Fi", "A hacker discovers the shocking truth about his reality and his role in the war against its controllers."),
        Movie(8, "Gladiator", null, 8.5, "2000-05-05", "Action", "A betrayed Roman general sets out to seek revenge against the corrupt emperor."),
        Movie(9, "Spider-Man: No Way Home", null, 8.2, "2021-12-17", "Action", "Peter Parker seeks help from Doctor Strange after his identity is revealed."),
        Movie(10, "Avengers: Endgame", null, 8.4, "2019-04-26", "Action", "The remaining Avengers assemble to reverse the damage caused by Thanos.")
    )

    val topRatedMovies = listOf(
        Movie(11, "The Shawshank Redemption", null, 9.3, "1994-09-23", "Drama", "Two imprisoned men bond over years, finding solace and eventual redemption."),
        Movie(12, "The Godfather", null, 9.2, "1972-03-24", "Drama", "The aging patriarch of an organized crime dynasty transfers control to his son."),
        Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Batman raises the stakes battling the Joker's reign of chaos over Gotham."),
        Movie(13, "Pulp Fiction", null, 8.9, "1994-10-14", "Drama", "The lives of two mob hitmen, a boxer and a pair of diner bandits intertwine."),
        Movie(2, "Inception", null, 8.8, "2010-07-16", "Sci-Fi", "A thief who steals corporate secrets through dream-sharing is given a chance at redemption.")
    )

    val genres = listOf(
        Genre(1, "Action"),
        Genre(2, "Comedy"),
        Genre(3, "Drama"),
        Genre(4, "Horror"),
        Genre(5, "Sci-Fi"),
        Genre(6, "Romance"),
        Genre(7, "Thriller"),
        Genre(8, "Animation")
    )

    fun toUiState() = HomeUiState(
        trendingMovies = trendingMovies,
        popularMovies = popularMovies,
        topRatedMovies = topRatedMovies,
        genres = genres
    )
}
