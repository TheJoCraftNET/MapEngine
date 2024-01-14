# MapEngine

<img src="https://imgur.com/x3tR7jb.png" alt="logo" width="200">

[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/TheJoCraftNET/MapEngine?style=flat-square)](#)
[![AGPLv3 License](https://img.shields.io/badge/License-AGPL%20v3-yellow.svg?style=flat-square)](https://opensource.org/license/agpl-v3/)
[![Status Beta](https://img.shields.io/badge/Status-Beta-orange?style=flat-square)](#)

## Description

MapEngine is a map library plugin for paper servers. It provides a simple API to create maps with custom content.
Using lightweight and asynchronous techniques, MapEngine has a very low impact on server performance.

## Features

- Pipelined API for custom pipelines
- Asynchronous rendering
- Completely packet based
- Optional dithering (Floyd-Steinberg)
- Groups as z-layer interpretation for saving immense network traffic
- Per player buffering, only the changed pixels are sent to the player
- Optional packet bundling prevents tearing
- Drawing utilities (text, components, lines, triangles, rectangles, circles, ellipses, polygons)

<details>
<summary><strong>Color Conversion Cache Performance Graph</strong></summary>

![Performance Graph](https://i.imgur.com/TtVSqyq.png)

</details>

<details>
<summary><strong>Live streaming via RTMP on maps</strong></summary>
This is an example of a live stream on a map. The stream is played on a 7x4 map array.
The Stream source is 1920x1080@20 streamed with OBS.

[![Watch it here](https://i.imgur.com/h1e9ROE.png)](https://youtu.be/5tg_DX84eLw)

</details>

<details>
<summary><strong>Floyd Sternberg dithering</strong></summary>
This is an example of a map with Floyd-Steinberg dithering enabled. The stream is played on a 7x4 map array.
The Stream source is 1920x1080@20 streamed with OBS.

[![Watch it here](https://i.imgur.com/Q8Jg0oo.png)](https://youtu.be/b2wxlgllsQs)

</details>

### Javadoc

A hosted version of the javadoc can be found [here](https://mapengine.finndohrmann.de/javadoc/).

### Support

| Server Version | Supported |
|----------------|-----------|
| Paper 1.20.4   | ✔️        |
| Paper 1.20.3   | ✔️        |
| Paper 1.20.2   | ✔️        |
| Paper 1.20(.1) | ✔️        |
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
        <version>1.7.2</version>
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
    compileOnly 'de.pianoman911:mapengine-api:1.7.2'
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
    compileOnly("de.pianoman911:mapengine-api:1.7.2")
}
```

</details>

### Example

```java
public class Bar {

    // getting the api instance
    private static final MapEngineApi MAP_ENGINE = Bukkit.getServicesManager().load(MapEngineApi.class);

    public void foo(BufferedImage image, BlockVector cornerA, BlockVector cornerB, BlockFace facing, Player viewer) {
        // create a map display instance
        IMapDisplay display = MAP_ENGINE.displayProvider().createBasic(cornerA, cornerB, facing);
        display.spawn(viewer); // spawn the map display for the player

        // create an input pipeline element
        // this object can also be used to draw simple shapes and text
        IDrawingSpace input = MAP_ENGINE.pipeline().createDrawingSpace(display);

        // draw the image to the input pipeline element
        input.image(image,0,0);

        // draw a triangle
        input.triangle(0, 0, 10, 10, 20, 0, 0xff0000ff);

        // add a player to the pipeline context,
        // making the player receive the map
        input.ctx().receivers().add(viewer);

        // enable floyd-steinberg dithering
        input.ctx().converter(Converter.FLOYD_STEINBERG);

        // enable per player buffering
        input.ctx().buffering(true);

        // flush the pipeline
        // the drawing space can be reused
        input.flush();
    }
}
```

More detailed examples can be found in
the [TheJoCraftNET/MapEngineExamples](https://github.com/TheJoCraftNET/MapEngineExamples) repository.

## Building

1. Clone the project (`git clone https://github.com/TheJoCraftNET/MapEngine.git`)
2. Go to the cloned directory (`cd MapEngine`)
3. Build the Jar (`./gradlew build` on Linux/MacOS, `gradlew build` on Windows)

The plugin jar can be found in the `build` → `libs` directory
