gpkg-viewer-android
====================

The GPKG Viewer app demonstrates displaying OGC Geopackages in an Android application, using ArcGIS Runtime.

## Requirements

To run the app:

- Android 4.0.1 or higher

To build the app from source:

- Android SDK
  - Android API 14 or higher
- [ArcGIS Runtime SDK 10.2.5 for Android](https://developers.arcgis.com/android)
- Android Studio

## Usage

When the app launches, it displays a map of the world. You can then choose **Add Vector GPKG** or **Add Raster GPKG** from the menu to add data from Geopackages to the map.

### Add Vector GPKG

Navigate to a .gpkg file, and the app will add all feature classes in the Geopackage to the map.

### Add Raster GPKG

Navigate to a .gpkg file, and the app will add the first raster in the Geopackage to the map. (The ability to access only the first raster in a Geopackage is a limitation of ArcGIS Runtime 10.2.5 for Android.)

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

See [Issues](issues) for a list of known issues.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing

Copyright 2015 Esri

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

A copy of the license is available in the repository's [license.txt](license.txt) file.

Portions of this code use third-party libraries:

- Use of aFileChooser is governed by the Apache License.

See [license-ThirdParty.txt](license-ThirdParty.txt) for the details of these licenses.

[](Esri Tags: ArcGIS Android)
[](Esri Language: Java)
