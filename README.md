# roborally

This repository showcases my contributions to a university course project where we developed a digital adaptation of the RoboRally board game (2017 edition).

> [!Note]
> This repository is archived and will not receive further updates. While the server and random bot are functional, the state of the code is not intended to be run or played as a complete game.

## Project Overview

The RoboRally project features a server-client structure based on a TCP protocol utilizing JSON message serialization. The focus of this repository is to highlight my skills in the application of software design patterns, work on extensive Java projects and game development.

You can find the rules [here](https://media.wizards.com/2017/rules/roborally_rules.pdf). 

## Features

1. Server-Client Structure
   
        TCP Protocol: Implemented a robust server-client architecture using TCP for reliable communication.

        JSON Serialization: Utilized JSON for message serialization, ensuring smooth data exchange.

2. Random Bot
   
        Automated Gameplay: Developed a bot that plays cards randomly, demonstrating the core gameplay logic.

3. Core Gameplay Logic
   
        Card Programming: Implemented the logic for programming cards and activation, including damage cards.
   
        Upgrade Shop: Added the functionality for buying upgrades (Only 4 Upgrades have been implemented).
   
        Game Rules Enforcement: Ensured that all movements and actions adhere to the official RoboRally rules.
   
5. Board Deserialization

        Dynamic Boards: Implemented deserialization of boards, allowing individual boards to be connected via JSON and configuration files.

6. General Configuration & Cheats

        Configurable Settings: Created a general configuration system that allows users to enable cheats and log to files.
   
        Cheat Codes: Enabled cheats to manipulate player robots, adjust energy levels, and add specific damage card types.


## License

This project is licensed under the MIT License.

Feel free to fork this repository and adapt the client with a GUI/TUI and user controls. Additionally, you can expand the server with further upgrade cards and enhancements. I encourage any creative developments and improvements.

