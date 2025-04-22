# PayTime

A Minecraft plugin that allows players to change time and weather in exchange for in-game currency.

## Features

- Change time between day and night
- Change weather between clear and stormy
- Configurable prices for each change
- Permission-based access control
- Support for hex color codes in messages
- Broadcast messages for time/weather changes
- Feature toggles for different commands
- Debug mode for troubleshooting

## Commands

- `/paytime day` - Change time to day
- `/paytime night` - Change time to night
- `/paytime storm` - Change weather to stormy
- `/paytime clear` - Change weather to clear
- `/paytime price` - View current prices
- `/paytime reload` - Reload the configuration

## Permissions

- `paytime.day` - Allows changing time to day
- `paytime.night` - Allows changing time to night
- `paytime.storm` - Allows changing weather to stormy
- `paytime.clear` - Allows changing weather to clear
- `paytime.price` - Allows viewing prices
- `paytime.reload` - Allows reloading configuration
- `paytime.admin` - Gives access to all commands

## Configuration

The plugin uses a `config.yml` file with the following structure:

```yaml
# Price Settings
prices:
  day: 10000
  night: 1000
  storm: 10000
  clear: 1000

# Message Settings
messages:
  # Plugin prefix (supports hex color codes)
  prefix: "<#808080>[<#FFA500>PayTime<#808080>]<#FFFFFF>"
  
  # Currency symbol (leave blank for no symbol)
  currency_symbol: "$"
  
  # Broadcast settings
  broadcast:
    enabled: true
    time: "<#FFFF00>%player%<#FFFFFF> has paid to change the time to <#FFFF00>%time%<#FFFFFF>."
    weather: "<#FFFF00>%player%<#FFFFFF> has paid to change the weather to <#FFFF00>%weather%<#FFFFFF>."
  
  # Error messages
  errors:
    not_enough_money: "<#FF0000>You do not have enough money to change the time."
    invalid_permission: "<#FF0000>You do not have permission to execute this command."
    already_day: "<#FF0000>The time is already day."
    already_night: "<#FF0000>The time is already night."
    already_clear: "<#FF0000>The weather is already clear."
    already_stormy: "<#FF0000>The weather is already stormy."
  
  # Success messages
  success:
    time_changed: "<#00FF00>You have paid %price% to change the time to <#FFFF00>%time%<#FFFFFF>."
    weather_changed: "<#00FF00>You have paid %price% to change the weather to <#FFFF00>%weather%<#FFFFFF>."
    config_reload: "<#00FF00>Configuration reloaded successfully."

# Time Settings
time:
  # Time values for day and night (in ticks)
  day: 0
  night: 14000

# Weather Settings
weather:
  # Duration of weather changes (in ticks)
  # Set to -1 for permanent changes
  duration: -1

# Feature Settings
features:
  # Enable/disable specific features
  time_changes: true
  weather_changes: true
  price_command: true
  reload_command: true

# Debug Settings
debug:
  # Enable debug mode for troubleshooting
  enabled: false
  # Log level (INFO, WARNING, SEVERE)
  log_level: "INFO"
```

### Hex Color Codes

The plugin supports hex color codes in the format `<#FFFFFF>`. Here are some examples:
- `<#FFFFFF>` - White
- `<#FF0000>` - Red
- `<#00FF00>` - Green
- `<#0000FF>` - Blue
- `<#FFFF00>` - Yellow
- `<#FFA500>` - Orange
- `<#808080>` - Gray

### Placeholders

The following placeholders can be used in messages:
- `%player%` - Player's name
- `%time%` - Time of day (day/night)
- `%weather%` - Weather state (clear/stormy)
- `%price%` - Price paid for the change

## Dependencies

- [Vault](https://www.spigotmc.org/resources/vault.34315/) - Required for economy integration
- An economy plugin that works with Vault (e.g., EssentialsX, CMI, etc.)

## Installation

1. Download the latest version of PayTime
2. Place the jar file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin using the `config.yml` file
5. Make sure you have Vault and an economy plugin installed

## Support

If you encounter any issues or have questions, please:
1. Check the [documentation](https://www.spigotmc.org/resources/paytime.107012/)
2. Enable debug mode in the config for more detailed logs
3. Contact support on the Spigot resource page

## License

This project is licensed under the MIT License - see the LICENSE file for details.
