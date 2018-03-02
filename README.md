# HttpLogMonitor
An http console log monitor progrom

The `MainApplication.java` is contained in `src/main/java/`. Running that file will run the main program and the two alert monitors contained in `src/main/java/core/monitors/`
* `MostSectionHitsForDuration.java`
* `TotalHitsAboveWatermark.java`

Application configuration can be found in `src/main/resources/config.properties`

* `core.monitors.mostsectionhitsforduration.interval.in.seconds=10` - Sets the interval to run the `MostSectionHitsForDuration` runnable object. (in essence, to report the section with the most hits every 10 seconds)

* `core.monitors.totalhitsabovewatermark.alert.watermark=12` - Sets the number of requests on average above which (inclusive) the `TotalHitsAboveWatermark` will alert.
* `core.monitors.totalhitsabovewatermark.interval.in.seconds=1` - Sets the interval to run the `TotalHitsAboveWatermark` runnable object (in essence, check every second to see if we're above or below the set watermark and report changes)
* `core.monitors.totalhitsabovewatermark.window.size.in.seconds=120` - Sets the duration window that the requests are looked at when determining if we're above the watermark. Requests older than this value in seconds aren't calculated in.

* `main.logfile.location=/basiclogfile.log` - Sets the location of the logfile to tail and monitor.

Included in `src/test/resources` are configurations specific for the testing support. 

Also in `srce/test/java/core` are some test files

`LogCreator.java` and `TestLogCreator.java` are just utility files to add random common log formatted entries to the `/basiclogfile.log` during testing of the `MainApplication.java`

There exist 2 test files inside `/monitors` for testing `MostSectionHitsForDuration` and `TotalHitsAboveWatermark`
