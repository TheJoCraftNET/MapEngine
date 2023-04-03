# MapEngine

<img src="https://imgur.com/x3tR7jb.png" alt="logo" width="200">

[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/TheJoCraftNET/MapEngine?style=flat-square)](./#)
[![AGPLv3 License](https://img.shields.io/badge/License-AGPL%20v3-yellow.svg)](https://opensource.org/license/agpl-v3/)

## Description

MapEngine is a map library plugin for paper servers. It provides a simple API to create maps with custom content.
Using lightweight and asynchronous techniques MapEngine has a very low impact on server performance.

## Features

- Pipelined API for custom pipelines
- Asynchronous rendering
- Completely packet based
- Optional dithering (Floyd-Steinberg)
- Groups as z-layer interpretation for saving immense network traffic

<details>
<summary><strong>Color Conversion Cache Performance Graph</strong></summary>

![Performance Graph](https://i.imgur.com/TtVSqyq.png)

</details>

### Support

| Server Version | Supported |
|----------------|-----------|
| Paper 1.19.4   | ✔️        |
| Paper 1.19.3   | ✔️        |

## Usage

`MapEngine` has to be added as a dependency to the `plugin.yml` regardless of the build system used.

<details>
<summary><strong>Maven</strong></summary>

```xml
<repositories>
    <repository>
        <id>tjcserver</id>
        <url>https://repo.thejocraft.net/releases/</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>de.pianoman911</groupId>
        <artifactId>mapengine-api</artifactId>
        <version>1.3.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
</details>


<details>
<summary><strong>Gradle (groovy)</strong></summary>

```groovy
repositories {
    maven {
        url = 'https://repo.thejocraft.net/releases/'
        name = 'tjcserver'
    }
}

dependencies {
    implementation 'de.pianoman911:mapengine-api:1.3.1'
}
```

</details>

<details>
<summary><strong>Gradle (kotlin)</strong></summary>

```kotlin
repositories {
    maven("https://repo.thejocraft.net/releases/") {
        name = "tjcserver"
    }
}

dependencies {
    implementation("de.pianoman911:mapengine-api:1.3.1")
}
```

</details>

### Example

```java
public class Bar {

    // getting the api instance
    private static final MapEngine MAP_ENGINE = Bukkit.getServicesManager().getRegistration(MapEngineApi.class).getProvider();

    public void foo(BufferedImage image, BlockVector cornerA, BlockVector cornerB, BlockFace facing, Player viewer) {
        // create a map display instance
        IMapDisplay display = MAP_ENGINE.displayProvider().createBasic(cornerA, cornerB, facing);

        // create a color buffer for 2x2 item frames (128x128 pixels per map)
        FullSpacedColorBuffer buffer = new FullSpacedColorBuffer(256, 256);

        // resize the image
        image = ImageUtils.resize(image, 256, 256);
        // get all rgb values of the image
        int[] rgb = ImageUtils.rgb(image);

        // set the rgb values of the buffer, it respects the alpha channel
        // starting at x: 0 and y: 0
        // end at width: 256 and height: 256
        buffer.pixels(rgb, 0, 0, 256, 256);

        // create an input pipeline element
        // this object can also be used to draw simple shapes and text
        IDrawingSpace input = MAP_ENGINE.pipeline().drawingSpace(buffer, display);

        // add a player to the pipeline context,
        // making the player receive the map
        input.ctx().receivers().add(viewer);

        // enable floyd-steinberg dithering
        input.ctx().converter(Converter.FLOYD_STEINBERG);

        // flush the pipeline
        // the drawing space can be reused
        display.pipeline().flush(input);
    }
}
```
More detailed examples can be found in the [TheJoCraftNET/MapEngineExamples](https://github.com/TheJoCraftNET/MapEngineExamples) repository.

## Building

1. Clone the project (`git clone https://github.com/TheJoCraftNET/MapEngine.git`)
2. Go to the cloned directory (`cd MapEngine`)
3. Build the Jar (`./gradlew build` on Linux/MacOS, `gradlew build` on Windows)

The plugin jar can be found in the `build` → `libs` directory
