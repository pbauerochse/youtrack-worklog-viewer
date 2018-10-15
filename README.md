YouTrack Worklog Viewer
=======================

# About this software
YouTrack is a commercial issue tracker created by [Jetbrains](https://www.jetbrains.com/youtrack/) where you also have the possibility to track the time you spent on each individual issue. Unfortunately, if you are like me and don't book your time right away, there is limited options to get an overview of how much time you have already booked on every single day of the week (addressed in [this issue](https://youtrack.jetbrains.com/issue/JT-29224)).

With this tool you can see the time you spent on each project and individual task as depicted in the following screenshot.

![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/screenshot.png) (*Issues column has been obfuscated for the screenshot*)

## Latest Version

[Download v2.5.0](https://github.com/pbauerochse/youtrack-worklog-viewer/releases/tag/2.5.0)

Most parts of the Worklog Viewer have been rewritten for the 2.4.0 release. If you ever experience any trouble, please file an issue here at the Github Project and let me know about it.

Please note, that authentication support for OAuth2 and Password authentication has been droped, as well
as the support for any YouTrack version less than 2018.1. If you have an older version of YouTrack please use
any of the older releases of the YouTrack Worklog Viewer.

If you need help, on how to configure token authentication, please head over to the [Wiki page](https://github.com/pbauerochse/youtrack-worklog-viewer/wiki/Authentication-with-YouTrack).

## How does it work?
You simply enter the URL to your YouTrack installation, and your own login data at the settings screen, select the reporting range in the main window and click on the "Download worklogs" button. That's it!

## FAQ

**What are the requirements?**

* *At least a Java 8 Runtime. Please note: if you are using Linux and installed the OpenJDK via the package manager, you might need to install openjfx too (e.g. `sudo apt install openjfx`)*
* *Of course you need a valid YouTrack account for the instance you want to fetch the worklogs from*

**How do I start the application?**

*Simply open up your terminal/console and start the application with `java -jar youtrack-worklog-viewer-[version].jar` or right click the file and select `Open with...` and then select the path to your java executable*

**Where are the settings stored?**

*The worklog viewer creates a file in your user home directory with the name `.youtrack-worklog-viewer.json`. All your settings are in there. Though - or because - it is a plain text file, your YouTrack credentials will not be stored in cleartext, instead they will be encrypted before storing them in that file. Please note: even though the credentials are encrypted, it is not impossible for anyone with access to your computer to decrypt them again*

**Which YouTrack versions are supported?**

*Support for YouTrack versions older than 2017.4 have been dropped in the Worklog Viewer version 2.4.0. If you have a YouTrack version between 6 and 2017.4, please use any YouTrack WorklogViewer release before 2.4.0*

**How does the tool get the data from YouTrack?**

*Depending on your YouTrack version, the WorklogViewer creates and downloads a TimeReport on your behalf, or executes a query and downloads the results.*

**What reporting options are available?**

*You can create a report for the current week, previous week, current month and previous month or a free time range. You can also enter your daily work hours to adjust the time format (e.g. 9 booked hours will be presented in an 8 hour workday with `1d 1h` and in an 9 hour workday with `1d`)*  

**I just get a blank report. What's wrong?**

There are several reasons, why this might be the case:

* **You entered a wrong username in the settings** - *This tool checks your personal worklog by comparing your username from the settings screen, with the worklog author name from the report. YouTrack seems to allow you to have a different username than the login name. Please make sure, that in your YouTrack profile, the login name is the same as your actual username*
* **You selected the wrong connector version** - *Jetbrains is constantly working on their product. To adapt to the changes, you need to specify the matching connector version in the settings dialog. Please check, if you have selected an outdated version in the settings*
* **You entered a wrong work date field in the settings** - *There is a bug in YouTrack version 2018.2 which requires you the specify the field name for the work date query. Please check out [the Wiki](https://github.com/pbauerochse/youtrack-worklog-viewer/wiki/Work-Date-Field-Help) for help.*
* **There simply are no tracked work items in the specified time range**
