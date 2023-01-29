# PicACGComic

- PicACG unofficial client on Android platform

- [Material Design 3](https://m3.material.io/) with [material-components/material-components-android](https://github.com/material-components/material-components-android)

- Both `MOBILE` and `TABLET` device layouts are supported by following [guide by Google](https://developer.android.com/guide/topics/large-screens/large-screen-canonical-layouts)

- Latest Android SDK build (Current: [Android Tiramisu, API 33](https://developer.android.com/about/versions/13))

- Kotlin is main maintenance language

- Any BUG met, you are welcome to open an issue with `DETAILED DESCERIPTION` of BUG you met

- Pull Request is welcomed, but please provide with issue and fixed result in the PR comment

## Package Name
`projekt.cloud.piece.pic`

## State
`Unstable Alpha Development`

## Current Development Stepping
1) ~~Sign In~~ - `Done`
2) ~~Collections~~ - `Done`
3) ~~Random~~ - `Done`
4) ~~Search~~ - `Done`
5) ~~Category~~ - `Done`
6) Comment - `In Progress`
7) Download - `Planned`

## Build

- With IDE
    1) Install [Android Studio](https://developer.android.com/studio) (recommended) or [Intellij Idea](https://www.jetbrains.com/idea/)
    2) Clone and import this [repo]()
        1) File
        2) New
        3) Project from version control...
            - Version Control: `Git`
            - URL: `https://github.com/1552980358/PicACGComic`
            - Select directory
        4) Clone
    3) Build
    4) Generated Signed Bundle / APK
        1) `APK`
        2) Signing APK
            - `Choose existing...` to select your own existing signature file `.jks`
            - `Create new...` for creating new signature file `.jks`
        3) Build Variant: `release`
    5) Wait for building process
    6) `Generate Signed APK`
        - APK is located under `release` folder
        - `projekt.cloud.piece.pic-<VERSION_NAME>-<VERSION_CODE>-<DATE>-release.apk`

- With command line
    1) Preparation
        1) Install and setup JDK (OpenJDK 11 or above)
        2) Download and setup command line tools
            1) Download from `Command line tools only` from [https://developer.android.com/studio/index.html](https://developer.android.com/studio/index.html)
            2) Accept licenses `yes | ./sdkmanager --licenses`
            3) Setup unzipped command line tools path `export ANDROID_HOME=<PATH>`
    2) Clone this repo
        - `git clone https://github.com/1552980358/PicACGComic`
    3) Build
        - `./gradlew assembleRelease`
    4) Wait for building process
    5) `BUILD SUCCESSFUL`
        - APK is located under `release` folder
        - `projekt.cloud.piece.pic-<VERSION_NAME>-<VERSION_CODE>-<DATE>-release.apk`
    6) Sign APK
        - Create signature `.jks`
            - `keytool -genkey -v -keystore <SIGNATURE_NAME>.jks -keyalg RSA -keysize 2048 -validity 10000 -alias <ALIAS>`
        - Signing
            1) Align APK
                - `zipalign -v -p 4 <UNSIGNED_AND_UNALIGNED_NAME>.apk <UNSIGNED_AND_ALIGNED>.apk`
            2) Sign APK
                - `apksigner sign --ks <SIGNATURE_NAME>.jks --out <SIGNED>.apk <SIGNED_AND_ALIGNED>.apk`

## License - [GNU Affero General Public License v3.0](LICENSE)
```
                   GNU AFFERO GENERAL PUBLIC LICENSE
                      Version 3, 19 November 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
Everyone is permitted to copy and distribute verbatim copies
of this license document, but changing it is not allowed.
```