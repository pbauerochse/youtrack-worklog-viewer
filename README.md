YouTrack Worklog Viewer
=======================

# About this software
YouTrack is a commercial issue tracker created by [Jetbrains](https://www.jetbrains.com/youtrack/) where you also have the possibility to log your time spent on each individual issue/task. Unfortunately, if you are like me and don't book your time right away, there is limited options to get an overview of how much time you have already booked on every single day of the week (addressed in [this issue](https://youtrack.jetbrains.com/issue/JT-29224)).

With this tool you can now overcome this issue. It let's you create your personal time tracking report as depicted in this screenshot

![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/screenshot.png) (*Issues column has been obfuscated for the screenshot*)

## Latest Version

[Download v2.3.4](https://github.com/pbauerochse/youtrack-worklog-viewer/releases/tag/2.3.3)

## How does it work?
You simply enter the URL to your YouTrack installation, and your own login data at the settings screen, select the reporting range in the main window and click on the "Download worklogs" button. That's it!

## FAQ

**What are the requirements?**

*You need to have Java 8 installed. The application has been tested with a Java version >= 1.8.0_25 on a Mac and Linux computer. Also you need an account to the YouTrack installation you want to access, with the right to create a time report*

**How do I start the application?**

*Simply open up your terminal/console and start the application with `java -jar youtrack-worklog-viewer-[version].jar` or right click the file and select `Open with...` and then select the path to your java executable*

**Where are the settings stored?**

*The worklog viewer creates a file in your user home directory with the name `youtrack-worklog.properties`. All your settings are in there. Though - or because - it is a plain text file, your YouTrack password will not be stored in cleartext, instead it will be encrypted before storing it in that file. Please note: even though the password is encrypted, it is not impossible for anyone with access to your computer to decrypt the password*

**Which YouTrack versions are supported?**

*It has been tested with YouTrack Version 6 up to YouTrack 2017.4*

**How does the tool get the data from YouTrack?**

*The application logs on to your YouTrack installation using the regular YouTrack API and the data provided by you in the settings screen. It then creates a temporary time report and downloads it as .csv file. The data in that file is being processed and displayed in the main window. After retrieving the data, the temporarily created report will be deleted to prevent polluting your reports section*

**What reporting options are available?**

*You can create a report for the current week, previous week, current month and previous month or a free time range. You can also enter your daily work hours to adjust the time format (e.g. 9 booked hours will be presented in an 8 hour workday with `1d 1h` and in an 9 hour workday with `1d`)*  

**I just get a blank report. What's wrong?**

*This tool checks your personal worklog by comparing your username from the settings screen, with the worklog author name from the report. YouTrack seems to allow you to have a different username than the login name. Please make sure, that in your YouTrack profile, the login name is the same as your actual username*

**Why are worklogs for subtasks not taken into calculation?**

*This seems to be a bug in YouTrack with the generation of the time report. See https://youtrack.jetbrains.com/issue/JT-29447 . Unfortunately I can't do anything about this :disappointed:*

**Can I use OAuth2 authentication?**

*Starting from version 2.1.0 you may now switch authentication from REST to OAuth2 in the settings dialogue. To get this working you will need to configure a service in the YouTrack Hub for the YouTrack Worklog Viewer and also provide the client id and client secret in the settings dialogue. See [the Wiki page](https://github.com/pbauerochse/youtrack-worklog-viewer/wiki/Authentication-with-YouTrack) for a detailed explanation*

*Please note, that starting from YouTrack Version 2017.4, the use of permanent token with YouTrack scope is required. A tutorial on how to generate these tokens can be found [here](https://www.jetbrains.com/help/youtrack/standalone/Manage-Permanent-Token.html). Your user needs to have the "Service Read" Role*

