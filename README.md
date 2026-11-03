# DailyRewards

A daily reward plugin for [Paper](https://papermc.io/) Minecraft servers. Players open a chest-style GUI with `/rewards`, claim a daily reward once every 24 hours, and their balance and claim time are stored in SQLite.

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Paper](https://img.shields.io/badge/PaperMC-API-blue.svg)
![Database](https://img.shields.io/badge/Database-SQLite-lightgrey.svg)
![Build](https://img.shields.io/badge/Gradle-Kotlin_DSL-green.svg)
![License](https://img.shields.io/badge/License-MIT-purple.svg)

## Screenshots

| Feature | Preview |
|---|---|
| Join reminder | ![Welcome message](screenshots/WelcomeMessage.png) |
| `/rewards` GUI | ![Plugin GUI](screenshots/PluginGUI.png) |
| Claim and balance | ![Reward claimed](screenshots/Reward.png) |
| Database update | ![Database update](screenshots/DatabaseUpdate.png) |

## Features

- **Custom GUI.** `/rewards` opens a 9-slot chest inventory with styled items and lore.
- **24-hour cooldown.** The last claim time is stored per player, and the remaining time is shown when a claim is too early.
- **Built-in balance.** Each claim adds $100 to the player's stored balance.
- **SQLite persistence.** UUID, balance and last claim time are saved in `plugins/DailyRewards/database.db`.
- **No external dependencies.** It uses the SQLite JDBC driver bundled with Paper, so no database server is needed.
- **Join listener.** Greets players on join and tells them if a reward is ready.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/rewards` | Opens the daily rewards GUI | All players |

## Database schema

Created automatically on startup:

```sql
CREATE TABLE IF NOT EXISTS rewards (
    uuid TEXT PRIMARY KEY,
    balance INTEGER NOT NULL DEFAULT 0,
    last_claim INTEGER NOT NULL DEFAULT 0
);
```

The cooldown is checked by comparing the stored `last_claim` timestamp to the current time.

## Build and run

```bash
./gradlew build        # jar is written to build/libs/DailyRewards-1.0.0.jar
./gradlew runServer    # starts a local Paper test server
```

Or copy the jar into an existing Paper server's `plugins/` folder and restart. The Java and Paper API versions are set in `build.gradle.kts`.

## Roadmap

- [ ] Configurable reward amount
- [ ] Integration with a Vault-compatible economy plugin
- [ ] Claim streaks

## Author

Sara Khan ([@SaraKhan-24](https://github.com/SaraKhan-24)). MIT licensed.
