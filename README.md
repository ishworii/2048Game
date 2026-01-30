# 2048

A Java Swing implementation of the classic 2048 game with smooth animations and an intuitive interface.

## Screenshots

### Game Start
![Game Start](resources/game_start.png)

### During Gameplay
![During Game](resources/during_game.png)

### Game Over
![Game Over](resources/game_over.png)

## Gameplay Demo

<video src="resources/gameplay.webm" controls autoplay loop muted></video>


## Features

- Smooth tile animations with easing functions
- Dynamic font sizing for larger numbers
- Score tracking system
- Clean, modern UI matching the classic 2048 design
- Game over detection and display

## How to Run

1. Compile the project:
   ```bash
   javac -d out src/core/*.java src/ui/*.java src/Main.java
   ```

2. Run the game:
   ```bash
   java -cp out Main
   ```

## How to Play

- Use arrow keys to slide tiles in four directions
- Tiles with the same number merge when they collide
- Each merge adds the new tile value to your score
- The goal is to create a tile with the number 2048

## Game Rules

- After each move, a new tile (2 or 4) appears in a random empty spot
- The game ends when no more moves are possible
- Tiles only merge once per move
