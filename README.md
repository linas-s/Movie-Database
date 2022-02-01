# MovieDB 🎬 

An Android movie database application, which was created using MVVM, Retrofit, Paging 3, Dagger Hilt, Coroutines, Flow, Room, Navigation Component and Glide. The app fetches data from the [The Movie Database API](https://developers.themoviedb.org/3) and caches it in the phones database.<br />

# How does it look? ✨

| Home screen | Home screen video |
| ------------ | ------------- |
| ![image](https://user-images.githubusercontent.com/57355498/151844980-2dfc435c-2111-42ec-a2f5-436a3c36bd48.png) | ![qemu-system-x86_64_XHfoneW8Jb](https://user-images.githubusercontent.com/57355498/151845066-a86ec5dc-4ef5-4250-90ed-789454f427fd.gif) |


| Watchlist screen  | Search screen |
| ------------ | ------------- |
| ![watchlist](https://user-images.githubusercontent.com/57355498/151847750-6c3bb175-f236-4eb1-8e11-5fe36d80f642.jpg) |![search](https://user-images.githubusercontent.com/57355498/151847772-a28f5ff1-01ba-4d09-ad06-f4b808ce1413.jpg) |

| Media details screen  | Person screen |
| ------------ | ------------- |
| ![qemu-system-x86_64_hB45ee3DgL](https://user-images.githubusercontent.com/57355498/151846048-685a3891-1673-4c78-b8b2-26f6efd9a5ab.gif) | ![image](https://user-images.githubusercontent.com/57355498/151846178-af824144-6e5a-4fdb-88f9-6b702822d693.png) |

# Architecture


The Model-View-ViewModel(MVVM) pattern was used, which separates the data presentation logic from the core business logic part of the application. A single-activity architecture was used, using the Navigation Components to manage fragment operations with multiple backstacks.

<p align="center">
  <img src="https://user-images.githubusercontent.com/57355498/151838352-35c07ad5-4d99-46eb-b000-2cd15b4aaa14.png">
</p>

# Technologies used ⚙️
• [Retrofit](https://square.github.io/retrofit/) - REST API Client <br />
• [Dagger Hilt](https://dagger.dev/hilt/) - Dependency injection <br />
• [Coroutines](https://developer.android.com/kotlin/coroutines) - Thread management and data flow <br />
• [Room](https://developer.android.com/topic/libraries/architecture/room) - Data persistance and offline caching <br />
• [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) - Pagination <br />
• [Navigation Component](https://developer.android.com/guide/navigation) - Handle navigations and passing of data between destinations <br />
• [Glide](https://github.com/bumptech/glide) - Image loading <br />

# Installation

You will need an TheMovieDB API key (befd4e9e671b15b8d0ef89554fae5966). You need to put it in your gradle.properties file and include the following line:
```
movie_api_access_key="f7244e2fd595017cabf28e1e95ab7d34"
```
You may need to Rebuild the project for it to work.

