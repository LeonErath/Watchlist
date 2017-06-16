# Watchlist

### A little App to Learn the Basics of the new Programming Language Kotlin

I've programmed an App, where I can see all upcoming Movies. I can store Movies that I like in a separate Watchlist. The reason for programming this App is to learn the new language Kotlin. I'm hoping to get a better understanding of its functions and why everybody loves kotlin. I will publish now and then new updates.


PopularMoviesFragment            |  MovieDetailActivity |  MyListFragment
:-------------------------:|:-------------------------:|:-------------------------:
<img src="https://github.com/LeonErath/KotlinApplication/blob/master/screenshots/PopularMovies.png" data-canonical-src="https://raw.githubusercontent.com/LeonErath/KotlinApplication/master/screenshots/PopularMovies.png" width="250" height="450" />                            |  <img src="https://github.com/LeonErath/KotlinApplication/blob/master/screenshots/MovieDetail.png" data-canonical-src="https://raw.githubusercontent.com/LeonErath/KotlinApplication/master/screenshots/MovieDetail.png" width="250" height="450" />         |  <img src="https://github.com/LeonErath/KotlinApplication/blob/master/screenshots/Watchlist.png" data-canonical-src="https://raw.githubusercontent.com/LeonErath/KotlinApplication/master/screenshots/Watchlist.png" width="250" height="450" />
NavigationView            |  MovieDetailActivity 
:-------------------------:|:-------------------------:
<img src="https://github.com/LeonErath/Watchlist/blob/master/screenshots/NavigationView.png" data-canonical-src="https://raw.githubusercontent.com/LeonErath/Watchlist/master/screenshots/NavigationView.png" width="250" height="450" />         |  <img src="https://github.com/LeonErath/Watchlist/blob/master/screenshots/MovieDetail2.png" data-canonical-src="https://raw.githubusercontent.com/LeonErath/Watchlist/master/screenshots/MovieDetail2.png" width="250" height="450" />


# TODO
- [x] PopularMovie Fragment with current Movies [12.06.2017]
- [x] Detail Overview of Movies [12.06.2017]
- [x] Functionality to add movies to a watchlist [14.06.2017]
- [x] Functionality to show movies which are currently displayed in cinemas [13.06.2017]
- [x] Added parser to display release date and movie revenue [14.06.2017]
- [x] Added snackbar for a good user experience [14.06.2017]
- [x] Trailers for Movies in DetailActivit[16.06.2017]
- [x] Added NavigationView for more Features [16.06.2017]
- [x] Actors in DetailActivity [16.06.2017]
- [ ] Genre Overview
- [ ] Search Function (started implementing)
- [ ] Series Overview

## Update 16.06.2017

Today was a good day. I had a lot of time so I could read me through the Youtube API. I found it quite hard to understand what exactly I should do, but with some Youtube videos I figured it out. So now you can watch trailers in the DetailActivity. I also added a recyclerview to display all actors from the movie. Later that day I quickly added an NavigationView so that I can implement more features in my app. I'm not really happy about the design but for now, it will work. When I have more time I will definitely revise my design. 

P.S.: I renamed my app from KotlinApplication to Watchlist.

## Update 14.06.2017

Today I found many bugs. More or less impactful but glad I could fix almost all of them. Added some parser for MyListFragment to display the watchlist correct. Also learned a new feature from kotlin: "with(){}". Made the ViewHolder-Code in MovieFlatBinder much cleaner. Also created a snackbar to notify the user if he adds a movie to the watchlist. Was a little bit difficult to build the reference from the MainActivity to the MovieBinder but in the End, it paid of.

## Update 13.06.2017

Cleaned my Code. I'm still making many mistakes when it comes to Kotlins syntax. Hope I will get used to not using semicolons or not using getter/setter methods. Additionally added some logic to my database. Added the relationship between movies and lists. I could delete some of the boilerplate code because of this.
