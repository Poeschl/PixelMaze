# PixelMaze

A maze drawer for the Pixelflut protocol.

It draws a randomly generated "perfect" maze on an Pixelflut Canvas. All mazes can be solved and if wanted the maze can
be periodically refreshed.

This project is based on the [HacktoberfestMunich/Pixelflut](https://github.com/HacktoberfestMunich/Pixelflut)
repository. It's now on its own for better maintainability and all dependencies were updated to the latest version. For
the maze generation it uses the generator from [armin-reichert/mazes](https://github.com/armin-reichert/mazes).

# How to use

The PixelMaze application is available as artefact over the releases or as docker image. If you are using the artifact
make sure at least Java 15 is available on our system.

```shell
java -jar PixelMaze-<version>.jar
# or docker
docker run -it --rm ghcr.io/poeschl/pixelmaze
```

The PixelMaze application can be parameterized with following parameters (can also be shown with `-h` or `--help`)

```shell
usage: [-h] [--host HOST] [-p PORT] [-x X] [-y Y] [--width WIDTH]
       [--height HEIGHT] [-t TIMER] [--blank] [-c CELLSIZE]

optional arguments:
  -h, --help            show this help message and exit

  --host HOST           The host of the pixelflut server

  -p PORT, --port PORT  The port of the server

  -x X                  The x start position

  -y Y                  The y start position

  --width WIDTH         The maze width in pixel

  --height HEIGHT       The maze height in pixel

  -t TIMER,             Enable the regen of the maze after the value specified
  --timer TIMER         in seconds

  --blank               Enables blanking before redraw

  -c CELLSIZE,          The size inside a maze cell
  --cellsize CELLSIZE
```

# Compile project

To set up the project make sure you have Java (`> 15`) installed then run `.\gradlew shadowJar` inside the project
folder to generate an executable jar.
