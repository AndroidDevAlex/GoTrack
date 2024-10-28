# GoTrack

This application enables users to track and filter their real-time location coordinates. With integrated mapping services, users can view their movement data on an interactive map. The app supports Firebase authentication, providing secure access and data storage. Users can save and retrieve their coordinates through Cloud Firestore.

## Technology Stack

- **Programming Language**: [Kotlin](https://kotlinlang.org/)
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Asynchronous Programming**: [Kotlin Flow](https://kotlinlang.org/docs/flow.html), [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- **Database**: [SQLite](https://www.sqlite.org/index.html) (with [Room](https://developer.android.com/training/data-storage/room))
- **Networking**: [Firebase](https://firebase.google.com)
- **Navigation**: Navigation component
- **View Layer**: XML (for layout design)
- **Localization**: Android resource system for localization

## Screenshots

### Authentication
| ![Screenshot 1](screenshots/sign_in.png)  | ![Screenshot 2](screenshots/sign_up.png)          |                                                |
|:-----------------------------------------:|:-------------------------------------------------:|:----------------------------------------------:|
### Main Dashboard
| ![Screenshot 3](screenshots/action.png)   | ![Screenshot 4](screenshots/settings.png)         | ![Screenshot 4](screenshots/auth.png)          |                                    
### Tracking
| ![Screenshot 4](screenshots/gps_not.png)  | ![Screenshot 5](screenshots/track_permission.png) | ![Screenshot 6](screenshots/tracking.png)      |
| ![Screenshot 7](screenshots/notif.png)    | ![Screenshot 8](screenshots/trackerIsOf.png)      |                                                | 

### Mapping
| ![Screenshot 9](screenshots/map.png)      | ![Screenshot 10](screenshots/mapDay.png)          | ![Screenshot 11](screenshots/noCoordinates.png)|                                                
## Features
- **Location tracking**: Track your location in real-time and send it to the server.
- **Map display**: View collected locations of authenticated users on a map.
- **Firebase integration**: Store and retrieve location data using Firebase/Cloud Firestore.
- **Firebase Authentication**: User authentication and registration are managed using Firebase Auth, allowing users to create accounts and sign in securely.
