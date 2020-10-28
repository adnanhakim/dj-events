# DJ Events

## About

An internal Android application for Dwarkadas J. Sanghvi College to automate the process of notifying students about upcoming events from each and every committee and also notify students about important updates from each and every committee.

The innovation stems from the lack of a better system to handle the entire notification process. Many a times, students miss important notifications about upcoming events in the college as the messages are sent in the WhatsApp group chat which often goes unnoticed. That makes the app kind of a must-have to keep up with the events, in turn, making them addicted to the application as long as he/she wants to willingly take part in such events.

## Technology Stack

1. Developed using Android (Java)
1. User authentication using Firebase Authentication
1. Data is stored in Firebase Firestore
1. Uses Android External Storage API and Notification Enabled
1. Notification handled by a [Node.js](https://github.com/arshshaikh06/dj-events-admin) server for FCM Notifications

## Outline

The application consists of 11 activities and 10 fragments:
- **SplashScreenActivity:** The activity displays the DJSCE logo while the app is rendering and an `Intent` to progress to `LoginActivity`.
- **LoginActivity:** The activity contains 2 fragments: `SignInFragment` and `SignUpFragment` for signing in to the application and signing up for the application respectively. It uses Firebase authentication modules for user authentication, `SharedPreferences` for storing user data locally, and `Intents` to progress to the next step. The design of this activity will comprise of `ConstraintLayout` having a `FrameLayout` consisting of 2 fragments in a custom-built `TabLayout`, with each fragment containing `TextInputLayouts` and `MaterialSpinners` to get input from the user and a `FloatingActionButton` to progress to the next step. If the user is already signed in, `MainActivity` will be opened instead via an `Intent`.
- **MainActivity:** The activity contains a custom-built `BottomNavigationBar` which handles 3/4 fragments: `HomeFragment` (for viewing a customized feed for each user consisting of posts of all the committees), `EventsFragment` (for viewing a list of all committees and all the events organized by them), and `UserProfileFragment` / `CommitteeProfileFragment` depending upon the type of the user (for viewing and editing details of the profile), and an additional fragment, `AdminFragment` if the account is a committee account for adding new members, new posts, new events or editing the details of the account. Each of these fragments will be encapsulated in a `SwipeRefreshLayout` for updating the contents of each of these without needing a rerun of the application to refresh the data. `HomeFragment` and `EventsFragment` will consist of vertical and horizontal `RecyclerViews` with custom-built items as per need. `UserProfileFragment` will contain details of the profile in a `ConstraintLayout`. `CommitteeProfileFragment` will contain details of the committee in a `ConstraintLayout` having a `ViewPager2` handled by a 3 fragment custom-built `TabLayout` with fragments to view committee posts, events, and members namely `ProfilePostsFragment`, `ProfileEventsFragment`, and `ProfileMembersFragment` respectively. The `AdminFragment` (viewable only if it is a committee account), contains `Intents` to `EditProfileActivity`, `AddMemberActivity`, `AddPostActivity`, and `AddEventActivity` to edit committee profile, add members, add posts and add events respectively.
- **ImageActivity:** The activity consists of an `Intent` which triggers the `Android External Storage API` to select an image after checking and asking the required permissions. If the permissions are denied the activity is finished. Once the image is selected, the Uri of the image is displayed in an `ImageView` asking the user to confirm the selection of the image. If the image is selected, the File path of the image is extracted and sent to `AddPostActivity`/`AddEventActivity` depending upon where it was originally called from.
- **CommitteeActivity:** The activity contains details of the committee in a `ConstraintLayout` having a `ViewPager2` handled by a 3 fragment custom-built `TabLayout` with fragments to view committee posts, events, and members namely `ProfilePostsFragment`, `ProfileEventsFragment`, and `ProfileMembersFragment` respectively. The user also has an `ImageButton` available for subscribing to the particular committee.
- **CommentActivity:** The activity contains the post title and caption along with a vertically set `RecyclerView` to display all the comments. Each comment item consists of the username followed by the comment. It also contains an `EditText` with a `Button` to add a comment, and the added comment is updated in the `RecyclerView` automatically
- **AddMemberActivity:** The activity is built using `ConstraintLayout` containing `TextInputLayouts` to add members to the committee and can only be accessed if the account is a committee account. The data is then updated in Firebase Firestore. 
- **AddPostActivity:** The activity is built using `ConstraintLayout` containing `TextInputLayouts` and `MaterialSpinners` to add new posts and can only be accessed if the account is a committee account. The post can be textual or with an image. The data is then updated in Firebase Firestore. Once the post is updated in Firebase, the application triggers an API request to the backend which sends a notification to each subscribed user using Firebase Cloud Messaging Service.


## Developers

> Adnan Hakim
> [github.com/adnanhakim](https://github.com/adnanhakim)

> Arsh Shaikh
> [github.com/arshshaikh06](https://github.com/arshshaikh06)

## MIT LICENSE

> Copyright (c) 2020 Adnan Hakim
>
> Permission is hereby granted, free of charge, to any person obtaining a copy
> of this software and associated documentation files (the "Software"), to deal
> in the Software without restriction, including without limitation the rights
> to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
> copies of the Software, and to permit persons to whom the Software is
> furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all
> copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
> IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
> FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
> AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
> LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
> OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
> SOFTWARE.
