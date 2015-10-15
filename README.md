YouTrack Worklog Viewer
=======================

# About this software
YouTrack is a commercial issue tracker created by [Jetbrains](https://www.jetbrains.com/youtrack/) where you also have the possibility to log your time spent on each individual issue/task. Unfortunately, if you are like me and don't book your time right away, there is limited options to get an overview of how much time you have already booked on every single day of the week (addressed in [this issue](https://youtrack.jetbrains.com/issue/JT-29224)).

With this tool you can now overcome this issue. It let's you create your personal time tracking report as depicted in this screenshot

![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/screenshot.png) (*Issues column has been obfuscated for the screenshot*)

## Latest Version

[Download v2.1.0](https://github.com/pbauerochse/youtrack-worklog-viewer/releases/tag/2.1.0)

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

*Since I only have access to a YouTrack 6 installation, this tool has only been tested on that version. I can not guarantee, that it will work with any other version*

**How does the tool get the data from YouTrack?**

*The application logs on to your YouTrack installation using the regular YouTrack API and the data provided by you in the settings screen. It then creates a temporary time report and downloads it as .csv file. The data in that file is being processed and displayed in the main window. After retrieving the data, the temporarily created report will be deleted to prevent polluting your reports section*

**What reporting options are available?**

*You can create a report for the current week, previous week, current month and previous month or a free time range. You can also enter your daily work hours to adjust the time format (e.g. 9 booked hours will be presented in an 8 hour workday with `1d 1h` and in an 9 hour workday with `1d`)*  

**I just get a blank report. What's wrong?**

*This tool checks your personal worklog by comparing your username from the settings screen, with the worklog author name from the report. YouTrack seems to allow you to have a different username than the login name. Please make sure, that in your YouTrack profile, the login name is the same as your actual username*

**Why are worklogs for subtasks not taken into calculation?**

*This seems to be a bug in YouTrack with the generation of the time report. See https://youtrack.jetbrains.com/issue/JT-29447 . Unfortunately I can't do anything about this :disappointed:*

**I'm seeing a lot question marks where umlauts should be**

*Unfortunately this also is a YouTrack issue: The report itself already contains the broken characters so I have no chance to fix that. If you want to get this fixed vote on the issue at https://youtrack.jetbrains.com/issue/JT-29234*

**Can I use OAuth2 authorization?**
*Starting from version 2.1.0 you may now switch authentication from REST to OAuth2 in the settings dialogue. To get this working you will need to configure a service in the YouTrack Hub for the YouTrack Worklog Viewer and also provide the client id and client secret in the settings dialogue*

## Setting up OAuth2
To get OAuth2 authentication working you need to set up a Service in the YouTrack Hub for the YouTrack Worklog Viewer. This only needs to be done once by your YouTrack administrator. The following steps will guide you as an administrator through the process.
 
### Step 1 - Setup the Service
Login to the YouTrack Hub. This web-interface can be accessed by pointing your browser to `<YourYouTrackBaseUrl>/hub` (e.g. if your YouTrack dashboard can be accessed with `https://issues.mycompany.com/dashboard` then your Hub URL is `https://issues.mycompany.com/hub`). In the top navigation click on "More settings" and then "Services"

![Step 1 - Create Service in Hub](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/oauth_step1_service.png)

Then click on "Create Service..." and put in a name for the service (e.g. YouTrack Worklog Viewer). You may leave the Home URL blank or put in the link to the github page. Click "Create Service"

### Step 2 - Set the service to trustworthy
Once you created the service, you need to set this service to trusted by clicking the "Trust" button. 

![Step 2 - Trust and Client ID](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/oauth_step2_trust_and_client_id.png)

The generated ID later needs to be copied to every YouTrack Worklog Viewer client that accesses the data from this YouTrack instance.

### Step 3 - Set or generate the service secret
Now you need to obtain the secret token. YouTrack can generate a token for you or you may set a fixed token. This token and the client id from step 2 need to be copied to your YouTrack Worklog Viewer settings.
 
![Step 3 - The service secret](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/oauth_step3_secret_token.png)

Attention: The token presented to you in that popup window is *NOT* the current set token. It is a newly generate random token, that gets set once you click on "Change secret". Make sure you copy the token *before* changing it or you will never ever see that token again and have to generate a new one.

### Step 4 - Set the client Id and secret token in YouTrack Worklog Viewer
Now that you have obtained the client id and the secret service token, you must paste that information into your YouTrack Worklog Viewer settings dialogue

![Step 4 - YouTrack Worklog Viewer settings](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/oauth_step4_ywlv_settings.png)

Set the authentication method to OAuth2 and click on "Save". Now you should be good to go with OAuth2. The OAuth2 secret token will be encrypted in the same way the user password gets encrypted, before saving it to the `youtrack-worklog.properties` file. 

### Optional Step 5 - Service for every single user
To add even more security you could consider repeating all steps for every single user that uses the YouTrack Worklog Viewer, so they won't share a single service id and service secret.
This is an optional step and is not required. The OAuth2 authentication works perfectly fine with a shared service id and secret.
