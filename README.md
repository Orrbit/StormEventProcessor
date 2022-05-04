# Storm Event Processor
This repository takes in data from NOAA representing storm events as well as radar locations in order to determine the closest radar station to a storm event. The purpose that this serves is so that NOAA archival data can easily be looked up and downloaded with a label indicating that it was a storm. This will assist in supervised machine learning where the label is that a storm exists in the image.

The storm events can be found at https://www.ncei.noaa.gov/pub/data/swdi/stormevents/csvfiles/. The URL provides StormEvents_*.csv.gz which detail all storms reported to NOAA. The Schema for those files can be found at the same site at https://www.ncei.noaa.gov/pub/data/swdi/stormevents/csvfiles/Storm-Data-Bulk-csv-Format.pdf.

On older list of the stations is used at https://apollo.nvu.vsc.edu/classes/remote/lecture_notes/radar/88d/88D_locations.html. This gives the latitude and longitude of each station ID.

The application runs as a compiled spark job. Included in the docker repository is instructions to run a mock spark cluster to execute the job.

## Input
Storm Events CSV
 - Begin Date & Timezone
 - Begin Latitude & Longitude
 - Event Type
 - Event ID

Radar Locations
 - Station ID
 - Latitude & Longitude

## Output

Storm Events + Closest Station
 - Begin Date & Timezone
 - Begin Latitude & Longitude
 - Event Type
 - Event ID
 - Station ID

