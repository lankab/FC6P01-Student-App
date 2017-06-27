# Demo
[View quick visual demo](https://www.youtube.com/watch?v=2skFCzy_1wc)

# Introduction
This project is one of two Android apps developed as part of my final project. This app is dedicated to London MET University students, enabling them to view past, present and future registered lectures. 

Students can also register their attendance for lectures in progress given that their smartphones' Bluetooth addresses running the app at the time have been scanned and inserted into a dedicated 'ScannedBluetoothAddresses' database table  by the second app. 

This process needs to be executed prior to students being able to register their attendance as once students attempt to register their attendances, it will only be successful if the smartphone's bluetooth address can be matched with a record inside ScannedBluetoothAddresses DB table for the corresponding lecture. 

# Features
* Login to student account using RESTful services
* Session Management using Shared Preferences
* View lectures' custom horizontal navigation UI
* Providing a number of status for each lecture: Attended lecture, Lecture not attended, Lecture in progress and future lecture.
* Material navigation drawer
* Providing warning if Internet connection could not be established via a snackbar component, enabling the launch of Android network conections settings.
* Providing warning if Bluetooth is not on will attempting to register attendance via a snackbar component, enabling the launch of Android bluetooth settings.
* Providing link to Android Bluetooth settings within the material navigation drawer.



# Dependencies
* com.wdullaer:materialdatetimepicker:2.3.0
* com.mikepenz:materialdrawer:5.9.1
* com.google.code.gson:gson:2.6.2
* com.squareup.retrofit2:retrofit:2.0.2
* com.squareup.retrofit2:converter-gson:2.0.2
