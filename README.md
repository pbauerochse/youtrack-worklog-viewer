YouTrack Worklog Viewer
=======================
YouTrack Worklog Viewer helps you keep track of your booked time in the commercial [Jetbrains YouTrack](https://www.jetbrains.com/youtrack/) Issue tracker 
by connecting to your YouTrack  instance and downloads the booked work time within a definable timespan. 

If you find this tool useful and would like to show me your appreciation [buy me a beer](https://www.paypal.me/patrickbrandes)

# Latest Version

[Download v2.7.3](https://github.com/pbauerochse/youtrack-worklog-viewer/releases/tag/2.7.3)

# Features

* search for issues and save them as favourite for quick access
* save search queries as favourites for quick access
* display your own spent time within the defined timespan in a table
* display all other time spent for each project in a seperate tab
* statistical charts on the current time report (can be turned off in the settings)
* change the grouping of the downloaded data (e.g. by project, work author, status or any other YouTrack field)
* add work items from within the application
* export the data to Excel
* two color schemes: light and dark

# Important changes
* **v2.7.3**
  * [#43 - Adding the project full name as grouping criteria](https://github.com/pbauerochse/youtrack-worklog-viewer/issues/43)
* **v2.7.2**
  * Fixed a bug reading the correct version from most recent releases
  * Updated dependencies
  * Added release for Apple Silicon CPUs
* **v2.7.1**
  * Added an indicator on issues with an estimate, to show how much time of the estimate was already spent
  * optimized detail view
    * long field values are abbreviated to prevent breaking the list view
    * summary in the title bar won't collapse anymore when too long
    * properly escaping HTML entities in the description
  * errors while decrypting the stored authentication won't prevent the application from starting anymore
  * fixing a bug where the file dialog won't show when the previously used directory does not exist anymore 

* **v2.7.0**  
  * Added a logo created by Patrick Marx
  * Minor changes to the plugin API
  * New search results view, containing more information
  * Creating new work items dialog now also contains a field for the work type
  * Statistics pane shows graphs depending on the selected grouping criteria
  * Progressbar shows the current tasks name
  * Issue details view now also contains the issues fields

* **v2.6.1**
  * Booked time overview in the statistics pane on the "own worklogs" tab
  * Saving window divider position in the settings
  * Fixed [#39 - Error parsing custom field value](https://github.com/pbauerochse/youtrack-worklog-viewer/issues/39)

* **v2.6.0**
  * Grouping criterias are now applied on-the-fly, without having to reload the report
  * Two new report time ranges: "last two weeks" and "this and last week"
  * Marking issues and frequently used search queries as favorites
  * Adding a view to search for issues and access your favourites
  * Added context menus in the issue view allowing you to
       * Open the selected issue in the browser
       * Add a new work item to the issue
       * Add a new work item to any other issue
       * Add the issue to your saved favourites
  * You may now define various keyboard shortcuts (e.g. for downloading the current time report) in the settings menu
  * Finer control of the work hours per day (quarter hour intervals)  
  * Added a basic plugin mechanism, that allows you to extend the Worklog Viewer with own functionality (beta, no documentation yet)
* **v2.5.0**
  * Switching to Java 11 as minimum required Java version. If you need to stick to Java < 11 please use the [v2.4.3 release](https://github.com/pbauerochse/youtrack-worklog-viewer/releases/tag/2.4.3) 
* **v2.4.0**
  * Dropping support for YouTrack versions < 2018.1
  * Dropping OAuth2 and password authentication in favor of new token based authentication
  
### Roadmap
  * Optional application installer
  * Further usability improvements (keyboard navigation)
  * High contrast theme for the visually impaired
  * Allowing to group by multiple criteria
  * Suggestions very welcome!

# Minimum requirements

* Java 11 Runtime
* Access to a YouTrack instance >= 2018.x
 
# Screenshots

| |  |
|:---|:---|
| ![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/docs/screenshots/own-worklogs-bright.png) | ![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/docs/screenshots/own-worklogs-dark.png) |
| Own worklogs (light theme) | Own worklogs (dark theme)|
| ![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/docs/screenshots/all-worklogs-bright.png) | ![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/docs/screenshots/all-worklogs-dark.png) |
| All worklogs (light theme) | All worklogs (dark theme)|

## Adding new work items
![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/docs/screenshots/add-work-item/context-menu.png)

*Right click on an issue column to access the context menu*

![Screenshot of the report](https://raw.githubusercontent.com/pbauerochse/youtrack-worklog-viewer/master/docs/screenshots/add-work-item/add-work-item-screen.png)

*Define the details and hit save*

## Troubleshooting

**How do I start the application?**

*Simply open up your terminal/console and start the application with `[/path/to/java-11/bin/]java -jar youtrack-worklog-viewer-[version].jar` or right click the file and select `Open with...` and then select the path to your java executable*

**How do I setup the authentication in YouTrack?**

*There is a tutorial on how to configure YouTrack authentication on the [Wiki page](https://github.com/pbauerochse/youtrack-worklog-viewer/wiki/Authentication-with-YouTrack).*

**Which YouTrack versions are supported?**

*Support for YouTrack versions older than 2017.4 has been dropped in the Worklog Viewer version 2.4.0. If you have a YouTrack version between 6 and 2017.4, please use any YouTrack WorklogViewer release before 2.4.0*

**I just get a blank report. What's wrong?**

*There are several reasons, why this might happen:*

* **You entered a wrong username in the settings** - *This tool checks your personal worklog by comparing your username from the settings screen, with the worklog author name from the report. YouTrack seems to allow you to have a different username than the login name. Please make sure, that in your YouTrack profile, the login name is the same as your actual username*
* **You selected the wrong connector version** - *Jetbrains is constantly working on their product. To adapt to the changes, you need to specify the matching connector version in the settings dialog. Please check, if you have selected an outdated version in the settings*
* **There simply are no tracked work items in the specified time range**

**I found a bug / have a question / have a feature request**

*Please feel free to file an issue here at the Github project and I'll see what I can do.*