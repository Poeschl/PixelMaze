# PixelMaze

A maze drawer for the pixelflut protocol.

This project is extracted from the [HacktoberfestMunich/Pixelflut](https://github.com/HacktoberfestMunich/Pixelflut) repository to give it a better place. 
For the maze generation it uses the generator from [armin-reichert/mazes](https://github.com/armin-reichert/mazes).

# Setup project

To setup the project make sure you have Java (`> 8`) installed then run `.\gradlew build shadowJar` inside the project folder to generate a executable jar.

# How to use

Download the artefact for a fixed version or use the provided docker images.

```shell
java -jar pixelflut-pixelmaze-*.jar
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
