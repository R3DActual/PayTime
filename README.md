# PayTime

PayTime is a plugin for [SpigotMC](https://www.spigotmc.org/) that allows players to pay to change the time or weather in their world. With PayTime, players can use in-game currency to change the time to day or night or weather to storm or clear, providing a unique gameplay experience for your server.

## Features

- Players can pay to change the time to day or night using in-game currency
- Customizable prices and messages in the config.yml file
- Supports both regular Minecraft color codes and hex color codes in messages
- Command and permission system for controlling access to the plugin
- Easy to use and set up, with detailed installation instructions included

## Installation

1. Download the PayTime plugin from the [SpigotMC resource page](https://www.spigotmc.org/resources/paytime.107012/)
2. Install the plugin on your server by placing it in the `plugins` folder
3. Start or restart your server to complete the installation
4. Configure the plugin by editing the `config.yml` file in the `PayTime` folder
5. Restart the server or use the `/paytime reload` command to apply the changes
6. Players can now use the `/paytime` command to change the time or weather in their world

## Commands and Permissions

- `/time <day|night|storm|clear|price>` - Changes the weather or time (permission: `paytime.<day|night|storm|clear|price>`)
- `/paytime reload` - Reloads the config.yml file (permission: `paytime.reload`)

## Configuration

The `config.yml` file in the `PayTime` folder contains all the configuration options for the plugin. You can customize the prices for changing the time, as well as the messages that are displayed to players. Regular Minecraft color codes and hex color codes are supported in messages.

## Support

If you have any questions or issues with the PayTime plugin, please create a new issue on this GitHub repository or post in the [plugin thread](https://www.spigotmc.org/threads/paytime.107012/) on the SpigotMC forums or in our [Discord Server](https://discord.com/invite/sxCJHr34Yt).

## Contributing

If you would like to contribute to the PayTime plugin, please fork this repository and create a pull request with your changes.

## License

The PayTime plugin is licensed under the MIT License. See the [LICENSE](LICENSE.md) file for more information.
