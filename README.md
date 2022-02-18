# Pemanfaatan Algoritma _Greedy_ dalam Aplikasi Permainan "Overdrive"

Tugas Besar I IF2211 Strategi Algoritma

## Description

Mengimplementasi bot mobil dalam permainan Overdrive dengan menggunakan strategi greedy untuk memenangkan permainan.

## Getting Started

### Prerequisite

- The Java starter bot requires Java 8 or above.
  https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

### Installation

1. Clone the repo

   ```sh
   git clone https://github.com/kentlius/Tubes1_kachow.git
   ```

2. Copy Tubes1Stima folder to starter-pack folder

3. Edit game-runner-config.json to

   ```json
   {
     "round-state-output-location": "./match-logs",
     "game-config-file-location": "game-config.json",
     "game-engine-jar": "game-engine.jar",
     "verbose-mode": true,
     "max-runtime-ms": 1000,
     "player-a": "./Tubes1_kachow",
     "player-b": "./reference-bot/java",
     "max-request-retries": 10,
     "request-timeout-ms": 5000,
     "is-tournament-mode": false,
     "tournament": {
       "connection-string": "",
       "bots-container": "",
       "match-logs-container": "",
       "game-engine-container": "",
       "api-endpoint": "http://localhost"
     }
   }
   ```

## Usage

1. Execute run.bat

## Author

- 13520025 - Fransiskus Davin Anwari
- 13520069 - Kent Liusudarso
- 13520121 - Nicholas Budiono
