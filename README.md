# HttpLogMonitor Console Program

Create a simple console program that monitors HTTP traffic from a logfile:

* Consume a w3c common log formatted HTTP log 
* The program should be able to run continuously to consume the log as it's written to
* Every 10s, display in the console the sections of the web site with the most hits, and some other statistics on the traffic as a whole.
* Whenever total traffic for the past 2 minutes exceeds a certain number on average, add a message saying that "High traffic alert triggered - total hits in {120} second window: {hits}, average hit rate: {hit-rate} - triggered at {currenttime}"
* Whenever the total traffic drops again below that value on average for the past 2 minutes, add another message detailing when the alert recovered

### Glossary
* Section - What's after the scheme and before the second '/' in a URL. e.g. the section for "http://my.site.com/pages/create' is "my.site.com/pages"
* Common Log Format - https://en.wikipedia.org/wiki/Common_Log_Format

### Running the application
The `MainApplication.java` is contained in `src/main/java/`. Running that file will run the main program and the two alert monitors contained in `src/main/java/core/monitors/`
* `MostSectionHitsForDuration.java`
* `TotalHitsAboveWatermark.java`

### Configuration
Application configuration can be found in `src/main/resources/config.properties`

* `core.monitors.mostsectionhitsforduration.interval.in.seconds=10` - Sets the interval to run the `MostSectionHitsForDuration` runnable object. (in essence, to report the section with the most hits every 10 seconds)

* `core.monitors.totalhitsabovewatermark.alert.watermark=12` - Sets the number of requests on average above which (inclusive) the `TotalHitsAboveWatermark` will alert.
* `core.monitors.totalhitsabovewatermark.interval.in.seconds=1` - Sets the interval to run the `TotalHitsAboveWatermark` runnable object (in essence, check every second to see if we're above or below the set watermark and report changes)
* `core.monitors.totalhitsabovewatermark.window.size.in.seconds=120` - Sets the duration window that the requests are looked at when determining if we're above the watermark. Requests older than this value in seconds aren't calculated in.

* `main.logfile.location=/basiclogfile.log` - Sets the location of the logfile to tail and monitor.

### Testing
Included in `src/test/resources` are configurations specific for the testing support. 

Also in `src/test/java/core` are some test files

`LogCreator.java` and `TestLogCreator.java` are just utility files to add random common log formatted entries to the `/basiclogfile.log` during testing of the `MainApplication.java`

There exist 2 test files inside `/monitors` for testing `MostSectionHitsForDuration` and `TotalHitsAboveWatermark`

### Possible Improvements

Things that could improve this project
* Adding transactions in the monitors as access to variables from `run()` and `processEvent(HttpEvent event)` could happen at the same time and pose race conditions.
* Ideally sending HttpEvents to the monitors would be done by some kind of message broker, rather than called explicitly from the `TailListener`
* Just using System.out.println to manifest the information isn't ideal.
* Currently the monitors don't look at the requests timestamps at all to determine when things are happening. They just go by when they receive the processEvent. They should look at timestamps to be able to handle events that happened in the past.
* Storage of these alerts and events would be necessary to facilitate queries on past events and monitoring as it happened, not as it was learned about by the monitor
